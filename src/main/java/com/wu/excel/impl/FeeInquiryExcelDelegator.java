package com.wu.excel.impl;

import java.util.Collection;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.IsoCode;
import com.wu.gw.util.UtilFunctions;

public class FeeInquiryExcelDelegator implements IExcelObjectDelegator {

	@Override
	public Sheet parseXlsSheet(Workbook workbook) {

		Sheet sheet = (Sheet) workbook.getSheetAt(0);

		return sheet;
	}

	@Override
	public Object parseDataFromRowXls(Row row) {

		if (row == null) {

			return null;
		}

		int cellNum = row.getLastCellNum() - row.getFirstCellNum() + 1;

		if (cellNum != 14) {

			return null;
		}

		ExcelDataEntry sData = new ExcelDataEntry();

		sData.setRowNum(row.getRowNum());

		String amountStr = ExcelFileReader.retrieveNumericCellValue(row.getCell(7));

		if (amountStr == null || amountStr.equals("") || amountStr.equals("0")) {

			String minStr = ExcelFileReader.retrieveNumericCellValue(row.getCell(0));
			String maxStr = ExcelFileReader.retrieveNumericCellValue(row.getCell(1));

			if (minStr == null || minStr.equals("") || maxStr == null || maxStr.equals("")) {

				sData.setAmount("10");

			} else {

				//String amount = UtilFunctions.randomInt(Integer.valueOf(minStr).intValue(),
						//Integer.valueOf(maxStr).intValue());
								
				String amount = "" + (Integer.valueOf(maxStr).intValue() - 1);

				sData.setAmount(amount);
			}

		} else {

			sData.setAmount(amountStr);
		}

		CountryCurrencyInfo sendCountryCurrency = new CountryCurrencyInfo();
		CountryCurrencyInfo receiveCountryCurrency = new CountryCurrencyInfo();

		IsoCode sendIsoCode = new IsoCode();
		sendIsoCode.setCountryCode(row.getCell(2).getStringCellValue());
		sendIsoCode.setCurrencyCode(row.getCell(3).getStringCellValue());
		sendCountryCurrency.setIsoCode(sendIsoCode);

		sData.setSendCountryCurrency(sendCountryCurrency);

		IsoCode receiveIsoCode = new IsoCode();
		receiveIsoCode.setCountryCode(row.getCell(4).getStringCellValue());
		receiveIsoCode.setCurrencyCode(row.getCell(5).getStringCellValue());
		receiveCountryCurrency.setIsoCode(receiveIsoCode);

		sData.setReceiveCountryCurrency(receiveCountryCurrency);

		sData.setTransactionType(row.getCell(6).getStringCellValue());

		String feeCharge = ExcelFileReader.retrieveNumericCellValue(row.getCell(8));
		sData.setFee(feeCharge);
		
		String fxRate = ExcelFileReader.retrieveNumericCellValue(row.getCell(9));
		sData.setFxRate(fxRate);
		
		String counterId = ExcelFileReader.retrieveNumericCellValue(row.getCell(10));
		sData.setCounterId(counterId);
		
		String service = ExcelFileReader.retrieveNumericCellValue(row.getCell(11));
		if(service.equals("D2B")) {
			sData.setServiceType("500");
		} else if(service.equals("SVC")) {
			sData.setServiceType("800");
		} else {
			sData.setServiceType("000");
		}

		String apiType = ExcelFileReader.retrieveNumericCellValue(row.getCell(12));
		sData.setApiType(apiType);

		return sData;
	}

	@Override
	public void buildHeader(Sheet sheet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void buildDataRows(Sheet sheet) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean dataUpdate(Workbook workBook, Object updateData) {

		Sheet worksheet = workBook.getSheetAt(0);
		Collection<ExcelDataEntry> dataSet = (Collection<ExcelDataEntry>) updateData;

		for (ExcelDataEntry sData : dataSet) {

			Cell cell = worksheet.getRow(sData.getRowNum()).getCell(7);
			cell.setCellValue(sData.getAmount());
			
			cell = worksheet.getRow(sData.getRowNum()).getCell(8);
			cell.setCellValue(sData.getFee());

			cell = worksheet.getRow(sData.getRowNum()).getCell(9);
			cell.setCellValue(sData.getFxRate());
		}

		return true;
	}

	@Override
	public void buildDataRowsAppend(Sheet sheet) {
		// TODO Auto-generated method stub

	}
}
