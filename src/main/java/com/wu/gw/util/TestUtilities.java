package com.wu.gw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509KeyManager;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.wu.xmlhandler.AbstractXMLAssembler;
import com.wu.xmlhandler.XMLAssemblerHandler;

public class TestUtilities {

	public static String TESTING_URL = "https://66.218.162.5";

	public static void main(String[] args) {

		//GWRuntime.getRTProp("PI.domain");
		
		String str = "<RECORDSET><GETCOUNTRIESCURRENCIES><COUNTRY_LONG>St. Maarten</COUNTRY_LONG><ISO_COUNTRY_NUM_CD>530</ISO_COUNTRY_NUM_CD><ISO_COUNTRY_CD>S1</ISO_COUNTRY_CD><CURRENCY_CD>USD</CURRENCY_CD><ISO_CURRENCY_NUM_CD>840</ISO_CURRENCY_NUM_CD><CURRENCY_NAME>US Dollar</CURRENCY_NAME></GETCOUNTRIESCURRENCIES><GETCOUNTRIESCURRENCIES><COUNTRY_LONG>St. Maarten</COUNTRY_LONG><ISO_COUNTRY_NUM_CD>530</ISO_COUNTRY_NUM_CD><ISO_COUNTRY_CD>S1</ISO_COUNTRY_CD><CURRENCY_CD>USD</CURRENCY_CD><ISO_CURRENCY_NUM_CD>840</ISO_CURRENCY_NUM_CD><CURRENCY_NAME>US Dollar</CURRENCY_NAME></GETCOUNTRIESCURRENCIES><GETCOUNTRIESCURRENCIES><COUNTRY_LONG>St. Maarten</COUNTRY_LONG><ISO_COUNTRY_NUM_CD>530</ISO_COUNTRY_NUM_CD><ISO_COUNTRY_CD>S1</ISO_COUNTRY_CD><CURRENCY_CD>USD</CURRENCY_CD><ISO_CURRENCY_NUM_CD>840</ISO_CURRENCY_NUM_CD><CURRENCY_NAME>US Dollar</CURRENCY_NAME></GETCOUNTRIESCURRENCIES></RECORDSET>";
		
		String value = UtilFunctions.retrieveXMLSubElement(str, "RECORDSET");
		
		String[] parts = value.split("(?<=</GETCOUNTRIESCURRENCIES>)");
		
		System.out.println(parts[0]);
		System.out.println(parts[1]);
	}
	
	private static void initSSLContext() throws Exception {
		//try {
			/*KeyStore ksPI = KeyStore.getInstance("JKS");
			InputStream inPI = new ClassPathResource(getProperty("PI.keyStore")).getInputStream();
			ksPI.load(inPI, getProperty("PI.keyPassWord").toCharArray());

			X509KeyManager kmPI = UtilFunctions.getKeyManager("SunX509", ksPI, getProperty("PI.keyPassWord").toCharArray());*/
			String prodPSW = "password01!";
			KeyStore ksPROD = KeyStore.getInstance("JKS");
			InputStream inPROD = new ClassPathResource("cert/keystore.jks").getInputStream();
			ksPROD.load(inPROD, prodPSW.toCharArray());

			X509KeyManager kmPROD = UtilFunctions.getKeyManager("SunX509", ksPROD, prodPSW.toCharArray());

			//String env = getProperty("GWRuntime.env");
			KeyManager[] keyManagers = new KeyManager[1];
			//if(env.equals("PROD")) {
				keyManagers[0] = kmPROD;
			//} else {
				//keyManagers[0] = kmPI;
			//}
			
			
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(keyManagers, null, null);
			SSLContext.setDefault(context);

		/*} catch (NoSuchAlgorithmException e) {
			UtilFunctions.loggingException(e);
		} catch (KeyManagementException e) {
			UtilFunctions.loggingException(e);
		} catch (CertificateException e) {
			UtilFunctions.loggingException(e);
		} catch (IOException e) {
			UtilFunctions.loggingException(e);
		} catch (KeyStoreException e) {
			UtilFunctions.loggingException(e);
		}*/
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				if (hostname.equals("https://wugateway2.westernunion.net"))
					return true;
				return false;
			}
		});
	}
	
	public static HashMap<String, File> scanFiles(String rootPath, String folder) {
		
		String fileDir = rootPath + folder;
		fileDir = URLDecoder.decode(fileDir);
		File srcDir = new File(fileDir);

		HashMap<String, File> fileMap = null;
		
		if (!srcDir.isDirectory()) {

			System.out.println("Invalid xml file directory.");

		} else {

			File[] files = srcDir.listFiles();

			fileMap = new HashMap<String, File>();
			
			for (File file : files) {
				
				if (getFileExtName(file).equals("xml")) {

					fileMap.put(file.getName(), file);
				}
			}
		}
		
		return fileMap;
	}
	
	public static String assembleSMVRequestMsg(XMLAssemblerHandler xmlHandler, String msg) {
		
		List<String> senderNameValues = Arrays.asList(
			
			"D",
			"TINA",
			"",
			"ZHANG"
		);
		
		List<String> receiverNameValues = Arrays.asList(
			
			"D",
			"ANGEL",
			"CHOA",
			"MEI"
		);
		
		List<String> financialValues = Arrays.asList(
				
			"WMN",
			"1900"
		);
		
		List<String> destValues = Arrays.asList(
				
			"KR",
			"USD"
		);
		
		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();
		
		String sNameKey = elements.get("smv.sender.name");
		AbstractXMLAssembler sNameAssembler = assemblers.get("smv.sender.name.assembler");
		
		msg = sNameAssembler.assemble(msg, sNameKey, senderNameValues);
		
		String rNameKey = elements.get("smv.receiver.name");
		AbstractXMLAssembler rNameAssembler = assemblers.get("smv.receiver.name.assembler");
		
		msg = rNameAssembler.assemble(msg, rNameKey, receiverNameValues);
		
		String finKey = elements.get("smv.financials.amount");
		AbstractXMLAssembler finAssembler = assemblers.get("smv.financials.amount.assembler");
		
		msg = finAssembler.assemble(msg, finKey, financialValues);
		
		String txnTypeKey = elements.get("smv.payment_details.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("smv.payment_details.transaction_type.assembler");
		
		msg = txnTypeAssembler.assemble(msg, txnTypeKey, financialValues);
		
		String destKey = elements.get("smv.payment_details.destination_country_currency");
		AbstractXMLAssembler destAssembler = assemblers.get("smv.payment_details.destination_country_currency.assembler");
		
		msg = destAssembler.assemble(msg, destKey, destValues);
		
		return msg;
	}
	
	public static String assembleSMSRequestMsg(XMLAssemblerHandler xmlHandler, String msg, String smvRequest, String smvReply) {
		
		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();
		
		List<String> smvReplyList = Arrays.asList(
				
			smvReply
		);
		
		List<String> smvRequestList = Arrays.asList(
				
			smvRequest
		);
		
		for(String key : elements.keySet()) {
			
			if(key.startsWith("sms.")) {

				String element = elements.get(key);
				AbstractXMLAssembler assembler = assemblers.get(key + ".assembler");
				
				if(key.endsWith(".sender") || key.endsWith(".receiver")) {
					
					msg = assembler.assemble(msg, element, smvRequestList);
					
				} else {
					
					msg = assembler.assemble(msg, element, smvReplyList);
					
				}
			}
		}
		
		return msg;
	}

	public static String getFileExtName(File file) {

		if (file == null) {

			return null;
		}

		return getFileExtName(file.getPath());
	}

	public static String getFileExtName(String filename) {

		if ((filename != null) && (filename.length() > 0)) {

			int dot = filename.lastIndexOf('.');

			if ((dot > -1) && (dot < (filename.length() - 1))) {

				return filename.substring(dot + 1);
			}
		}

		return filename;
	}

	public static String readFileToString(File srcFile) throws IOException {

		FileReader fr = new FileReader(srcFile);

		StringBuffer strBuf = new StringBuffer();

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		while ((line = br.readLine()) != null) {
			strBuf.append(line.trim());
		}

		br.close();
		fr.close();

		return strBuf.toString();
	}

	public static String requestGatewayService(String requestMsg) throws Exception {

		String replyMsg = null;
		HttpsURLConnection conn = null;
		
		TestUtilities.initSSLContext();
		
		try {

			//System.out.println("Request - \n" + requestMsg + "\n");

			URL url = new URL(TESTING_URL);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", Integer.toString(requestMsg.length()));
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			conn.setRequestProperty("SOAPAction", "ReceiveMoneySearch_Action");

			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

			conn.setSSLSocketFactory(factory);
			conn.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			conn.addRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE5.5;Windows NT; DigExt)");

			OutputStream os = conn.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
			osw.write(requestMsg);
			osw.flush();
			osw.close();

			BufferedReader l_reader = null;

			if (conn.getResponseCode() == 200) {

				l_reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			} else {

				l_reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sTotalString = new StringBuilder();
			String sCurrentLine = "";

			while ((sCurrentLine = l_reader.readLine()) != null) {

				sTotalString.append(sCurrentLine);
			}

			int iErrorCode = sTotalString.indexOf("<error>");
			int totalLength = sTotalString.length();

			if (iErrorCode > 0 && totalLength > (iErrorCode + 10)) {

				String errorCode = sTotalString.substring(iErrorCode);
				replyMsg = "Error Reply - " + sTotalString.toString();
				//System.out.println("Error Reply - \n" + errorCode);
				
			} else {

				//System.out.println("Success Reply - \n" + sTotalString);
				
				replyMsg = "Success Reply - " + sTotalString.toString();
			}

		} catch (Exception e) {

			e.printStackTrace();
			
		} finally {
			
			conn.disconnect();
		}
		
		return replyMsg;
	}
}
