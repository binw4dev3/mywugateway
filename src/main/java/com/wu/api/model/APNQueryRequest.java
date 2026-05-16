package com.wu.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for APN operations that additionally require a product code.
 */
@Schema(description = "Parameters for an APN-level query (extends DAS with product code)")
public class APNQueryRequest extends DASQueryRequest {

    @Schema(description = "WU product code", allowableValues = {"D2C", "D2B", "SVC"}, example = "D2B", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productCode;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
}
