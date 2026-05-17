package com.wu.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import com.wu.api.service.MtcnService;
import com.wu.excel.impl.ExcelFileUpdator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.wu.WUGWRuntime;
import com.wu.excel.impl.ExcelFileReader;
import com.wu.excel.impl.MTCNExcelDelegator;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.util.UtilFunctions;

/**
 * Console request handler for file-based MTCN batch generation.
 *
 * A @Component with prototype scope — Spring manages the full lifecycle
 * including @Value injection, @Autowired wiring, and @PostConstruct init.
 *
 * For the REST API path, use MtcnController / MtcnService directly.
 */
@Component
@Scope("singleton")
public class GenerateMTCNRequest extends AbstractServiceRequest {

    @Value("${GenMTCNRequest.serialNum}")
    private String requestIDValue;

    @Value("${GenMTCNRequest.description}")
    private String descriptionValue;

    @Value("${MTCN.input.folder:}")
    private String inputFolderPath;

    @Value("${MTCN.output.folder:}")
    private String outputFolderPath;

    @Value("${MTCN.standalone.input.folder:}")
    private String standaloneInputFolderPath;

    @Value("${MTCN.standalone.output.folder:}")
    private String standaloneOutputFolderPath;

    @Autowired
    private MtcnService mtcnService;

    public GenerateMTCNRequest() {
        super();
    }

    @PostConstruct
    private void init() {
        setRequestID(requestIDValue);
        setDescription(descriptionValue);
    }

    /**
     * Standalone loop: continuously scans the OneDrive/standalone input folder,
     * processes each Excel file, and moves it to the standalone output folder.
     * Runs on its own thread; sleeps 3 minutes between scans.
     */
    public void runServiceStandalone(WUGWRuntime wugwRuntime) {
        this.wugwRuntime = wugwRuntime;

        if (standaloneInputFolderPath == null || standaloneInputFolderPath.isBlank()) {
            UtilFunctions.mtcnLogger.info("MTCN.standalone.input.folder is not configured; standalone runner will not start.");
            return;
        }
        if (standaloneOutputFolderPath == null || standaloneOutputFolderPath.isBlank()) {
            UtilFunctions.mtcnLogger.info("MTCN.standalone.output.folder is not configured; standalone runner will not start.");
            return;
        }

        File inputFolder;
        try {
            inputFolder = ResourceUtils.getFile(standaloneInputFolderPath);
        } catch (FileNotFoundException e) {
            UtilFunctions.mtcnLogger.info("Standalone input folder not found: " + standaloneInputFolderPath + "; standalone runner will not start.");
            return;
        }

        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            UtilFunctions.mtcnLogger.info("Standalone input folder does not exist: " + standaloneInputFolderPath + "; standalone runner will not start.");
            return;
        }

        File standaloneOutputFolder = new File(standaloneOutputFolderPath);
        if (!standaloneOutputFolder.exists()) {
            standaloneOutputFolder.mkdirs();
        }

        while (true) {
            UtilFunctions.mtcnLogger.info("Scan MTCN request files in One-Drive input folder...");

            HashMap<String, File> dataFileMap = UtilFunctions.scanFiles(inputFolder);
            dataFileMap.forEach((fName, dataFile) -> {
                try {
                    execute(dataFile);
                    String destFilePath = standaloneOutputFolderPath + "/"
                            + UtilFunctions.getCurrentDate() + "-" + dataFile.getName();
                    UtilFunctions.moveFile(dataFile.getPath(), destFilePath);
                } catch (Exception e) {
                    UtilFunctions.mtcnLogger.info(e.getMessage());
                }
            });

            UtilFunctions.mtcnLogger.info("Done all the MTCN request files. Rescan later soon.");

            try {
                Thread.sleep(180000);
            } catch (InterruptedException e) {
                UtilFunctions.mtcnLogger.info(e.getMessage());
                return;
            }
        }
    }

    /**
     * Console-triggered run: scans the standard input folder once and processes
     * all waiting Excel files.
     */
    @Override
    public boolean doService() {
        if (inputFolderPath == null || inputFolderPath.isBlank()) {
            UtilFunctions.mtcnLogger.info("MTCN.input.folder is not configured; skipping batch file processing.");
            return true;
        }
        if (outputFolderPath == null || outputFolderPath.isBlank()) {
            UtilFunctions.mtcnLogger.info("MTCN.output.folder is not configured; skipping batch file processing.");
            return true;
        }

        File inputFolder = new File(inputFolderPath);
        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            UtilFunctions.mtcnLogger.info("MTCN input folder does not exist: " + inputFolderPath + "; skipping batch file processing.");
            return true;
        }

        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        HashMap<String, File> dataFileMap = UtilFunctions.scanFiles(inputFolder);
        dataFileMap.forEach((fName, dataFile) -> {
            try {
                execute(dataFile);
                String destFilePath = outputFolderPath + "/"
                        + UtilFunctions.getCurrentDate() + "-" + dataFile.getName();
                UtilFunctions.moveFile(dataFile.getPath(), destFilePath);
            } catch (Exception e) {
                UtilFunctions.mtcnLogger.info(e.getMessage());
            }
        });

        return true;
    }

    /**
     * Process a single Excel file: read all rows, delegate each to MtcnService,
     * then write results back and persist the file.
     */
    private void execute(File dataFile) throws Exception {
        MTCNExcelDelegator delegator = new MTCNExcelDelegator();
        List<Object> dataSet = ExcelFileReader.readFromExcel(dataFile.getPath(), delegator);

        for (Object sObject : dataSet) {
            ExcelDataEntry sData = (ExcelDataEntry) sObject;
            if (sData != null) {
                mtcnService.processDataEntry(sData);
            }
        }

        ExcelFileUpdator.updateToExcel(dataFile.getPath(), delegator, dataSet);
    }
}
