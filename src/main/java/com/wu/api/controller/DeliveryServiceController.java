package com.wu.api.controller;

import com.wu.api.model.ApiResponse;
import com.wu.api.model.DASQueryRequest;
import com.wu.api.service.DeliveryServiceApiService;
import com.wu.gw.model.APNTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoint for delivery service list discovery.
 * Returns available corridors with their delivery service codes,
 * template IDs, and fee-inquiry availability status.
 */
@RestController
@RequestMapping("/api/v1/delivery")
@Tag(name = "Delivery Services", description = "Discover available delivery services and corridors for a partner")
public class DeliveryServiceController {

    @Autowired
    private DeliveryServiceApiService deliveryServiceApiService;

    @PostMapping("/service-list")
    @Operation(
        summary = "Get Delivery Service List",
        description = """
            Multi-step query that:
            1. GetCountriesCurrencies — enumerates all destination corridors
            2. GetDeliveryServices (all products) — lists delivery services per corridor
            3. FeeInquiry (AIS or BIS) — checks live service availability per entry
            Returns a flat list of APNTemplate entries, each tagged with 'Available'
            or the gateway error reason for unavailability.""")
    public ApiResponse<List<APNTemplate>> getDeliveryServiceList(@RequestBody DASQueryRequest request) {
        try {
            List<APNTemplate> services = deliveryServiceApiService.execute(
                    request.getEnv(), request.getCountryCode(), request.getNaid());
            return ApiResponse.ok("Delivery service list retrieved. Entries: " + services.size(), services);
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve delivery service list: " + e.getMessage());
        }
    }
}
