package com.wu.api.service;

import com.wu.WUGWRuntime;
import com.wu.console.GenerateMTCNRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * API service for triggering the MTCN batch generation process.
 *
 * The MTCN generator reads Excel files from a configured input folder
 * (property: MTCN.input.folder), processes each row (Send / Refund / PKPush),
 * and writes results back to the same file, then moves it to the output folder
 * (property: MTCN.output.folder). Both paths are set in GWRuntimeProps.properties.
 *
 * This endpoint triggers the batch processing of any files currently waiting
 * in the input folder. It runs synchronously and returns the number of
 * files processed.
 */
@Service
public class MtcnApiService {

    @Autowired
    private WUGWRuntime wugwRuntime;

    @Autowired
    private GenerateMTCNRequest generateMTCNRequest;

    /**
     * Trigger a single batch run of the MTCN generator.
     * @return count of Excel files processed in this run.
     */
    public int triggerBatch() {
        generateMTCNRequest.serve(wugwRuntime);
        // The actual file count is not exposed by GenerateMTCNRequest;
        // return -1 to signal "ran successfully, check output folder for results".
        return -1;
    }
}
