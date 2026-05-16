package com.wu.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wu.excel.impl.ExcelFileUpdator;
import com.wu.gw.model.PKJsonTxnObject;
import com.wu.gw.service.PKMTCNPushService;
import com.wu.api.model.ApiResponse;
import org.springframework.util.ResourceUtils;

import com.wu.WUGWRuntime;
import com.wu.excel.impl.ExcelFileReader;
import com.wu.excel.impl.MTCNExcelDelegator;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.GeneralName;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;

public class GenerateMTCNRequest extends AbstractServiceRequest {

        private Map<String, File> smvAISMap;
        private Map<String, File> smsAISMap;
        private Map<String, File> smvBISMap;
        private Map<String, File> smsBISMap;
        private Map<String, File> fiBISMap;

        private Map<String, File> searchAISMap;
        private Map<String, File> refundAISMap;

        private XMLAssemblerHandler xmlHandlerAIS;
        private XMLAssemblerHandler xmlHandlerBIS;

        public GenerateMTCNRequest() {
                super();
        }

        public void runServiceStandalone(WUGWRuntime wugwRuntime) {

                this.wugwRuntime = wugwRuntime;
                
                smvAISMap = wugwRuntime.getSmvAISMap();
                smsAISMap = wugwRuntime.getSmsAISMap();
                smvBISMap = wugwRuntime.getSmvBISMap();
                smsBISMap = wugwRuntime.getSmsBISMap();
                fiBISMap = wugwRuntime.getFiBISMap();
                searchAISMap = wugwRuntime.getSearchAISMap();
                refundAISMap = wugwRuntime.getRefundAISMap();

                xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
                xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();

                String inputFolderPath = "C:/Users/303172/OneDrive - Western Union/[TestingMTCN]/input";
                String outputFolderPath = "C:/Users/303172/OneDrive - Western Union/[TestingMTCN]/output";

                File inputFolder = null;
                try {
                        inputFolder = ResourceUtils.getFile(inputFolderPath);
                } catch (FileNotFoundException e1) {
                        UtilFunctions.loggingException(e1);
                        return;
                }

                while (true) {
                        UtilFunctions.mtcnLogger.info("Scan MTCN request files in One-Drive input folder...");

                        HashMap<String, File> dataFileMap = UtilFunctions.scanFiles(inputFolder);
                        dataFileMap.forEach((fName, dataFile) -> {
                                try {
                                        execute(dataFile);
                                        String destFilePath = outputFolderPath + "/" + UtilFunctions.getCurrentDate() + "-"
                                                        + dataFile.getName();
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

        @Override
        public boolean doService() {

                smvAISMap = wugwRuntime.getSmvAISMap();
                smsAISMap = wugwRuntime.getSmsAISMap();
                smvBISMap = wugwRuntime.getSmvBISMap();
                smsBISMap = wugwRuntime.getSmsBISMap();
                fiBISMap = wugwRuntime.getFiBISMap();
                searchAISMap = wugwRuntime.getSearchAISMap();
                refundAISMap = wugwRuntime.getRefundAISMap();

                xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
                xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();

                String inputFolderPath = wugwRuntime.getProperty("MTCN.input.folder");
                String outputFolderPath = wugwRuntime.getProperty("MTCN.output.folder");

                File inputFolder;
                try {
                        inputFolder = ResourceUtils.getFile(inputFolderPath);
                } catch (FileNotFoundException e1) {
                        UtilFunctions.loggingException(e1);
                        return true;
                }

                HashMap<String, File> dataFileMap = UtilFunctions.scanFiles(inputFolder);

                dataFileMap.forEach((fName, dataFile) -> {
                        try {
                                execute(dataFile);
                                String destFilePath = outputFolderPath + "/" + UtilFunctions.getCurrentDate() + "-"
                                                + dataFile.getName();
                                UtilFunctions.moveFile(dataFile.getPath(), destFilePath);
                        } catch (Exception e) {
                                UtilFunctions.mtcnLogger.info(e.getMessage());
                        }
                });

                return true;
        }

        private void execute(File dataFile) throws Exception {

                MTCNExcelDelegator delegator = new MTCNExcelDelegator();

                List<Object> dataSet = ExcelFileReader.readFromExcel(dataFile.getPath(), delegator);

                for (Object sObject : dataSet) {

                        ExcelDataEntry sData = (ExcelDataEntry) sObject;

                        if (sData != null) {

                                String serviceType = sData.getServiceType();

                                if (serviceType.equalsIgnoreCase("Send")) {

                                        if (sData.getMTCN() != null && !sData.getMTCN().trim().equals("")) {

                                                UtilFunctions.mtcnLogger.info("MTCN already created");

                                                continue;
                                        }

                                        this.sendTransaction(sData, false);

                                } else if (serviceType.equalsIgnoreCase("RefundAll")) {

                                        if (// sData.getMTCN() == null
                                                // || sData.getMTCN().trim().equals("") ||
                                        sData.getAmount() == null || sData.getAmount().trim().equals("") || sData.getFee() == null
                                                        || sData.getFee().trim().equals("")) {
                                                // ||(sData.getRefundMTCN() != null &&
                                                // !sData.getRefundMTCN().trim().equals(""))) {

                                                continue;
                                        }

                                        GeneralName senderName = sData.getSenderName();
                                        GeneralName receiverName = sData.getReceiverName();
                                        sData.setReceiverName(senderName);
                                        sData.setSenderName(receiverName);

                                        CountryCurrencyInfo sCountryCurrency = sData.getSendCountryCurrency();
                                        CountryCurrencyInfo pCountryCurrency = sData.getReceiveCountryCurrency();
                                        sData.setSendCountryCurrency(pCountryCurrency);
                                        sData.setReceiveCountryCurrency(sCountryCurrency);

                                        String amount = sData.getAmount();
                                        String feeCharge = sData.getFee();
                                        float newAmount = Float.valueOf(amount) + Float.valueOf(feeCharge);
                                        sData.setTransactionType("WMF");

                                        sData.setAmount(String.format("%.2f", newAmount));

                                        this.sendTransaction(sData, true);

                                        sData.setAmount(amount);
                                        sData.setFee(feeCharge);

                                } else if (serviceType.equalsIgnoreCase("Refund")) {

                                        if (sData.getMTCN() == null || sData.getMTCN().trim().equals("")
                                                        || (sData.getRefundMTCN() != null && !sData.getRefundMTCN().trim().equals(""))) {

                                                continue;
                                        }

                                        this.refundTransaction(sData);

                                } else if (serviceType.equalsIgnoreCase("PKPush")) {
                                        if (sData.getMTCN() == null || sData.getMTCN().trim().equals("")) {
                                                UtilFunctions.mtcnLogger.info("MTCN is empty");
                                                continue;
                                        }
                                        pushMTCNForPK(sData);
                                }
                        }
                }

                ExcelFileUpdator.updateToExcel(dataFile.getPath(), delegator, dataSet);
        }

        private void refundTransaction(ExcelDataEntry sData) throws IOException {

                String naid = sData.getNaid();
                String serviceURL = wugwRuntime.getProperty("PI.domain");

                if (searchAISMap.containsKey(naid) && refundAISMap.containsKey(naid)) {

                        File searchFile = searchAISMap.get(naid);
                        File refundFile = refundAISMap.get(naid);

                        String searchXMLStr = UtilFunctions.readFileToString(searchFile);
                        String refundXMLStr = UtilFunctions.readFileToString(refundFile);

                        searchXMLStr = GWMessageAssembleUtils.assembleAISSearchRequestMsg(xmlHandlerAIS, searchXMLStr, sData);
                        String searchReplyStr = UtilFunctions.requestGatewayService(serviceURL, searchXMLStr);

                        if (!searchReplyStr.startsWith("error - ")) {

                                refundXMLStr = GWMessageAssembleUtils.assembleAISRefundRequestMsg(xmlHandlerAIS, refundXMLStr,
                                                searchReplyStr);
                                String refundReplyStr = UtilFunctions.requestGatewayService(serviceURL, refundXMLStr);

                                if (!refundReplyStr.startsWith("error - ")) {

                                        String replyMTCN = UtilFunctions.retrieveXMLValue(refundReplyStr, "mtcn");
                                        UtilFunctions.mtcnLogger.info("MTCN Refund - " + replyMTCN);
                                        sData.setRefundMTCN(replyMTCN);

                                } else {

                                        UtilFunctions.mtcnLogger.info("AIS Refund - " + refundReplyStr);
                                        sData.setMTCN("AIS Refund " + refundReplyStr);
                                }

                        } else {

                                UtilFunctions.mtcnLogger.info("AIS Refund Search - " + searchReplyStr);
                                sData.setMTCN("AIS Refund Search " + searchReplyStr);
                        }

                } else {

                        System.out.println("Search or Refund samples does not exist : " + naid);
                }
        }

        private void sendTransaction(ExcelDataEntry sData, boolean isRefund) throws IOException {

                String sCountryCode = sData.getSendCountryCurrency().getIsoCode().getCountryCode();
                String serviceURL = wugwRuntime.getProperty("PI.domain");

                if (smvAISMap.containsKey(sCountryCode) && smsAISMap.containsKey(sCountryCode)) {

                        File smvFile = smvAISMap.get(sCountryCode);
                        File smsFile = smsAISMap.get(sCountryCode);

                        String smvXMLStr = UtilFunctions.readFileToString(smvFile);
                        String smsXMLStr = UtilFunctions.readFileToString(smsFile);

                        smvXMLStr = GWMessageAssembleUtils.assembleAISSMVRequestMsg(xmlHandlerAIS, smvXMLStr, sData);
                        String smvReplyStr = UtilFunctions.requestGatewayService(serviceURL, smvXMLStr);

                        if (!smvReplyStr.startsWith("error - ")) {

                                smsXMLStr = GWMessageAssembleUtils.assembleAISSMSRequestMsg(xmlHandlerAIS, smsXMLStr, smvXMLStr,
                                                smvReplyStr);
                                String smsReplyStr = UtilFunctions.requestGatewayService(serviceURL, smsXMLStr);

                                if (!smsReplyStr.startsWith("error - ")) {

                                        String replyMTCN = UtilFunctions.retrieveXMLValue(smsReplyStr, "mtcn");

                                        String feeCharge = UtilFunctions.retrieveXMLValue(smsReplyStr, "charges");

                                        UtilFunctions.mtcnLogger.info("MTCN - " + replyMTCN);

                                        // if(isRefund) {

                                        // sData.setRefundMTCN(replyMTCN);

                                        // } else {

                                        sData.setMTCN(replyMTCN);
                                        // }

                                        sData.setFee(UtilFunctions.convertToNatureAmount(feeCharge));

                                } else {

                                        UtilFunctions.mtcnLogger.info("AIS SMS - " + smsReplyStr);

                                        sData.setMTCN("AIS SMS " + smsReplyStr);
                                }

                        } else {

                                UtilFunctions.mtcnLogger.info("AIS SMV - " + smvReplyStr);

                                sData.setMTCN("AIS SMV " + smvReplyStr);
                        }

                } else if (fiBISMap.containsKey(sCountryCode) && smvBISMap.containsKey(sCountryCode)
                                && smsBISMap.containsKey(sCountryCode)) {

                        File fiFile = fiBISMap.get(sCountryCode);
                        String fiXMLStr = UtilFunctions.readFileToString(fiFile);

                        fiXMLStr = GWMessageAssembleUtils.assembleBISFIRequestMsg(xmlHandlerBIS, fiXMLStr, sData);
                        String fiReplyStr = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);

                        if (!fiReplyStr.startsWith("error - ")) {

                                File smvFile = smvBISMap.get(sCountryCode);
                                File smsFile = smsBISMap.get(sCountryCode);

                                String smvXMLStr = UtilFunctions.readFileToString(smvFile);
                                String smsXMLStr = UtilFunctions.readFileToString(smsFile);

                                smvXMLStr = GWMessageAssembleUtils.assembleBISSMVRequestMsg(xmlHandlerBIS, smvXMLStr, sData,
                                                fiReplyStr);
                                String smvReplyStr = UtilFunctions.requestGatewayService(serviceURL, smvXMLStr);

                                if (!smvReplyStr.startsWith("error - ")) {

                                        smsXMLStr = GWMessageAssembleUtils.assembleBISSMSRequestMsg(xmlHandlerBIS, smsXMLStr, smvXMLStr,
                                                        smvReplyStr);
                                        String smsReplyStr = UtilFunctions.requestGatewayService(serviceURL, smsXMLStr);

                                        if (!smsReplyStr.startsWith("error - ")) {
                                                String replyMTCN = UtilFunctions.retrieveXMLValue(smsReplyStr, "mtcn");
                                                UtilFunctions.mtcnLogger.info("MTCN - " + replyMTCN);
                                                sData.setMTCN(replyMTCN);
                                        } else {
                                                UtilFunctions.mtcnLogger.info("BIS SMS - " + smsReplyStr);
                                                sData.setMTCN("BIS SMS " + smsReplyStr);
                                        }
                                } else {
                                        UtilFunctions.mtcnLogger.info("BIS SMV - " + smvReplyStr);
                                        sData.setMTCN("BIS SMV " + smvReplyStr);
                                }
                        } else {
                                UtilFunctions.mtcnLogger.info("BIS FI - " + fiReplyStr);
                                sData.setMTCN("BIS FI " + fiReplyStr);
                        }
                } else {
                        System.out.println("SMV or SMS samples does not exist : " + sCountryCode);
                        sData.setMTCN("The sending country/currency is not available for testing");
                }

                //pushMTCNForPK(sData);
        }

        private PKMTCNPushService pushService = new PKMTCNPushService();

        private void pushMTCNForPK(ExcelDataEntry sData) {
                PKJsonTxnObject txnObject = new PKJsonTxnObject();
                txnObject.getTransaction().setMgiTransactionId(sData.getMTCN());
                txnObject.getTransaction().getReceiveAmount().setValue(sData.getAmount());
                //txnObject.getTransaction().setSendCountryCode(sData.getSendCountryCurrency().getIsoCode().getCountryCode());
                txnObject.getTransaction().getSender().getPerson().setFirstName(sData.getSenderName().getFirstName());
                txnObject.getTransaction().getSender().getPerson().setLastName(sData.getSenderName().getLastName());
                txnObject.getTransaction().getSender().getPerson().setMiddleName(sData.getSenderName().getMiddleName());
                txnObject.getTransaction().getReceiver().getPerson().setFirstName(sData.getReceiverName().getFirstName());
                txnObject.getTransaction().getReceiver().getPerson().setLastName(sData.getReceiverName().getLastName());
                txnObject.getTransaction().getReceiver().getPerson().setMiddleName(sData.getReceiverName().getMiddleName());

                ApiResponse<?> respBean = pushService.pushMTCN(txnObject);
                System.out.println("respBean = " + respBean);
        }
}
