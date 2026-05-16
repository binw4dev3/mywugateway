package com.wu.api.controller;

import com.wu.api.model.ApiResponse;
import com.wu.api.service.MtcnApiService;
import com.wu.gw.util.UtilFunctions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST endpoints for low-level gateway operations:
 * - Raw XML pass-through (for custom requests not covered by higher-level endpoints)
 * - MTCN batch generation trigger
 */
@RestController
@RequestMapping("/api/v1/gateway")
@Tag(name = "Gateway", description = "Low-level gateway pass-through and MTCN batch operations")
public class GatewayController {

    @Autowired
    private MtcnApiService mtcnApiService;

    @PostMapping("/request")
    @Operation(
        summary = "Raw Gateway Request",
        description = """
            Sends an arbitrary SOAP XML message to a WU gateway endpoint and returns the raw XML reply.
            Use this for custom requests not covered by the higher-level DAS or APN endpoints.
            The caller is responsible for assembling a valid SOAP request XML string.
            
            Request body fields:
            - **gwURL** (required): The full WU gateway endpoint URL
            - **requestMsg** (required): The complete SOAP XML request string""")
    public ApiResponse<String> rawGatewayRequest(
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> body) {
        try {
            String gwURL = (String) body.get("gwURL");
            String requestMsg = (String) body.get("requestMsg");

            if (gwURL == null || gwURL.isBlank()) {
                return ApiResponse.error("Missing required field: gwURL");
            }
            if (requestMsg == null || requestMsg.isBlank()) {
                return ApiResponse.error("Missing required field: requestMsg");
            }

            String reply = UtilFunctions.requestGatewayService(gwURL, requestMsg);

            if (reply.startsWith("error - ")) {
                return ApiResponse.error("Gateway returned an error.", reply);
            }
            return ApiResponse.ok("Gateway request completed.", reply);
        } catch (Exception e) {
            return ApiResponse.error("Failed to execute gateway request: " + e.getMessage());
        }
    }

    @PostMapping("/mtcn/trigger")
    @Operation(
        summary = "Trigger MTCN Batch Generation",
        description = """
            Triggers a single run of the MTCN batch processor. The processor scans the
            configured input folder (MTCN.input.folder in GWRuntimeProps.properties),
            processes each Excel file row by row (Send / Refund / PKPush service types),
            writes the generated MTCNs back into the file, then moves it to the output folder
            (MTCN.output.folder).
            
            This runs synchronously. The response confirms the trigger completed; check the
            output folder for results.""")
    public ApiResponse<String> triggerMtcnBatch() {
        try {
            mtcnApiService.triggerBatch();
            return ApiResponse.ok("MTCN batch run triggered. Check the configured output folder for results.");
        } catch (Exception e) {
            return ApiResponse.error("MTCN batch trigger failed: " + e.getMessage());
        }
    }
}
