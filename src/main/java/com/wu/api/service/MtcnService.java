package com.wu.api.service;

import com.wu.WUGWRuntime;
import com.wu.api.model.MtcnRequest;
import com.wu.api.model.MtcnResult;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.PKJsonTxnObject;
import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.GeneralName;
import com.wu.gw.model.ais.IsoCode;
import com.wu.gw.model.ais.NameType;
import com.wu.gw.service.PKMTCNPushService;
import com.wu.gw.util.UtilFunctions;
import com.wu.web.model.RespBean;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Core MTCN transaction service.
 *
 * Owns the gateway interaction logic (send, refund, PKPush) extracted from
 * GenerateMTCNRequest so it can be reached by both:
 *   - the REST API (MtcnController)  via process() / processBatch()
 *   - the file-based console flow    via processDataEntry()
 */
@Service
public class MtcnService {

    private static final String DEFAULT_SENDER_ADDRESS = "testing sender address";

    private static final String[] TEST_FIRST_NAMES = {
        "James", "Mary", "Robert", "Patricia", "John",
        "Jennifer", "Michael", "Linda", "David", "Barbara"
    };
    private static final String[] TEST_LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones",
        "Garcia", "Miller", "Davis", "Wilson", "Taylor"
    };

    private final Random random = new Random();
    private final PKMTCNPushService pkPushService = new PKMTCNPushService();

    @Lazy
    @Autowired
    private WUGWRuntime wugwRuntime;

    // ── Public API entry points ───────────────────────────────────────────────

    /**
     * Process a single MTCN request from the REST API.
     * Validates input, resolves optional fields, calls the gateway, and
     * returns a fully populated MtcnResult.
     */
    public MtcnResult process(MtcnRequest request) {
        String validationError = validate(request);
        if (validationError != null) {
            return failResult(request, validationError);
        }

        ExcelDataEntry data = toDataEntry(request);
        try {
            processDataEntry(data);
        } catch (IOException e) {
            MtcnResult result = buildResult(request, data);
            result.setSuccess(false);
            result.setMessage("Gateway communication error: " + e.getMessage());
            return result;
        }
        return buildResult(request, data);
    }

    /**
     * Process a list of MTCN requests from the REST API.
     * Each entry is processed independently; failures in one do not stop others.
     */
    public List<MtcnResult> processBatch(List<MtcnRequest> requests) {
        return requests.stream()
                .map(this::process)
                .collect(Collectors.toList());
    }

    /**
     * Process an ExcelDataEntry in place (file-based console flow).
     * Reads serviceType from the entry, calls the appropriate gateway method,
     * and writes MTCN / fee / refundMTCN back to the same object.
     */
    public void processDataEntry(ExcelDataEntry sData) throws IOException {
        if (sData == null) return;

        String serviceType = sData.getServiceType();
        if (serviceType == null) return;

        if (serviceType.equalsIgnoreCase("Send")) {
            if (sData.getMTCN() != null && !sData.getMTCN().trim().isEmpty()) {
                UtilFunctions.mtcnLogger.info("MTCN already created");
                return;
            }
            sendTransaction(sData, false);

        } else if (serviceType.equalsIgnoreCase("RefundAll")) {
            if (sData.getAmount() == null || sData.getAmount().trim().isEmpty()
                    || sData.getFee() == null || sData.getFee().trim().isEmpty()) {
                return;
            }
            // Swap sender ↔ receiver and country/currency for the refund send
            GeneralName senderName   = sData.getSenderName();
            GeneralName receiverName = sData.getReceiverName();
            sData.setReceiverName(senderName);
            sData.setSenderName(receiverName);

            CountryCurrencyInfo sCountry = sData.getSendCountryCurrency();
            CountryCurrencyInfo pCountry = sData.getReceiveCountryCurrency();
            sData.setSendCountryCurrency(pCountry);
            sData.setReceiveCountryCurrency(sCountry);

            String amount    = sData.getAmount();
            String feeCharge = sData.getFee();
            float newAmount  = Float.parseFloat(amount) + Float.parseFloat(feeCharge);
            sData.setTransactionType("WMF");
            sData.setAmount(String.format("%.2f", newAmount));

            sendTransaction(sData, true);

            sData.setAmount(amount);
            sData.setFee(feeCharge);

        } else if (serviceType.equalsIgnoreCase("Refund")) {
            if (sData.getMTCN() == null || sData.getMTCN().trim().isEmpty()
                    || (sData.getRefundMTCN() != null && !sData.getRefundMTCN().trim().isEmpty())) {
                return;
            }
            refundTransaction(sData);

        } else if (serviceType.equalsIgnoreCase("PKPush")) {
            if (sData.getMTCN() == null || sData.getMTCN().trim().isEmpty()) {
                UtilFunctions.mtcnLogger.info("MTCN is empty");
                return;
            }
            pushMTCNForPK(sData);
        }
    }

    // ── Gateway methods ───────────────────────────────────────────────────────

    private void sendTransaction(ExcelDataEntry sData, boolean isRefund) throws IOException {

        Map<String, File> smvAISMap = wugwRuntime.getSmvAISMap();
        Map<String, File> smsAISMap = wugwRuntime.getSmsAISMap();
        Map<String, File> smvBISMap = wugwRuntime.getSmvBISMap();
        Map<String, File> smsBISMap = wugwRuntime.getSmsBISMap();
        Map<String, File> fiBISMap  = wugwRuntime.getFiBISMap();
        XMLAssemblerHandler xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
        XMLAssemblerHandler xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();
        String serviceURL = wugwRuntime.getProperty("PI.domain");

        String sCountryCode = sData.getSendCountryCurrency().getIsoCode().getCountryCode();

        if (smvAISMap.containsKey(sCountryCode) && smsAISMap.containsKey(sCountryCode)) {

            File smvFile = smvAISMap.get(sCountryCode);
            File smsFile = smsAISMap.get(sCountryCode);

            String smvXMLStr = UtilFunctions.readFileToString(smvFile);
            String smsXMLStr = UtilFunctions.readFileToString(smsFile);

            smvXMLStr = GWMessageAssembleUtils.assembleAISSMVRequestMsg(xmlHandlerAIS, smvXMLStr, sData);
            String smvReplyStr = UtilFunctions.requestGatewayService(serviceURL, smvXMLStr);

            if (!smvReplyStr.startsWith("error - ")) {

                smsXMLStr = GWMessageAssembleUtils.assembleAISSMSRequestMsg(xmlHandlerAIS, smsXMLStr, smvXMLStr, smvReplyStr);
                String smsReplyStr = UtilFunctions.requestGatewayService(serviceURL, smsXMLStr);

                if (!smsReplyStr.startsWith("error - ")) {
                    String replyMTCN  = UtilFunctions.retrieveXMLValue(smsReplyStr, "mtcn");
                    String feeCharge  = UtilFunctions.retrieveXMLValue(smsReplyStr, "charges");
                    UtilFunctions.mtcnLogger.info("MTCN - " + replyMTCN);
                    sData.setMTCN(replyMTCN);
                    sData.setFee(UtilFunctions.convertToNatureAmount(feeCharge));
                } else {
                    UtilFunctions.mtcnLogger.info("AIS SMS - " + smsReplyStr);
                    sData.setMTCN("AIS SMS " + smsReplyStr);
                }

            } else {
                UtilFunctions.mtcnLogger.info("AIS SMV - " + smvReplyStr);
                sData.setMTCN("AIS SMV " + smvReplyStr);
            }

        } else if (fiBISMap.containsKey(sCountryCode)
                && smvBISMap.containsKey(sCountryCode)
                && smsBISMap.containsKey(sCountryCode)) {

            File fiFile = fiBISMap.get(sCountryCode);
            String fiXMLStr = UtilFunctions.readFileToString(fiFile);

            fiXMLStr = GWMessageAssembleUtils.assembleBISFIRequestMsg(xmlHandlerBIS, fiXMLStr, sData);
            String fiReplyStr = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);

            if (!fiReplyStr.startsWith("error - ")) {

                File smvFile = smvBISMap.get(sCountryCode);
                File smsFile = smsBISMap.get(sCountryCode);

                String smvXMLStr = UtilFunctions.readFileToString(smvFile);
                String smsXMLStr = UtilFunctions.readFileToString(smsFile);

                smvXMLStr = GWMessageAssembleUtils.assembleBISSMVRequestMsg(xmlHandlerBIS, smvXMLStr, sData, fiReplyStr);
                String smvReplyStr = UtilFunctions.requestGatewayService(serviceURL, smvXMLStr);

                if (!smvReplyStr.startsWith("error - ")) {

                    smsXMLStr = GWMessageAssembleUtils.assembleBISSMSRequestMsg(xmlHandlerBIS, smsXMLStr, smvXMLStr, smvReplyStr);
                    String smsReplyStr = UtilFunctions.requestGatewayService(serviceURL, smsXMLStr);

                    if (!smsReplyStr.startsWith("error - ")) {
                        String replyMTCN = UtilFunctions.retrieveXMLValue(smsReplyStr, "mtcn");
                        UtilFunctions.mtcnLogger.info("MTCN - " + replyMTCN);
                        sData.setMTCN(replyMTCN);
                    } else {
                        UtilFunctions.mtcnLogger.info("BIS SMS - " + smsReplyStr);
                        sData.setMTCN("BIS SMS " + smsReplyStr);
                    }
                } else {
                    UtilFunctions.mtcnLogger.info("BIS SMV - " + smvReplyStr);
                    sData.setMTCN("BIS SMV " + smvReplyStr);
                }
            } else {
                UtilFunctions.mtcnLogger.info("BIS FI - " + fiReplyStr);
                sData.setMTCN("BIS FI " + fiReplyStr);
            }

        } else {
            String msg = "The sending country/currency is not available for testing: " + sCountryCode;
            System.out.println(msg);
            sData.setMTCN(msg);
        }
    }

    private void refundTransaction(ExcelDataEntry sData) throws IOException {

        Map<String, File> searchAISMap = wugwRuntime.getSearchAISMap();
        Map<String, File> refundAISMap  = wugwRuntime.getRefundAISMap();
        XMLAssemblerHandler xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
        String serviceURL = wugwRuntime.getProperty("PI.domain");
        String naid = sData.getNaid();

        if (searchAISMap.containsKey(naid) && refundAISMap.containsKey(naid)) {

            File searchFile = searchAISMap.get(naid);
            File refundFile = refundAISMap.get(naid);

            String searchXMLStr = UtilFunctions.readFileToString(searchFile);
            String refundXMLStr = UtilFunctions.readFileToString(refundFile);

            searchXMLStr = GWMessageAssembleUtils.assembleAISSearchRequestMsg(xmlHandlerAIS, searchXMLStr, sData);
            String searchReplyStr = UtilFunctions.requestGatewayService(serviceURL, searchXMLStr);

            if (!searchReplyStr.startsWith("error - ")) {
                refundXMLStr = GWMessageAssembleUtils.assembleAISRefundRequestMsg(xmlHandlerAIS, refundXMLStr, searchReplyStr);
                String refundReplyStr = UtilFunctions.requestGatewayService(serviceURL, refundXMLStr);

                if (!refundReplyStr.startsWith("error - ")) {
                    String replyMTCN = UtilFunctions.retrieveXMLValue(refundReplyStr, "mtcn");
                    UtilFunctions.mtcnLogger.info("MTCN Refund - " + replyMTCN);
                    sData.setRefundMTCN(replyMTCN);
                } else {
                    UtilFunctions.mtcnLogger.info("AIS Refund - " + refundReplyStr);
                    sData.setMTCN("AIS Refund " + refundReplyStr);
                }
            } else {
                UtilFunctions.mtcnLogger.info("AIS Refund Search - " + searchReplyStr);
                sData.setMTCN("AIS Refund Search " + searchReplyStr);
            }

        } else {
            System.out.println("Search or Refund samples does not exist: " + naid);
        }
    }

    private void pushMTCNForPK(ExcelDataEntry sData) {
        PKJsonTxnObject txnObject = new PKJsonTxnObject();
        txnObject.getTransaction().setMgiTransactionId(sData.getMTCN());
        txnObject.getTransaction().getReceiveAmount().setValue(sData.getAmount());
        txnObject.getTransaction().getSender().getPerson().setFirstName(sData.getSenderName().getFirstName());
        txnObject.getTransaction().getSender().getPerson().setLastName(sData.getSenderName().getLastName());
        txnObject.getTransaction().getSender().getPerson().setMiddleName(sData.getSenderName().getMiddleName());
        txnObject.getTransaction().getReceiver().getPerson().setFirstName(sData.getReceiverName().getFirstName());
        txnObject.getTransaction().getReceiver().getPerson().setLastName(sData.getReceiverName().getLastName());
        txnObject.getTransaction().getReceiver().getPerson().setMiddleName(sData.getReceiverName().getMiddleName());

        RespBean respBean = pkPushService.pushMTCN(txnObject);
        System.out.println("PKPush response: " + respBean);
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    /**
     * Map an API request DTO to the internal ExcelDataEntry model,
     * resolving all optional fields (auto-names, default address, random amount).
     */
    public ExcelDataEntry toDataEntry(MtcnRequest request) {
        ExcelDataEntry data = new ExcelDataEntry();

        data.setServiceType(request.getServiceType());
        data.setTransactionType(request.getTransactionType());
        data.setNaid(request.getNaid());
        data.setMTCN(request.getMtcn());

        // Sender address — default if absent
        data.setSenderAddress(
            (request.getSenderAddress() != null && !request.getSenderAddress().isBlank())
                ? request.getSenderAddress()
                : DEFAULT_SENDER_ADDRESS);

        // Amount — resolve random if absent
        data.setAmount(resolveAmount(request));

        // Sending country/currency
        CountryCurrencyInfo sendCC = new CountryCurrencyInfo();
        IsoCode sendIso = new IsoCode();
        sendIso.setCountryCode(request.getSendCountry().getCountryCode());
        sendIso.setCurrencyCode(request.getSendCountry().getCurrencyCode());
        sendCC.setIsoCode(sendIso);
        data.setSendCountryCurrency(sendCC);

        // Receiving country/currency
        CountryCurrencyInfo recvCC = new CountryCurrencyInfo();
        IsoCode recvIso = new IsoCode();
        recvIso.setCountryCode(request.getReceiveCountry().getCountryCode());
        recvIso.setCurrencyCode(request.getReceiveCountry().getCurrencyCode());
        recvCC.setIsoCode(recvIso);
        data.setReceiveCountryCurrency(recvCC);

        // Sender name — auto-generate if absent
        if (request.getSenderName() != null) {
            data.setSenderName(toGeneralName(request.getSenderName()));
            data.setSenderNameAutoGen(false);
        } else {
            data.setSenderName(generateRandomName());
            data.setSenderNameAutoGen(true);
        }

        // Receiver name — auto-generate if absent
        if (request.getReceiverName() != null) {
            data.setReceiverName(toGeneralName(request.getReceiverName()));
            data.setReceiverNameAutoGen(false);
        } else {
            data.setReceiverName(generateRandomName());
            data.setReceiverNameAutoGen(true);
        }

        return data;
    }

    /**
     * Build a MtcnResult from the original request and the mutated ExcelDataEntry.
     * Echoes back all resolved fields (including auto-generated names and actual amount).
     */
    private MtcnResult buildResult(MtcnRequest request, ExcelDataEntry data) {
        MtcnResult result = new MtcnResult();

        result.setServiceType(request.getServiceType());

        result.setSendCountry(new MtcnResult.CountryInfo(
            request.getSendCountry().getCountryCode(),
            request.getSendCountry().getCurrencyCode()));

        result.setReceiveCountry(new MtcnResult.CountryInfo(
            request.getReceiveCountry().getCountryCode(),
            request.getReceiveCountry().getCurrencyCode()));

        if (data.getSenderName() != null) {
            result.setSenderName(new MtcnResult.NameInfo(
                data.getSenderName().getFirstName(),
                data.getSenderName().getMiddleName(),
                data.getSenderName().getLastName()));
        }
        if (data.getReceiverName() != null) {
            result.setReceiverName(new MtcnResult.NameInfo(
                data.getReceiverName().getFirstName(),
                data.getReceiverName().getMiddleName(),
                data.getReceiverName().getLastName()));
        }

        result.setSenderAddress(data.getSenderAddress());
        result.setAmount(data.getAmount());
        result.setFee(data.getFee());
        result.setFxRate(data.getFxRate());
        result.setRefundMtcn(data.getRefundMTCN());

        // Determine success: a real MTCN is a numeric/alphanumeric code,
        // not one of the error strings the gateway methods write on failure.
        String mtcn = data.getMTCN();
        boolean success = mtcn != null
                && !mtcn.startsWith("AIS ")
                && !mtcn.startsWith("BIS ")
                && !mtcn.startsWith("The sending");
        result.setSuccess(success);
        result.setMtcn(success ? mtcn : null);
        result.setMessage(success ? "MTCN generated successfully." : mtcn);

        return result;
    }

    private MtcnResult failResult(MtcnRequest request, String message) {
        MtcnResult result = new MtcnResult();
        result.setServiceType(request != null ? request.getServiceType() : null);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private String validate(MtcnRequest request) {
        if (request == null)
            return "Request body is required.";
        if (request.getServiceType() == null || request.getServiceType().isBlank())
            return "serviceType is required.";
        if (request.getSendCountry() == null
                || request.getSendCountry().getCountryCode() == null)
            return "sendCountry.countryCode is required.";
        if (request.getReceiveCountry() == null
                || request.getReceiveCountry().getCountryCode() == null)
            return "receiveCountry.countryCode is required.";
        if (request.getTransactionType() == null || request.getTransactionType().isBlank())
            return "transactionType is required.";
        if (!request.getTransactionType().equalsIgnoreCase("WMF")
                && !request.getTransactionType().equalsIgnoreCase("WMN"))
            return "transactionType must be 'WMF' or 'WMN'.";

        boolean hasAmount    = request.getAmount() != null && !request.getAmount().isBlank();
        boolean hasMinMax    = request.getMinAmount() != null && !request.getMinAmount().isBlank()
                            && request.getMaxAmount() != null && !request.getMaxAmount().isBlank();
        if (!hasAmount && !hasMinMax)
            return "Provide either 'amount' or both 'minAmount' and 'maxAmount'.";

        if (!hasAmount) {
            try {
                BigDecimal min = new BigDecimal(request.getMinAmount());
                BigDecimal max = new BigDecimal(request.getMaxAmount());
                if (min.compareTo(max) > 0)
                    return "'minAmount' must be less than or equal to 'maxAmount'.";
            } catch (NumberFormatException e) {
                return "'minAmount' and 'maxAmount' must be valid numeric values.";
            }
        }
        return null;
    }

    // ── Utility helpers ───────────────────────────────────────────────────────

    private String resolveAmount(MtcnRequest request) {
        if (request.getAmount() != null && !request.getAmount().isBlank()) {
            return request.getAmount();
        }
        BigDecimal min   = new BigDecimal(request.getMinAmount());
        BigDecimal max   = new BigDecimal(request.getMaxAmount());
        BigDecimal range = max.subtract(min);
        BigDecimal value = min.add(range.multiply(BigDecimal.valueOf(random.nextDouble())))
                             .setScale(2, RoundingMode.HALF_UP);
        return value.toPlainString();
    }

    private GeneralName generateRandomName() {
        GeneralName name = new GeneralName();
        name.setNameType(NameType.D);
        name.setFirstName(TEST_FIRST_NAMES[random.nextInt(TEST_FIRST_NAMES.length)]);
        name.setLastName(TEST_LAST_NAMES[random.nextInt(TEST_LAST_NAMES.length)]);
        return name;
    }

    private GeneralName toGeneralName(MtcnRequest.NameRequest nameRequest) {
        GeneralName name = new GeneralName();
        String nameType = nameRequest.getNameType();
        if ("M".equalsIgnoreCase(nameType)) {
            name.setNameType(NameType.M);
            name.setGivenName(nameRequest.getGivenName());
            name.setPaternalName(nameRequest.getPaternalName());
            name.setMaternalName(nameRequest.getMaternalName());
        } else if ("C".equalsIgnoreCase(nameType)) {
            name.setNameType(NameType.C);
            name.setBusinessName(nameRequest.getBusinessName());
        } else {
            name.setNameType(NameType.D);
            name.setFirstName(nameRequest.getFirstName());
            name.setMiddleName(nameRequest.getMiddleName());
            name.setLastName(nameRequest.getLastName());
        }
        return name;
    }
}
