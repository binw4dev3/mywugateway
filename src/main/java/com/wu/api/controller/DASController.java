package com.wu.api.controller;

import com.wu.api.model.ApiResponse;
import com.wu.api.model.DASQueryRequest;
import com.wu.api.service.DASApiService;
import com.wu.gw.model.das.DASQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for single-step DAS query operations.
 * Each endpoint accepts env + countryCode + naid, calls the WU gateway,
 * and returns a structured record set.
 */
@RestController
@RequestMapping("/api/v1/das")
@Tag(name = "DAS Queries", description = "Direct Access Services — single-step lookup operations against the WU gateway")
public class DASController {

    @Autowired
    private DASApiService dasApiService;

    @PostMapping("/countries-currencies")
    @Operation(
        summary = "Get Countries and Currencies",
        description = "Queries the WU DAS GetCountriesCurrencies service. Returns a paginated-and-merged list of all destination country/currency pairs available for the given partner NAID.")
    public ApiResponse<DASQueryResult> getCountriesCurrencies(@RequestBody DASQueryRequest request) {
        try {
            DASQueryResult result = dasApiService.queryCountriesCurrencies(
                    request.getEnv(), request.getCountryCode(), request.getNaid());
            return ApiResponse.ok("Countries and currencies retrieved successfully.", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve countries and currencies: " + e.getMessage());
        }
    }

    @PostMapping("/us-states")
    @Operation(
        summary = "Get US State List",
        description = "Queries the WU DAS GetUSStateList service. Returns all US state codes and names supported for the given partner.")
    public ApiResponse<DASQueryResult> getUSStates(@RequestBody DASQueryRequest request) {
        try {
            DASQueryResult result = dasApiService.queryUSStates(
                    request.getEnv(), request.getCountryCode(), request.getNaid());
            return ApiResponse.ok("US state list retrieved successfully.", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve US state list: " + e.getMessage());
        }
    }

    @PostMapping("/mexico-city-state")
    @Operation(
        summary = "Get Mexico City and State List",
        description = "Queries the WU DAS GetMexicoCityState service. Returns Mexican city/state data for the given partner.")
    public ApiResponse<DASQueryResult> getMexicoCityState(@RequestBody DASQueryRequest request) {
        try {
            DASQueryResult result = dasApiService.queryMexicoCityState(
                    request.getEnv(), request.getCountryCode(), request.getNaid());
            return ApiResponse.ok("Mexico city/state list retrieved successfully.", result);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve Mexico city/state list: " + e.getMessage());
        }
    }
}
