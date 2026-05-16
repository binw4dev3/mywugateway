package com.wu.api.controller;

import com.wu.api.model.APNQueryRequest;
import com.wu.api.model.ApiResponse;
import com.wu.api.service.APNDataScopeApiService;
import com.wu.api.service.APNDynamicApiService;
import com.wu.gw.model.APNTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for APN (Agent Partner Network) template operations.
 * Both endpoints require env, countryCode, naid, and productCode.
 */
@RestController
@RequestMapping("/api/v1/apn")
@Tag(name = "APN Templates", description = "Agent Partner Network — dynamic template and data-scope retrieval")
public class APNController {

    @Autowired
    private APNDynamicApiService apnDynamicApiService;

    @Autowired
    private APNDataScopeApiService apnDataScopeApiService;

    @PostMapping("/dynamic-template")
    @Operation(
        summary = "Get APN Dynamic Template",
        description = """
            Performs a multi-step DAS query to build the full APN dynamic template for a partner:
            1. GetCountriesCurrencies — lists all available corridors
            2. GetDeliveryServices — lists delivery services per corridor
            3. GetDeliveryOptionTemplate — retrieves data field definitions per service
            4. GetCascadeList — fetches options for cascade (Type-II) dropdown fields
            Returns a list of APNTemplate objects, one per corridor/service combination.""")
    public ApiResponse<List<APNTemplate>> getDynamicTemplate(@RequestBody APNQueryRequest request) {
        try {
            List<APNTemplate> templates = apnDynamicApiService.execute(
                    request.getEnv(), request.getCountryCode(),
                    request.getNaid(), request.getProductCode());
            return ApiResponse.ok("APN dynamic template retrieved successfully. Templates found: " + templates.size(), templates);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve APN dynamic template: " + e.getMessage());
        }
    }

    @PostMapping("/data-scope")
    @Operation(
        summary = "Get APN Data Scope",
        description = """
            Similar to dynamic-template but lighter — performs Steps 1–3 only (no cascade list fetching).
            Returns field-level metadata (name, type, max length, required flag, cascade list name)
            without downloading the full dropdown option values.
            Use this when you need to understand the data schema without the complete dataset.""")
    public ApiResponse<List<APNTemplate>> getDataScope(@RequestBody APNQueryRequest request) {
        try {
            List<APNTemplate> templates = apnDataScopeApiService.execute(
                    request.getEnv(), request.getCountryCode(),
                    request.getNaid(), request.getProductCode());
            return ApiResponse.ok("APN data scope retrieved successfully. Templates found: " + templates.size(), templates);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve APN data scope: " + e.getMessage());
        }
    }
}
