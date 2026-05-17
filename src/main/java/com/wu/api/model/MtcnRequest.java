package com.wu.api.model;

/**
 * Request DTO for MTCN generation via REST API.
 *
 * Mandatory fields : serviceType, sendCountry, receiveCountry, transactionType
 * Optional fields  : senderName, receiverName, senderAddress, amount
 *
 * Amount rule (conditional mandatory):
 *   - Provide 'amount' directly, OR
 *   - Provide both 'minAmount' and 'maxAmount' for random generation within that range.
 *   At least one of these two options is required.
 *
 * Auto-generation:
 *   - senderName / receiverName: generated randomly when absent.
 *   - senderAddress: defaults to "testing sender address" when absent.
 */
public class MtcnRequest {

    // ── Mandatory ─────────────────────────────────────────────────────────────

    /** "Send" | "Refund" | "RefundAll" | "PKPush" */
    private String serviceType;

    /** Sending country and currency codes. */
    private CountryRequest sendCountry;

    /** Receiving country and currency codes. */
    private CountryRequest receiveCountry;

    /** Transaction type: "WMF" (will-call / money in minutes) or "WMN" (next-day). */
    private String transactionType;

    // ── Optional ──────────────────────────────────────────────────────────────

    /** Sender name; auto-generated when absent. */
    private NameRequest senderName;

    /** Receiver name; auto-generated when absent. */
    private NameRequest receiverName;

    /** Sender address; defaults to "testing sender address" when absent. */
    private String senderAddress;

    // ── Conditional mandatory (amount OR min+max) ─────────────────────────────

    /** Fixed transaction amount. Required unless minAmount + maxAmount are supplied. */
    private String amount;

    /** Lower bound for random amount generation (inclusive). */
    private String minAmount;

    /** Upper bound for random amount generation (inclusive). */
    private String maxAmount;

    // ── Conditional (Refund / PKPush service types) ────────────────────────────

    /** Partner/agent identifier. Required for Refund service types. */
    private String naid;

    /** Existing MTCN. Required for "Refund" and "PKPush" service types. */
    private String mtcn;

    // ── Nested types ──────────────────────────────────────────────────────────

    public static class NameRequest {
        private String firstName;
        private String middleName;
        private String lastName;

        public String getFirstName()  { return firstName; }
        public void setFirstName(String firstName)   { this.firstName = firstName; }
        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }
        public String getLastName()   { return lastName; }
        public void setLastName(String lastName)     { this.lastName = lastName; }
    }

    public static class CountryRequest {
        private String countryCode;
        private String currencyCode;

        public String getCountryCode()  { return countryCode; }
        public void setCountryCode(String countryCode)   { this.countryCode = countryCode; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getServiceType()  { return serviceType; }
    public void setServiceType(String serviceType)   { this.serviceType = serviceType; }

    public CountryRequest getSendCountry()  { return sendCountry; }
    public void setSendCountry(CountryRequest sendCountry)   { this.sendCountry = sendCountry; }

    public CountryRequest getReceiveCountry()  { return receiveCountry; }
    public void setReceiveCountry(CountryRequest receiveCountry) { this.receiveCountry = receiveCountry; }

    public String getTransactionType()  { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public NameRequest getSenderName()  { return senderName; }
    public void setSenderName(NameRequest senderName)   { this.senderName = senderName; }

    public NameRequest getReceiverName()  { return receiverName; }
    public void setReceiverName(NameRequest receiverName) { this.receiverName = receiverName; }

    public String getSenderAddress()  { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }

    public String getAmount()    { return amount; }
    public void setAmount(String amount)       { this.amount = amount; }

    public String getMinAmount() { return minAmount; }
    public void setMinAmount(String minAmount) { this.minAmount = minAmount; }

    public String getMaxAmount() { return maxAmount; }
    public void setMaxAmount(String maxAmount) { this.maxAmount = maxAmount; }

    public String getNaid()  { return naid; }
    public void setNaid(String naid) { this.naid = naid; }

    public String getMtcn()  { return mtcn; }
    public void setMtcn(String mtcn) { this.mtcn = mtcn; }
}
