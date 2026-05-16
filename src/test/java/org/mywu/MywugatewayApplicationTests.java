package org.mywu;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wu.gw.mybatis.mapper.CountryCurrencyListMapper;
import com.wu.gw.mybatis.model.CountryCurrencyList;

import javax.net.ssl.SSLServerSocketFactory;

@SpringBootTest
class MywugatewayApplicationTests {

	@Autowired
	CountryCurrencyListMapper ccListMapper;
	
	@Test
	void contextLoads() {
		/*CountryCurrencyList ccList = new CountryCurrencyList();
		ccList.setListName("CN_USD_CN393");
		ccList.setSCountry("CN");
		ccList.setSCurrency("USD");
		ccList.setSNaid("CN393");
		ccList.setUpdateTime(new Date());
		
		ccListMapper.addCountryCurrencyList(ccList);*/

		List<String> desired = List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
				"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
		SSLServerSocketFactory fact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		String[] supported = fact.getSupportedCipherSuites();
		//Arrays.stream(supported).filter(s -> desired.contains(s)).forEach(System.out::println);
		Arrays.stream(supported).forEach(System.out::println);
	}
}
