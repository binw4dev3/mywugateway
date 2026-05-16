package com.wu.gw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class StringProcessor {

	private static HashMap<String, WUTransaction> transactions = new HashMap<String, WUTransaction>();

	public static void main(String[] args) {

		// processMTCNField();

		// processIDIssueDateField();

		//extractSubstring("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\USSC_E9387_RMS_20240510.txt",
				//"<mtcn>", "</mtcn>");

		//extractDataField("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\USSC_E9387_RMS_20240510.txt");
		
		testPayStatus("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\USSC_MTCN_20240510.txt");
		
		//extractTXNDetailsAIS4PSBC("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBC_MTCN_SEARCH_20230601.txt");

		//extractSubstring("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBC-payout-MTCN-May_1_16.xml",
		//"<mtcn>", "</mtcn>");

		//extractTXNDetailsBIS("C:\\\\Users\\\\303172\\\\Desktop\\\\[[temp]]\\\\CHINA_APN_BIS_SMS_March.txt");

		//UtilFunctions.readFileByLine("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBC_ABMTSEND_RMS.txt",
				//StringProcessor::retrieveAISOrigin);
		
		/*UtilFunctions.readFileByLine("C:\\Users\\303172\\Desktop\\[[temp]]\\PSBC_MTCN_PAID_since_Jan_1_REQUEST.txt",
				(line) -> {
					if(line.startsWith("<env:Envelope")) {
					
					String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");
					String accountNum = retrieveSubstr(line, "<account_number>", "</account_number>");
					
					System.out.println(mtcn + "\t" + accountNum);
					}
				});*/

		//transactions.values().forEach(txn -> System.out.println("Ref_" + txn.getMTCN()));
		
		//transactions.values().forEach(txn -> System.out.println(txn.getMTCN() + "\t" + txn.getTimestamp() 
				//+ "\t" + txn.getPayoutAmount()));

		// transactions.values().forEach(txn -> System.out.println(txn.getFtid()));

		// transactions.values().forEach(txn -> System.out.println(txn.getMTCN() + "\t"
		// + txn.getFtid()));

		// WUTransaction.printTitle();
		// transactions.values().forEach(txn -> System.out.println(txn.getMTCN() + "\t"
		// + txn.getFtid()));
		// transactions.values().forEach(System.out::println);
	}
	
	private static void testPayStatus(String filePath) {
		
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
					status = retrieveSubstr(result, "<error>", "</error>");
				} else if(result.startsWith("Success Reply - ")) {
					status = retrieveSubstr(result, "<pay_status_description>", "</pay_status_description>");
				}
				
				System.out.println(mtcn + "\t" + status);
				

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		
		
		
	}

	private static void readAISExceptionLog(String logMsg) {

		if (logMsg != null) {

			if (logMsg.startsWith("\"<Message><exception_id>")) {
				readAISTransactionLog(logMsg);
			}
		}
	}

	private static void readAISTransactionLog(String logMsg) {

		if (logMsg != null) {

			/*if (!logMsg.startsWith("\"<Transaction><Message>") || !logMsg.startsWith("<Transaction><Message>")) {
				return;
			}*/
			
			WUTransaction txn = new WUTransaction();

			txn.setMTCN(retrieveSubstr(logMsg, "<mtcn>", "</mtcn>"));

			String originStr = retrieveSubstr(logMsg, "<originating_country_currency>",
					"</originating_country_currency>");
			txn.setOriginationCountry(retrieveSubstr(originStr, "<country_code>", "</country_code>"));
			txn.setOriginationCurrency(retrieveSubstr(originStr, "<currency_code>", "</currency_code>"));

			String destStr = retrieveSubstr(logMsg, "<destination_country_currency>",
					"</destination_country_currency>");
			txn.setDestinationCountry(retrieveSubstr(destStr, "<country_code>", "</country_code>"));
			txn.setDestinationCurrency(retrieveSubstr(destStr, "<currency_code>", "</currency_code>"));

			String sendingAmount = retrieveSubstr(logMsg, "<principal_amount>",
			"</principal_amount>");
			//String sendingAmount = retrieveSubstr(logMsg, "<originators_principal_amount>",
					//"</originators_principal_amount>");
			if (sendingAmount != null && !sendingAmount.equals("")) {
				txn.setSendingAmount(UtilFunctions.convertToNatureAmount(sendingAmount));
			}

			String payoutAmount = retrieveSubstr(logMsg, "<pay_amount>",
			"</pay_amount>");
			//String payoutAmount = retrieveSubstr(logMsg, "<destination_principal_amount>",
					//"</destination_principal_amount>");
			if (payoutAmount != null && !payoutAmount.equals("")) {
				txn.setPayoutAmount(UtilFunctions.convertToNatureAmount(payoutAmount));
			}

			String fxRate = retrieveSubstr(logMsg, "<exchange_rate>", "</exchange_rate>");
			if (fxRate != null && !fxRate.equals("")) {
				txn.setForeignExchangeRate(UtilFunctions.convertToNatureAmount(fxRate));
			}

			txn.setFsid(retrieveSubstr(logMsg, "<identifier>", "</identifier>"));
			txn.setReferenceNum("Ref_" + retrieveSubstr(logMsg, "<reference_no>", "</reference_no>"));
			txn.setFtid(retrieveSubstr(logMsg, "<counter_id>", "</counter_id>"));
			
			txn.setTimestamp(retrieveSubstr(logMsg, "<currentTimeStamp>", "</currentTimeStamp>"));

			if ((txn.getMTCN() != null && !txn.getMTCN().equals(""))
					|| (txn.getReferenceNum() != null && !txn.getReferenceNum().equals(""))) {
				
				//transactions.put(txn.getReferenceNum(), txn);
				
				if(!transactions.containsKey(txn.getMTCN()))
					transactions.put(txn.getMTCN(), txn);
			}

			/*
			 System.out.print(retrieveSubstr(logMsg, "<InterfaceName>", "</InterfaceName>") + "\t");
			
			String err = retrieveSubstr(logMsg, "<ErrorMessage>", "</ErrorMessage>");
			
			System.out.println(txn.getSendingAmount() + "(" + txn.getOriginationCountry() + "/"
					+ txn.getOriginationCurrency() + ")" + "\t" + txn.getPayoutAmount() + "("
					+ txn.getDestinationCountry() + "/" + txn.getDestinationCurrency() + ")" + "\t" + txn.getFtid() + "\t" + err);
			*/
		}
	}

	private static void readBISExceptionLog(String logMsg) {

		if (logMsg != null) {

			if (!logMsg.startsWith("\"<Message><exception_id>")) {
				return;
			}

			WUTransaction txn = new WUTransaction();

			txn.setReferenceNum(retrieveSubstr(logMsg, "<ExternalRefNo>", "</ExternalRefNo>"));

			txn.setMTCN(retrieveSubstr(logMsg, "<MTCN>", "</MTCN>"));

			String partnerStr = retrieveSubstr(logMsg, "<partner>", "</partner>");
			txn.setFsid(retrieveSubstr(partnerStr, "<id>", "</id>"));
			txn.setReferenceNum("Ref_" + retrieveSubstr(logMsg, "<ExternalRefNo>", "</ExternalRefNo>"));
			String connectorStr = retrieveSubstr(logMsg, "<connector>", "</connector>");
			txn.setFtid(retrieveSubstr(connectorStr, "<id>", "</id>"));

			if (txn.getFtid() == null || txn.getFtid().equals("")) {
				txn.setFtid(retrieveSubstr(logMsg, "<ConnectorId>", "</ConnectorId>"));
			}

			if ((txn.getMTCN() != null && !txn.getMTCN().equals(""))
					|| (txn.getReferenceNum() != null && !txn.getReferenceNum().equals(""))) {
				// transactions.put(txn.getReferenceNum(), txn);
				transactions.put(txn.getMTCN(), txn);
			}
		}
	}

	private static void readBISTransactionLog(String logMsg) {

		if (logMsg != null) {

			/*
			 * if (!logMsg.startsWith("\"<Transaction><Message><message_id>")) { return; }
			 * 
			 * String serviceName = retrieveSubstr(logMsg, "<ServiceName>",
			 * "</ServiceName>");
			 * 
			 * if (!serviceName.equalsIgnoreCase("SEND_MONEY_VALIDATION")) { return; }
			 */

			WUTransaction txn = new WUTransaction();

			txn.setMTCN(retrieveSubstr(logMsg, "<mtcn>", "</mtcn>"));

			txn.setNewMTCN(retrieveSubstr(logMsg, "<transaction_id>", "</transaction_id>"));

			String originStr = retrieveSubstr(logMsg, "<origination>", "</origination>");
			txn.setOriginationCountry(retrieveSubstr(originStr, "<country_iso_code>", "</country_iso_code>"));
			txn.setOriginationCurrency(retrieveSubstr(originStr, "<currency_iso_code>", "</currency_iso_code>"));

			String sendingAmount = retrieveSubstr(originStr, "<principal_amount>", "</principal_amount>");
			if (sendingAmount != null && !sendingAmount.equals("")) {
				txn.setSendingAmount(UtilFunctions.convertToNatureAmount(sendingAmount));
			}

			String destStr = retrieveSubstr(logMsg, "<destination>", "</destination>");
			txn.setDestinationCountry(retrieveSubstr(destStr, "<country_iso_code>", "</country_iso_code>"));
			txn.setDestinationCurrency(retrieveSubstr(destStr, "<currency_iso_code>", "</currency_iso_code>"));

			String payoutAmount = retrieveSubstr(destStr, "<expected_payout_amount>", "</expected_payout_amount>");
			if (payoutAmount != null && !payoutAmount.equals("")) {
				txn.setPayoutAmount(UtilFunctions.convertToNatureAmount(payoutAmount));
			}

			String fxRate = retrieveSubstr(logMsg, "<exchange_rate>", "</exchange_rate>");
			if (fxRate != null && !fxRate.equals("")) {
				txn.setForeignExchangeRate(UtilFunctions.convertToNatureAmount(fxRate));
			}

			String partnerStr = retrieveSubstr(logMsg, "<partner>", "</partner>");
			txn.setFsid(retrieveSubstr(partnerStr, "<id>", "</id>"));
			txn.setReferenceNum("Ref_" + retrieveSubstr(logMsg, "<ExternalRefNo>", "</ExternalRefNo>"));
			String connectorStr = retrieveSubstr(logMsg, "<connector>", "</connector>");
			txn.setFtid(retrieveSubstr(connectorStr, "<id>", "</id>"));

			if ((txn.getMTCN() != null && !txn.getMTCN().equals(""))
					|| (txn.getReferenceNum() != null && !txn.getReferenceNum().equals(""))) {
				// transactions.put(txn.getReferenceNum(), txn);
				transactions.put(txn.getMTCN(), txn);
			}
		}
	}

	private static void extractDataField(String filePath) {

		try {

			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				//if(!line.startsWith("\"<Message><exception_id>")) { line = br.readLine();
				 //continue; }

				//String refNo = retrieveSubstr(line, "<ExternalRefNo>", "</ExternalRefNo>");
				//System.out.println(refNo);
				
				
				String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");
				
				String origin = retrieveAISOrigin(line);
				
				String dest = retrieveAISDest(line);
				
				String ftid = retrieveSubstr(line, "<counter_id>", "</counter_id>");
				
				//if(mtcn != null && !mtcn.equals("")) System.out.println(mtcn + "\t" + origin + "\t" + dest + "\t" + ftid);
				
				if(mtcn != null && !mtcn.equals("")) System.out.println(mtcn + "\t" + dest + "\t" + ftid);
				

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private static void extractTXNDetailsAIS(String filePath) {

		try {

			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				if(!line.startsWith("\"<Message><exception_id>")) { line = br.readLine();
				 continue; }

				String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");
				
				 String interfaceName = retrieveSubstr(line, "<InterfaceName>",
				 "</InterfaceName>");

				/* if(!interfaceName.equals("SEND_MONEY_VALIDATION") &&
				 * !interfaceName.equals("SEND_MONEY_STORE") ) { line = br.readLine(); continue;
				 * }
				 */

				String refno = retrieveSubstr(line, "<reference_no>", "</reference_no>");
				
				String timepoints = retrieveSubstr(line, "<currentTimeStamp>", "</currentTimeStamp>");
				String txnpurpose = retrieveSubstr(line, "<transaction_reason>", "</transaction_reason>");
				String occupation = retrieveSubstr(line, "<occupation>", "</occupation>");
				String idIssueDate = retrieveSubstr(line, "<id_issue_date>", "</id_issue_date>");
				String idExpDate = retrieveSubstr(line, "<id_expiration_date>", "</id_expiration_date>");
				String idType = retrieveSubstr(line, "<id_type>", "</id_type>");
				String idIssuer = retrieveSubstr(line, "<id_country_of_issue>", "</id_country_of_issue>");
				String idNum = retrieveSubstr(line, "<id_number>", "</id_number>");
				String phoneNum = retrieveSubstr(line, "<contact_phone>", "</contact_phone>");
				
				String destStr = retrieveSubstr(line, "<destination_country_currency>",
						 "</destination_country_currency>");
				String destCountry =
						 retrieveSubstr(destStr, "<country_code>", "</country_code>");
				String destCurrency =
						 retrieveSubstr(destStr, "<currency_code>", "</currency_code>");
				
				String receiverStr = retrieveSubstr(line, "<receiver>",
						 "</receiver>");
				String receiverFN = retrieveSubstr(receiverStr, "<first_name>",
						 "</first_name>");
				String receiverLN = retrieveSubstr(receiverStr, "<last_name>",
						 "</last_name>");
				
				String employPosLevel = retrieveSubstr(line, "<employment_position_level>",
						 "</employment_position_level>");
						
				String nationality = retrieveSubstr(line, "<nationality>", "</nationality>");
				
				String countryofBirth = retrieveSubstr(line, "<Country_of_Birth>", "</Country_of_Birth>");

				
				// String amount = retrieveSubstr(line, "<originators_principal_amount>",
				// "</originators_principal_amount>");
				String ftid = retrieveSubstr(line, "<counter_id>", "</counter_id>");
				
				String mywu = retrieveSubstr(line, "<mywu_number>", "</mywu_number>");

				String errorMsg = retrieveSubstr(line, "<ErrorMessage>", "</ErrorMessage>");
				
				System.out.println("SPM_" + mtcn + "\t" + "SPM_" + refno + "\t" + ftid + "\t" + errorMsg + "\t" + interfaceName + 
						"\t" + "SPM_" + timepoints +
						"\t" + txnpurpose +
						"\t" + occupation +
						"\t" + idType +
						"\t" + "SPM_" + idNum +
						"\t" + idIssuer +
						
						"\t" + "SPM_" + idExpDate +
						"\t" + "SPM_" + phoneNum + 
						"\t" + "SPM_" + idExpDate + 
						"\t" + destCountry + "/" + destCurrency +
						"\t" + receiverFN + 
						"\t" + receiverLN + 
						"\t" + employPosLevel + 
						"\t" + nationality + 
						"\t" + countryofBirth 
						);
				
				/*System.out.println("SPM_" + mtcn + "\t" + "SPM_" + refno + "\t" + ftid + "\t" + interfaceName + 
						"\t" + "SPM_" + timepoints +
						"\t" + idType +
						"\t" + "SPM_" + idNum +
						"\t" + "SPM_" + mywu);*/
				
				/*
				 * if(amount !=null && !amount.equals("")) {
				 * 
				 * String oriStr = retrieveSubstr(line, "<originating_country_currency>",
				 * "</originating_country_currency>"); String oriCurrency =
				 * retrieveSubstr(oriStr, "<currency_code>", "</currency_code>");
				 * 
				 * //System.out.println(interfaceName + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + oriCurrency + "\t" + ftid);
				 * 
				 * System.out.println("SEND_MONEY_STORE" + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + oriCurrency + "\t" + ftid);
				 * 
				 * } else {
				 * 
				 * amount = retrieveSubstr(line, "<destination_principal_amount>",
				 * "</destination_principal_amount>");
				 * 
				 * String destStr = retrieveSubstr(line, "<destination_country_currency>",
				 * "</destination_country_currency>"); String destCurrency =
				 * retrieveSubstr(destStr, "<currency_code>", "</currency_code>");
				 * 
				 * //System.out.println(interfaceName + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + destCurrency + "\t" + ftid);
				 * 
				 * System.out.println("SEND_MONEY_STORE" + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + destCurrency + "\t" + ftid); }
				 */

				// String refNo = retrieveSubstr(line, "<reference_no>", "</reference_no>");
				// String employpos = retrieveSubstr(line, "<employment_position_level>",
				// "</employment_position_level>");
				// String origin = retrieveBISOrigin(line);
				// String dest = retrieveAISDest(line);
				// String product = retrieveProductCodeAIS(line);

				// String destAmount = retrieveSubstr(line, "<destination_principal_amount>",
				// "</destination_principal_amount>");
				// if(interfaceName.equals("SEND_MONEY_VALIDATION") && refno != null &&
				// !refno.equals(""))

				// if(mtcn.length() == 10)

				// System.out.println("SPM" + mtcn + "\t" + "SPM" + ftid);

				//System.out.println(mtcn + "\t" + ftid);

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static void extractTXNDetailsAIS2(String filePath) {

		try {

			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				//if(!line.startsWith("\"<Message><exception_id>")) { line = br.readLine();
				// continue; }

				//if(!line.startsWith("\"<Transaction><Message><message_id>")) { line =
				 //br.readLine(); continue; }
				
				String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");
				
				//String appName = retrieveSubstr(line, "<ApplicationName>", "</ApplicationName>");
				//if(appName == null || (!appName.equals("H2H") && !appName.equals("SFSS"))) { line = br.readLine(); continue; }
				
				//String interfaceName = retrieveSubstr(line, "<InterfaceName>",
				 //"</InterfaceName>");

				/* if(!interfaceName.equals("SEND_MONEY_VALIDATION") &&
				 * !interfaceName.equals("SEND_MONEY_STORE") ) { line = br.readLine(); continue;
				 * }
				 */

				String refno = retrieveSubstr(line, "<reference_no>", "</reference_no>");
				
				//String timepoints = retrieveSubstr(line, "<currentTimeStamp>", "</currentTimeStamp>");
				/*String txnpurpose = retrieveSubstr(line, "<transaction_reason>", "</transaction_reason>");
				String occupation = retrieveSubstr(line, "<occupation>", "</occupation>");
				String idIssueDate = retrieveSubstr(line, "<id_issue_date>", "</id_issue_date>");
				String idExpDate = retrieveSubstr(line, "<id_expiration_date>", "</id_expiration_date>");
				String idType = retrieveSubstr(line, "<id_type>", "</id_type>");
				String idIssuer = retrieveSubstr(line, "<id_country_of_issue>", "</id_country_of_issue>");
				String idNum = retrieveSubstr(line, "<id_number>", "</id_number>");
				String phoneNum = retrieveSubstr(line, "<contact_phone>", "</contact_phone>");*/
				
				/*String oriStr = retrieveSubstr(line, "<originating_country_currency>",
						 "</originating_country_currency>");
				String oriCountry =
						 retrieveSubstr(oriStr, "<country_code>", "</country_code>");
				String oriCurrency =
						 retrieveSubstr(oriStr, "<currency_code>", "</currency_code>");
				
				String destStr = retrieveSubstr(line, "<destination_country_currency>",
						 "</destination_country_currency>");
				String destCountry =
						 retrieveSubstr(destStr, "<country_code>", "</country_code>");
				String destCurrency =
						 retrieveSubstr(destStr, "<currency_code>", "</currency_code>");
				
				String receiverName = retrieveReceiverName(line);*/
				
				String receiverStr = retrieveSubstr(line, "<receiver>",
						 "</receiver>");
				/*String receiverFN = retrieveSubstr(receiverStr, "<first_name>",
						 "</first_name>");
				String receiverLN = retrieveSubstr(receiverStr, "<last_name>",
						 "</last_name>");
				
				String employPosLevel = retrieveSubstr(line, "<employment_position_level>",
						 "</employment_position_level>");
						
				String nationality = retrieveSubstr(line, "<nationality>", "</nationality>");
				
				String countryofBirth = retrieveSubstr(line, "<Country_of_Birth>", "</Country_of_Birth>");*/

				//String product = retrieveProductCodeAIS(line);
				
				/*String originalAmount = retrieveSubstr(line, "<originators_principal_amount>",
						"</originators_principal_amount>");
				
				String destAmount = retrieveSubstr(line, "<destination_principal_amount>",
				"</destination_principal_amount>");
				
				
				//String mywu = retrieveSubstr(line, "<mywu_number>", "</mywu_number>");

				String errorMsg = retrieveSubstr(line, "<ErrorMessage>", "</ErrorMessage>");*/
				
				String ftid = retrieveSubstr(line, "<counter_id>", "</counter_id>");
				
				String senderStreet =
						 retrieveSubstr(line, "<street>", "</street>");
				
				String senderCity =
						 retrieveSubstr(line, "<city>", "</city>");
				
				String senderState =
						 retrieveSubstr(line, "<state>", "</state>");
				
				String filingDate =
						 retrieveSubstr(line, "<filing_date>", "</filing_date>");
				
				System.out.println("SPM_" + mtcn + "\t" + "SPM_" + refno + "\t" + "SPM_" + ftid + "\t" + senderState + "\t" + senderCity + "\t" + senderStreet);
				
				/*System.out.println("SPM_" + refno + "\t" + ftid + "\t" + product + "\t" + 
						"\t" + "SPM_" + timepoints +
						"\t" + oriCountry + "/" + oriCurrency +
						"\t" + destCountry + "/" + destCurrency +
						"\t" + "SPM_" + convertToNatureAmount(originalAmount) + 
						"\t" + receiverName
						);*/
				
				
				/*System.out.println("SPM_" + refno + "\t" + ftid + "\t" + errorMsg + "\t" + interfaceName + 
						"\t" + "SPM_" + timepoints +
						"\t" + oriCountry + "/" + oriCurrency +
						"\t" + destCountry + "/" + destCurrency +
						"\t" + "SPM_" + convertToNatureAmount(destAmount)
						);
				
				System.out.println("SPM_" + mtcn + "\t" + "SPM_" + refno + "\t" + ftid + "\t" + interfaceName + 
						"\t" + "SPM_" + timepoints +
						"\t" + idType +
						"\t" + "SPM_" + idNum +
						"\t" + "SPM_" + mywu);*/
				
				/*
				 * if(amount !=null && !amount.equals("")) {
				 * 
				 * String oriStr = retrieveSubstr(line, "<originating_country_currency>",
				 * "</originating_country_currency>"); String oriCurrency =
				 * retrieveSubstr(oriStr, "<currency_code>", "</currency_code>");
				 * 
				 * //System.out.println(interfaceName + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + oriCurrency + "\t" + ftid);
				 * 
				 * System.out.println("SEND_MONEY_STORE" + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + oriCurrency + "\t" + ftid);
				 * 
				 * } else {
				 * 
				 * amount = retrieveSubstr(line, "<destination_principal_amount>",
				 * "</destination_principal_amount>");
				 * 
				 * String destStr = retrieveSubstr(line, "<destination_country_currency>",
				 * "</destination_country_currency>"); String destCurrency =
				 * retrieveSubstr(destStr, "<currency_code>", "</currency_code>");
				 * 
				 * //System.out.println(interfaceName + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + destCurrency + "\t" + ftid);
				 * 
				 * System.out.println("SEND_MONEY_STORE" + "\t" + refno + "\t" +
				 * convertToNatureAmount(amount) + "\t" + destCurrency + "\t" + ftid); }
				 */

				// String refNo = retrieveSubstr(line, "<reference_no>", "</reference_no>");
				// String employpos = retrieveSubstr(line, "<employment_position_level>",
				// "</employment_position_level>");
				// String origin = retrieveBISOrigin(line);
				// String dest = retrieveAISDest(line);
				// String product = retrieveProductCodeAIS(line);

				// String destAmount = retrieveSubstr(line, "<destination_principal_amount>",
				// "</destination_principal_amount>");
				// if(interfaceName.equals("SEND_MONEY_VALIDATION") && refno != null &&
				// !refno.equals(""))

				// if(mtcn.length() == 10)

				// System.out.println("SPM" + mtcn + "\t" + "SPM" + ftid);

				//System.out.println(mtcn + "\t" + ftid);

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private static void extractTXNDetailsAIS4PSBC(String filePath) {

		try {

			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {
				
				String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");

				String refno = retrieveSubstr(line, "<reference_no>", "</reference_no>");
				
				String ftid = retrieveSubstr(line, "<counter_id>", "</counter_id>");
				
				String senderStreet =
						 retrieveSubstr(line, "<street>", "</street>");
				
				String senderCity =
						 retrieveSubstr(line, "<city>", "</city>");
				
				String senderState =
						 retrieveSubstr(line, "<state>", "</state>");
				
				System.out.println("SPM_" + mtcn + "\t" + "SPM_" + refno + "\t" + "SPM_" + ftid + "\t" + senderState + "\t" + senderCity + "\t" + senderStreet);

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	public static String convertToNatureAmount(String amount) {

		if(amount == null || amount.equals("")) return "";
		
		float natureAmt = Float.valueOf(amount) / 100;

		return String.valueOf(natureAmt);
	}

	/*
	 * private static void extractTXNDetailsAIS(String filePath) {
	 * 
	 * try {
	 * 
	 * File inputFile = new File(filePath); InputStreamReader reader = new
	 * InputStreamReader(new FileInputStream(inputFile));
	 * 
	 * BufferedReader br = new BufferedReader(reader); String line = br.readLine();
	 * 
	 * while (line != null) {
	 * 
	 * //if(!line.startsWith("\"<Message><exception_id>")) { //line = br.readLine();
	 * //continue; //}
	 * 
	 * String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>"); //String receiver =
	 * retrieveSubstr(line, "<reference_no>", "</reference_no>"); String payamount =
	 * retrieveSubstr(line, "<counter_id>", "</counter_id>"); //String refNo =
	 * retrieveSubstr(line, "<reference_no>", "</reference_no>"); //String employpos
	 * = retrieveSubstr(line, "<employment_position_level>",
	 * "</employment_position_level>"); //String origin = retrieveBISOrigin(line);
	 * //String dest = retrieveAISDest(line); //String product =
	 * retrieveProductCodeAIS(line);
	 * 
	 * //String destAmount = retrieveSubstr(line, "<destination_principal_amount>",
	 * "</destination_principal_amount>");
	 * 
	 * //System.out.println("SPM" + mtcn + "\t" + "SPM" + receiver + "\t" +
	 * payamount); System.out.println("SPM" + mtcn + "\t" + "SPM" + payamount);
	 * 
	 * line = br.readLine(); }
	 * 
	 * br.close();
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace(); } }
	 */
	
	private static String retrieveReceiverName(String line) {
		
		String receiverStr = retrieveSubstr(line, "<receiver>",
				 "</receiver>");
		
		if(receiverStr.equals("")) {
			receiverStr = retrieveSubstr(line, "<receiver xmlns=\"\"\"\">",
					 "</receiver>");
		}
		
		String receiverFN = retrieveSubstr(receiverStr, "<first_name>",
				 "</first_name>");
		String receiverLN = retrieveSubstr(receiverStr, "<last_name>",
				 "</last_name>");
		
		return receiverFN + " " + receiverLN;
	}

	private static void extractTXNDetailsBIS(String filePath) {

		try {

			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				if(!line.startsWith("\"<Transaction><Message><message_id>")) { line =
				 br.readLine(); continue; }
				
				//String appName = retrieveSubstr(line, "<ApplicationName>", "</ApplicationName>");
				//if(appName == null || !appName.equals("BIS"))  { line = br.readLine(); continue; }
				
				String externalRef = retrieveSubstr(line, "<ExternalRefNo>",
						"</ExternalRefNo>");
				
				String origin = retrieveBISOrigin(line);
				String dest = retrieveBISDest(line);
				String product = retrieveProductCode(line);
				
				String receiverName = retrieveReceiverName(line);

				System.out.println("REF_" + externalRef + "\t" + product + "\t" + origin + "\t" + dest + "\t" + receiverName);
				
				/*
				if(!line.startsWith("\"<Message><exception_id>")) { line =
						 br.readLine(); continue; }

				// String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");
				String externalRef = retrieveSubstr(line, "<ExternalRefNo>",
				"</ExternalRefNo>");
				
				String errorMsg = retrieveSubstr(line, "<ErrorMessage>",
						"</ErrorMessage>");
				String serviceName = retrieveSubstr(line, "<ServiceName>",
						"</ServiceName>");
				
				String ftid = retrieveSubstr(line, "<ConnectorId>",
						"</ConnectorId>");
				
				String timestamp = retrieveSubstr(line, "<currentTimeStamp>",
						"</currentTimeStamp>");
				
				System.out.println("Ref_" + externalRef + "\t" + ftid + "\t" + errorMsg + "\t" + serviceName + "\t" + timestamp);
*/
				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static String retrieveBISDest(String line) {

		String destStr = retrieveSubstr(line, "<destination>", "</destination>");
		String destCountry = retrieveSubstr(destStr, "<country_iso_code>", "</country_iso_code>");
		String destCurrency = retrieveSubstr(destStr, "<currency_iso_code>", "</currency_iso_code>");
		String destAmount = UtilFunctions.convertToNatureAmount(retrieveSubstr(destStr, "<expected_payout_amount>", "</expected_payout_amount>"));

		return destCountry + "/" + destCurrency + "\t" + destAmount;
	}

	private static String retrieveAISDest(String line) {

		String destStr = retrieveSubstr(line, "<destination_country_currency>", "</destination_country_currency>");
		String destCountry = retrieveSubstr(destStr, "<country_code>", "</country_code>");
		String destCurrency = retrieveSubstr(destStr, "<currency_code>", "</currency_code>");

		return destCountry + "\t" + destCurrency;
	}
	
	private static String retrieveAISOrigin(String line) {

		String originStr = retrieveSubstr(line, "<originating_country_currency>", "</originating_country_currency>");
		String originCountry = retrieveSubstr(originStr, "<country_code>", "</country_code>");
		String originCurrency = retrieveSubstr(originStr, "<currency_code>", "</currency_code>");

		//System.out.println(originCountry + "\t" + originCurrency);
		
		return originCountry + "\t" + originCurrency;
	}

	private static String retrieveBISOrigin(String line) {

		String oriStr = retrieveSubstr(line, "<origination>", "</origination>");
		String oriCountry = retrieveSubstr(oriStr, "<country_iso_code>", "</country_iso_code>");
		String oriCurrency = retrieveSubstr(oriStr, "<currency_iso_code>", "</currency_iso_code>");
		String oriAmount = UtilFunctions.convertToNatureAmount(retrieveSubstr(oriStr, "<principal_amount>", "</principal_amount>"));

		return oriCountry + "/" + oriCurrency + "\t" + oriAmount;
	}

	private static String retrieveProductCode(String line) {

		String product = retrieveSubstr(line, "<wu_product>", "</wu_product>");
		String code = retrieveSubstr(product, "<code>", "</code>");

		return "Product(" + code + ")";
	}

	private static String retrieveProductCodeAIS(String line) {

		String product = retrieveSubstr(line, "<delivery_services>", "</delivery_services>");
		String code = retrieveSubstr(product, "<code>", "</code>");

		return "Product(" + code + ")";
	}

	public static String retrieveSubstr(String line, String sStr, String eStr) {

		String subStr = "";

		int iStart = line.indexOf(sStr);
		int iEnd = line.indexOf(eStr, iStart);

		if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

			subStr = line.substring(iStart + sStr.length(), iEnd);
			// System.out.println(subStr);
		}

		return subStr;
	}

	private static void processMTCNField() {

		try {

			String filePath = "C:\\Users\\303172\\Desktop\\[[temp]]\\USSC_WGHHPH6471P_E9387.txt";
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				int iStart = line.indexOf("<mtcn>");
				int iEnd = line.indexOf("</mtcn>", iStart);

				if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

					String subStr = line.substring(iStart + 6, iEnd);
					System.out.print(subStr + "\t");

					if (subStr.length() < 10) {
						System.out.print("malformed MTCN");
					}

					System.out.println("");
				}

				// System.out.println();

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static void extractSubstring(String filePath, String sStr, String eStr) {

		try {

			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				int iStart = line.indexOf(sStr);
				int iEnd = line.indexOf(eStr, iStart);

				if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

					String subStr = line.substring(iStart + sStr.length(), iEnd);
					System.out.println(subStr + " OR ");
				}

				// System.out.println();

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static void processIDIssueDateField() {

		try {

			String filePath = "C:\\Users\\303172\\Desktop\\[[temp]]\\BDO_IDIssueDate_List.txt";
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				int iStart = line.indexOf("<id_issue_date>");
				int iEnd = line.indexOf("</id_issue_date>", iStart);

				if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

					String subStr = line.substring(iStart + 15, iEnd);
					System.out.print(subStr + "\t");

					if (subStr.length() < 10) {
						System.out.print("invalid id issue date");
					}

					System.out.println();
				}

				line = br.readLine();
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}

class WUTransaction {

	private String MTCN;

	private String newMTCN;

	private String sendingAmount;

	private String payoutAmount;

	private String foreignExchangeRate;

	private String originationCountry;

	private String originationCurrency;

	private String destinationCountry;

	private String destinationCurrency;

	private String productCode;

	private String referenceNum;

	private String fsid;

	private String ftid;
	
	private String timestamp;

	public String getMTCN() {
		return MTCN;
	}

	public void setMTCN(String mTCN) {
		MTCN = mTCN;
	}

	public String getNewMTCN() {
		return newMTCN;
	}

	public void setNewMTCN(String newMTCN) {
		this.newMTCN = newMTCN;
	}

	public String getSendingAmount() {
		return sendingAmount;
	}

	public void setSendingAmount(String sendingAmount) {
		this.sendingAmount = sendingAmount;
	}

	public String getPayoutAmount() {
		return payoutAmount;
	}

	public void setPayoutAmount(String payoutAmount) {
		this.payoutAmount = payoutAmount;
	}

	public String getForeignExchangeRate() {
		return foreignExchangeRate;
	}

	public void setForeignExchangeRate(String foreignExchangeRate) {
		this.foreignExchangeRate = foreignExchangeRate;
	}

	public String getOriginationCountry() {
		return originationCountry;
	}

	public void setOriginationCountry(String originationCountry) {
		this.originationCountry = originationCountry;
	}

	public String getOriginationCurrency() {
		return originationCurrency;
	}

	public void setOriginationCurrency(String originationCurrency) {
		this.originationCurrency = originationCurrency;
	}

	public String getDestinationCountry() {
		return destinationCountry;
	}

	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}

	public String getDestinationCurrency() {
		return destinationCurrency;
	}

	public void setDestinationCurrency(String destinationCurrency) {
		this.destinationCurrency = destinationCurrency;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getFsid() {
		return fsid;
	}

	public void setFsid(String fsid) {
		this.fsid = fsid;
	}

	public String getFtid() {
		return ftid;
	}

	public void setFtid(String ftid) {
		this.ftid = ftid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {

		return MTCN + "\t" + referenceNum + "\t" + ftid + "\t" + originationCountry + "\t" + originationCurrency + "\t"
				+ destinationCountry + "\t" + destinationCurrency + "\t" + sendingAmount + "\t" + payoutAmount + "\t"
				+ foreignExchangeRate + "\t" + productCode;
	}

	public static void printTitle() {
		
		System.out.println("mtcn" + "\t" + "Reference" + "\t" + "Counter ID" + "\t" + "Origination" + "\t" + "Sending Currency" + "\t" + "Destination" + "\t" + "Payout Currency" + "\t" + "Sending Amount" + "\t" + "Payout Amount"
				 + "\t" + "FX Rate" + "\t" + "Product Code");
	}
}
