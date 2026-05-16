package com.wu.gw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class BatchTestingProcessor {

	public static void main(String[] args) {
		getDeliveryServices("C:\\dev\\IdeaProjects\\mywugateway\\testdata\\USSCCounterIDs.txt");
	}

	private static void getDeliveryServices(String filePath) {
		String request1 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xrsi=\"http://www.westernunion.com/schema/xrsi\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header/><soapenv:Body><xrsi:h2h-das-request><channel><type>H2H</type><name>USSC</name><version>9500</version></channel><foreign_remote_system><identifier>WGHHPH6471P</identifier><reference_no>29997000141024240</reference_no><counter_id>";
		String request2 = "</counter_id></foreign_remote_system><name>GetDeliveryServices</name><filters><queryfilter1>en</queryfilter1><queryfilter2>PH PHP</queryfilter2><queryfilter3>JP JPY</queryfilter3><queryfilter4>ALL</queryfilter4></filters></xrsi:h2h-das-request></soapenv:Body></soapenv:Envelope>";

		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null && !line.equals("")) {

				String counterId = line.trim();

				String templateCode = line + "\t";
				String result = TestUtilities.requestGatewayService(request1 + counterId + request2);
				if(result.startsWith("Error Reply - ")) {
					templateCode += StringProcessor.retrieveSubstr(result, "<error>", "</error>");
				} else if(result.startsWith("Success Reply - ")) {
					int i = result.indexOf("<SVC_CODE>500</SVC_CODE>");
					if(i > 0) {
						result = result.substring(i);
						templateCode += StringProcessor.retrieveSubstr(result, "<TEMPLT>", "</TEMPLT>");
					} else {
						templateCode += "D2B is not enabled for this account";
					}
				}
				System.out.println(templateCode);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void payStatusInquiry(String filePath) {
		String payStatusInquiry1 = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:pay-status-inquiry-request-data xmlns:ns2=\"http://www.westernunion.com/schema/xrsi\"><channel><type>H2H</type><name>USSC</name><version>9500</version></channel><mtcn>";
		String payStatusInquiry2 = "</mtcn><foreign_remote_system><identifier>WGHHPH6471P</identifier><reference_no>2019071100021636</reference_no><counter_id>PH647PP0101T</counter_id></foreign_remote_system></ns2:pay-status-inquiry-request-data></S:Body></S:Envelope>";
		
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null && line.trim().length() == 10) {
				
				String mtcn = line.trim();
				
				String status = "";
				String result = TestUtilities.requestGatewayService(payStatusInquiry1 + mtcn + payStatusInquiry2);
				if(result.startsWith("Error Reply - ")) {
					status = StringProcessor.retrieveSubstr(result, "<error>", "</error>");
				} else if(result.startsWith("Success Reply - ")) {
					status = StringProcessor.retrieveSubstr(result, "<pay_status_description>", "</pay_status_description>");
				}
				
				System.out.println(mtcn + "\t" + status);
				

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
