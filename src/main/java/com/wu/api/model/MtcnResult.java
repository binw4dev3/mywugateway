package com.wu.api.model;

/**
 * Response DTO returned for each MTCN generation request.
 * Echoes back all resolved input fields (including auto-generated names
 * and the actual amount used) together with gateway outputs.
 */
public class MtcnResult {

    private String serviceType;
    private CountryInfo sendCountry;
    private CountryInfo receiveCountry;
    private NameInfo senderName;
    private NameInfo receiverName;
    private String senderAddress;
    private String amount;
    private String mtcn;
    private String refundMtcn;
    private String fee;
    private String fxRate;
    private boolean success;
    private String message;

    // ── Nested types ──────────────────────────────────────────────────────────

    public static class NameInfo {
        private String firstName;
        private String middleName;
        private String lastName;

        public NameInfo() {}
        public NameInfo(String firstName, String middleName, String lastName) {
            this.firstName  = firstName;
            this.middleName = middleName;
            this.lastName   = lastName;
        }

        public String getFirstName()  { return firstName; }
        public void setFirstName(String firstName)   { this.firstName = firstName; }
        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }
        public String getLastName()   { return lastName; }
        public void setLastName(String lastName)     { this.lastName = lastName; }
    }

    public static class CountryInfo {
        private String countryCode;
        private String currencyCode;

        public CountryInfo() {}
        public CountryInfo(String countryCode, String currencyCode) {
            this.countryCode  = countryCode;
            this.currencyCode = currencyCode;
        }

        public String getCountryCode()  { return countryCode; }
        public void setCountryCode(String countryCode)   { this.countryCode = countryCode; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getServiceType()    { return serviceType; }
    public void setServiceType(String serviceType)     { this.serviceType = serviceType; }

    public CountryInfo getSendCountry()    { return sendCountry; }
    public void setSendCountry(CountryInfo sendCountry)     { this.sendCountry = sendCountry; }

    public CountryInfo getReceiveCountry()    { return receiveCountry; }
    public void setReceiveCountry(CountryInfo receiveCountry) { this.receiveCountry = receiveCountry; }

    public NameInfo getSenderName()    { return senderName; }
    public void setSenderName(NameInfo senderName)     { this.senderName = senderName; }

    public NameInfo getReceiverName()    { return receiverName; }
    public void setReceiverName(NameInfo receiverName)   { this.receiverName = receiverName; }

    public String getSenderAddress()  { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }

    public String getAmount()  { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getMtcn()       { return mtcn; }
    public void setMtcn(String mtcn)        { this.mtcn = mtcn; }

    public String getRefundMtcn() { return refundMtcn; }
    public void setRefundMtcn(String refundMtcn) { this.refundMtcn = refundMtcn; }

    public String getFee()        { return fee; }
    public void setFee(String fee)       { this.fee = fee; }

    public String getFxRate()  { return fxRate; }
    public void setFxRate(String fxRate) { this.fxRate = fxRate; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
