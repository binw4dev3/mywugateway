package com.wu.gw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class USSCTxnDataProcessor {

	public static void main(String[] args) {
		extractTXNDetailsRMP4USSC("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\USSC_Nov23_RMP_log.txt");
	}

	private static void extractTXNDetailsRMP4USSC(String filePath) {
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {
				String destCountryCurrency = retrieveSubstr(line, "<destination_country_currency>", "</destination_country_currency>");
				
				String orignCountryCurrency = retrieveSubstr(line, "<originating_country_currency>", "</originating_country_currency>");
				String orignDestCountryCurrency = retrieveSubstr(line, "<original_destination_country_currency>", "</original_destination_country_currency>");
				String fxRate = retrieveSubstr(line, "<exchange_rate>", "</exchange_rate>");
				String sendAmt = retrieveSubstr(line, "<principal_amount>", "</principal_amount>");
				String payoutAmt = retrieveSubstr(line, "<pay_amount>", "</pay_amount>");
				String txnType = retrieveSubstr(line, "<transaction_type>", "</transaction_type>");
				String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");

				String orignCountry = retrieveSubstr(orignCountryCurrency, "<country_code>", "</country_code>");
				String orignCurrency = retrieveSubstr(orignCountryCurrency, "<currency_code>", "</currency_code>");

				String destCountry = retrieveSubstr(destCountryCurrency, "<country_code>", "</country_code>");
				String destCurrency = retrieveSubstr(destCountryCurrency, "<currency_code>", "</currency_code>");
				
				String orignDestCountry = retrieveSubstr(orignDestCountryCurrency, "<country_code>", "</country_code>");
				String orignDestCurrency = retrieveSubstr(orignDestCountryCurrency, "<currency_code>", "</currency_code>");
				
				if(destCountry.equals("PH")) {
					System.out.println(mtcn + "\t"
							 + "\t" + orignCountry
							 + "\t" + orignCurrency
							 + "\t" + destCountry
							 + "\t" + destCurrency
							 + "\t" + orignDestCountry
							 + "\t" + orignDestCurrency
							 + "\t" + sendAmt
							 + "\t" + payoutAmt
							 + "\t" + fxRate
							 + "\t" + txnType);
				}
				
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String retrieveSubstr(String line, String sStr, String eStr) {
		String subStr = "";

		int iStart = line.indexOf(sStr);
		int iEnd = line.indexOf(eStr, iStart);
		if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

			subStr = line.substring(iStart + sStr.length(), iEnd);
		}

		return subStr;
	}
}
