package com.wu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509KeyManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import com.wu.console.AbstractServiceRequest;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.XMLAssemblerHandler;

/**
 * Gateway Client Runtime Class, to offer essential parameters and properties.
 * Properties are loaded automatically by Spring Boot from application.properties.
 */
public class WUGWRuntime {

    @Autowired
    private Environment environment;

    private HashMap<String, AbstractServiceRequest> requestMap;
    private XMLAssemblerHandler AISAssemblerHandler;
    private XMLAssemblerHandler BISAssemblerHandler;

    private final Map<String, File> dasAISMapPI = new HashMap<String, File>();
    private final Map<String, File> dasBISMapPI = new HashMap<String, File>();

    private final Map<String, File> dasAISMapProd = new HashMap<String, File>();
    private final Map<String, File> dasBISMapProd = new HashMap<String, File>();

    private final Map<String, File> smvAISMap = new HashMap<String, File>();
    private final Map<String, File> smsAISMap = new HashMap<String, File>();
    private final Map<String, File> smvBISMap = new HashMap<String, File>();
    private final Map<String, File> smsBISMap = new HashMap<String, File>();
    private final Map<String, File> fiBISMap = new HashMap<String, File>();

    private final Map<String, File> searchAISMap = new HashMap<String, File>();
    private final Map<String, File> refundAISMap = new HashMap<String, File>();

    private final Map<String, File> pricingBISMapProd = new HashMap<String, File>();
    private final Map<String, File> pricingAISMapProd = new HashMap<String, File>();

    private final Map<String, File> pricingBISMapPI = new HashMap<String, File>();
    private final Map<String, File> pricingAISMapPI = new HashMap<String, File>();

    public WUGWRuntime() {
    }

    @PostConstruct
    public void init() throws Exception {
        initSSLContext();
        initParameterMaps();
    }

    private void initParameterMaps() {
        try {
            File xmlDasQuery = ResourceUtils.getFile(getProperty("XMLSamplesPath.DAS_Query"));
            File xmlPIMTCN = ResourceUtils.getFile(getProperty("XMLSamplesPath.PI_MTCN"));

            /* Retrieve xml samples for DAS Query */
            HashMap<String, File> xmlFileMapDAS = UtilFunctions.scanFiles(xmlDasQuery);
            for (String fileName : xmlFileMapDAS.keySet()) {
                String[] keys = fileName.split("-");
                if (keys == null) {
                    continue;
                }

                if (keys.length == 5) {
                    String env = keys[0].toUpperCase();
                    String countryCode = keys[1].toUpperCase();
                    String gwService = keys[2].toUpperCase();
                    String gwType = keys[3].toUpperCase();
                    String naid = keys[4].toUpperCase();

                    String key = gwService + "-" + countryCode + "-" + naid;
                    if (env.equalsIgnoreCase("PI")) {
                        if (gwType.equals("AIS")) {
                            dasAISMapPI.put(key.toUpperCase(), xmlFileMapDAS.get(fileName));
                        } else if (gwType.equals("BIS")) {
                            dasBISMapPI.put(key.toUpperCase(), xmlFileMapDAS.get(fileName));
                        }

                    } else if (env.equalsIgnoreCase("PROD")) {
                        if (gwType.equals("AIS")) {
                            dasAISMapProd.put(key.toUpperCase(), xmlFileMapDAS.get(fileName));
                        } else if (gwType.equals("BIS")) {
                            dasBISMapProd.put(key.toUpperCase(), xmlFileMapDAS.get(fileName));
                        }
                    }
                }
            }

            /* Retrieve xml samples for MTCN Generation */
            HashMap<String, File> xmlFileMapMTCN = UtilFunctions.scanFiles(xmlPIMTCN);
            for (String fileName : xmlFileMapMTCN.keySet()) {
                String[] keys = fileName.split("-");
                if (keys == null) {
                    continue;
                }

                if (keys.length == 3) {
                    String gwType = keys[2].toUpperCase();
                    String apiType = keys[1].toUpperCase();
                    String countryCode = keys[0].toUpperCase();

                    if (apiType.equals("SMV") && gwType.equals("AIS")) {
                        smvAISMap.put(countryCode, xmlFileMapMTCN.get(fileName));
                    } else if (apiType.equals("SMS") && gwType.equals("AIS")) {
                        smsAISMap.put(countryCode, xmlFileMapMTCN.get(fileName));
                    } else if (apiType.equals("SMV") && gwType.equals("BIS")) {
                        smvBISMap.put(countryCode, xmlFileMapMTCN.get(fileName));
                    } else if (apiType.equals("SMS") && gwType.equals("BIS")) {
                        smsBISMap.put(countryCode, xmlFileMapMTCN.get(fileName));
                    } else if (apiType.equals("FI") && gwType.equals("BIS")) {
                        fiBISMap.put(countryCode, xmlFileMapMTCN.get(fileName));
                    }
                } else if (keys.length == 4) {
                    if (keys[1].toUpperCase().equals("REFUND")) {
                        String gwType = keys[3].toUpperCase();
                        String apiType = keys[2].toUpperCase();
                        String naid = keys[0].toUpperCase();

                        if (apiType.equals("SEARCH") && gwType.equals("AIS")) {
                            searchAISMap.put(naid, xmlFileMapMTCN.get(fileName));
                        } else if (apiType.equals("CONFIRM") && gwType.equals("AIS")) {
                            refundAISMap.put(naid, xmlFileMapMTCN.get(fileName));
                        }
                    }
                }
            }

            /* Retrieve xml samples for Pricing Inquiry */
            File xmlProdPricing = ResourceUtils.getFile(getProperty("XMLSamplesPath.PROD_Pricing"));
            File xmlPIPricing = ResourceUtils.getFile(getProperty("XMLSamplesPath.PI_Pricing"));

            HashMap<String, File> xmlFileMapPricingProd = UtilFunctions.scanFiles(xmlProdPricing);
            for (String fileName : xmlFileMapPricingProd.keySet()) {
                String[] keys = fileName.split("-");
                if (keys == null) {
                    continue;
                }

                if (keys.length == 4 && keys[1].toUpperCase().equals("FI")) {
                    String naid = keys[3].toUpperCase();
                    String gwType = keys[2].toUpperCase();
                    String countryCode = keys[0].toUpperCase();
                    String key = countryCode + "-" + naid;

                    if (gwType.equals("AIS")) {
                        pricingAISMapProd.put(key, xmlFileMapPricingProd.get(fileName));
                    } else if (gwType.equals("BIS")) {
                        pricingBISMapProd.put(key, xmlFileMapPricingProd.get(fileName));
                    }
                }
            }

            HashMap<String, File> xmlFileMapPricingPI = UtilFunctions.scanFiles(xmlPIPricing);
            for (String fileName : xmlFileMapPricingPI.keySet()) {
                String[] keys = fileName.split("-");
                if (keys == null) {
                    continue;
                }

                if (keys.length == 4 && keys[1].toUpperCase().equals("FI")) {
                    String naid = keys[3].toUpperCase();
                    String gwType = keys[2].toUpperCase();
                    String countryCode = keys[0].toUpperCase();
                    String key = countryCode + "-" + naid;

                    if (gwType.equals("AIS")) {
                        pricingAISMapPI.put(key, xmlFileMapPricingPI.get(fileName));
                    } else if (gwType.equals("BIS")) {
                        pricingBISMapPI.put(key, xmlFileMapPricingPI.get(fileName));
                    }
                }
            }

        } catch (IOException e) {
            UtilFunctions.loggingException(e);
        }
    }

    private void initSSLContext() throws Exception {
        KeyStore ksPI = KeyStore.getInstance("JKS");
        InputStream inPI = new ClassPathResource(getProperty("PI.keyStore")).getInputStream();
        ksPI.load(inPI, getProperty("PI.keyPassWord").toCharArray());
        X509KeyManager kmPI = UtilFunctions.getKeyManager("SunX509", ksPI,
                getProperty("PI.keyPassWord").toCharArray());

        KeyStore ksPROD = KeyStore.getInstance("JKS");
        InputStream inPROD = new ClassPathResource(getProperty("PROD.keyStore")).getInputStream();
        ksPROD.load(inPROD, getProperty("PROD.keyPassWord").toCharArray());
        X509KeyManager kmPROD = UtilFunctions.getKeyManager("SunX509", ksPROD,
                getProperty("PROD.keyPassWord").toCharArray());

        KeyManager[] keyManagers = new KeyManager[1];
        keyManagers[0] = kmPROD;

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(keyManagers, null, null);
        SSLContext.setDefault(context);

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals(getProperty("PROD.domain")))
                    return true;
                return false;
            }
        });
    }

    /**
     * Returns a property value from Spring Boot's environment (application.properties).
     */
    public String getProperty(String propName) {
        return environment.getProperty(propName);
    }

    public HashMap<String, AbstractServiceRequest> getRequestMap() {
        return requestMap;
    }

    public XMLAssemblerHandler getAISAssemblerHandler() {
        return AISAssemblerHandler;
    }

    public void setAISAssemblerHandler(XMLAssemblerHandler aISAssemblerHandler) {
        AISAssemblerHandler = aISAssemblerHandler;
    }

    public XMLAssemblerHandler getBISAssemblerHandler() {
        return BISAssemblerHandler;
    }

    public void setBISAssemblerHandler(XMLAssemblerHandler bISAssemblerHandler) {
        BISAssemblerHandler = bISAssemblerHandler;
    }

    public void setRequestMap(HashMap<String, AbstractServiceRequest> requestMap) {
        this.requestMap = requestMap;
    }

    public Map<String, File> getSmvAISMap() {
        return smvAISMap;
    }

    public Map<String, File> getSmsAISMap() {
        return smsAISMap;
    }

    public Map<String, File> getSmvBISMap() {
        return smvBISMap;
    }

    public Map<String, File> getSmsBISMap() {
        return smsBISMap;
    }

    public Map<String, File> getFiBISMap() {
        return fiBISMap;
    }

    public Map<String, File> getSearchAISMap() {
        return searchAISMap;
    }

    public Map<String, File> getRefundAISMap() {
        return refundAISMap;
    }

    public Map<String, File> getDasAISMapPI() {
        return dasAISMapPI;
    }

    public Map<String, File> getDasBISMapPI() {
        return dasBISMapPI;
    }

    public Map<String, File> getDasAISMapProd() {
        return dasAISMapProd;
    }

    public Map<String, File> getDasBISMapProd() {
        return dasBISMapProd;
    }

    public Map<String, File> getPricingBISMapProd() {
        return pricingBISMapProd;
    }

    public Map<String, File> getPricingAISMapProd() {
        return pricingAISMapProd;
    }

    public Map<String, File> getPricingBISMapPI() {
        return pricingBISMapPI;
    }

    public Map<String, File> getPricingAISMapPI() {
        return pricingAISMapPI;
    }
}
