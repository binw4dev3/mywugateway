package com.wu.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for all DAS-based single-step query operations.
 */
@Schema(description = "Parameters for a DAS query against the WU gateway")
public class DASQueryRequest {

    @Schema(description = "Target WU environment", allowableValues = {"PI", "PROD"}, example = "PI", requiredMode = Schema.RequiredMode.REQUIRED)
    private String env;

    @Schema(description = "ISO country code of the originating partner", example = "TW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String countryCode;

    @Schema(description = "Network Agent ID (NAID) of the partner", example = "TWD00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String naid;

    public String getEnv() { return env; }
    public void setEnv(String env) { this.env = env; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getNaid() { return naid; }
    public void setNaid(String naid) { this.naid = naid; }
}
