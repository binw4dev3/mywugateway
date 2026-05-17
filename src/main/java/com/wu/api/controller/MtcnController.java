package com.wu.api.controller;

import com.wu.api.model.ApiResponse;
import com.wu.api.model.MtcnRequest;
import com.wu.api.model.MtcnResult;
import com.wu.api.service.MtcnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for MTCN generation driven by request data from the client.
 *
 * Two endpoints are provided:
 *   POST /api/v1/mtcn/generate        — single transaction request
 *   POST /api/v1/mtcn/generate/batch  — list of transaction requests
 *
 * Mandatory fields in each MtcnRequest: serviceType, sendCountry, receiveCountry, transactionType.
 * Optional: senderName, receiverName (auto-generated if absent), senderAddress (defaults to
 * "testing sender address"), and either amount or minAmount+maxAmount (for random generation).
 */
@RestController
@RequestMapping("/api/v1/mtcn")
@Tag(name = "MTCN", description = "Generate MTCN transactions via client-supplied request data")
public class MtcnController {

    @Autowired
    private MtcnService mtcnService;

    @PostMapping("/generate")
    @Operation(
        summary = "Generate MTCN – single request",
        description = """
            Accepts one transaction request and calls the WU gateway to generate an MTCN.

            **Mandatory fields**: serviceType, sendCountry (countryCode + currencyCode),
            receiveCountry (countryCode + currencyCode), transactionType (WMF or WMN).

            **Optional fields**:
            - senderName / receiverName – auto-generated randomly when absent.
            - senderAddress – defaults to "testing sender address" when absent.
            - amount – fixed amount; OR provide minAmount + maxAmount for a random value
              within that range (one option is required).

            The response echoes back all resolved fields (including any auto-generated names
            and the actual amount used) together with the resulting MTCN and fee.""")
    public ApiResponse<MtcnResult> generateOne(@RequestBody MtcnRequest request) {
        try {
            MtcnResult result = mtcnService.process(request);
            return result.isSuccess()
                    ? ApiResponse.ok("MTCN generated successfully.", result)
                    : ApiResponse.error(result.getMessage(), result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to process MTCN request: " + e.getMessage());
        }
    }

    @PostMapping("/generate/batch")
    @Operation(
        summary = "Generate MTCN – batch request",
        description = """
            Accepts a list of transaction requests and processes each one independently.
            Failures in individual entries do not stop the rest of the batch.

            Each entry in the list follows the same field rules as the single-request endpoint.
            The response list preserves the same order as the input list.
            Check the 'success' flag on each result to identify any that failed.""")
    public ApiResponse<List<MtcnResult>> generateBatch(@RequestBody List<MtcnRequest> requests) {
        try {
            if (requests == null || requests.isEmpty()) {
                return ApiResponse.error("Request list must not be empty.");
            }
            List<MtcnResult> results = mtcnService.processBatch(requests);
            long failCount = results.stream().filter(r -> !r.isSuccess()).count();
            String message = failCount == 0
                    ? "All " + results.size() + " MTCN(s) generated successfully."
                    : results.size() + " request(s) processed; " + failCount + " failed.";
            return ApiResponse.ok(message, results);
        } catch (Exception e) {
            return ApiResponse.error("Failed to process batch: " + e.getMessage());
        }
    }
}
