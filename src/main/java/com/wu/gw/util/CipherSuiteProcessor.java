package com.wu.gw.util;

import javax.net.ssl.SSLServerSocketFactory;
import java.util.Arrays;
import java.util.List;

public class CipherSuiteProcessor {
    public static void main(String[] args) {
        List<String> desired = List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
        SSLServerSocketFactory fact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        String[] supported = fact.getSupportedCipherSuites();
        //Arrays.stream(supported).filter(s -> desired.contains(s)).forEach(System.out::println);
        Arrays.stream(supported).forEach(System.out::println);
    }
}
