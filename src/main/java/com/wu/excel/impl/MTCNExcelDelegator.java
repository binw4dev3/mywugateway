package com.wu.excel.impl;

import java.util.Collection;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.GeneralName;
import com.wu.gw.model.ais.IsoCode;
import com.wu.gw.model.ais.NameType;
import com.wu.gw.util.UtilFunctions;

public class MTCNExcelDelegator implements IExcelObjectDelegator {

	@Override
	public Sheet parseXlsSheet(Workbook workbook) {
		Sheet sheet = (Sheet) workbook.getSheetAt(0);
		return sheet;
	}

	private String readCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return cell.getStringCellValue();
		}
	}

	@Override
	public Object parseDataFromRowXls(Row row) {

		if (row == null) {

			return null;
		}

		int cellNum = row.getLastCellNum() - row.getFirstCellNum() + 1;

		if (cellNum != 30) {

			return null;
		}

		ExcelDataEntry sData = new ExcelDataEntry();

		sData.setRowNum(row.getRowNum());

		sData.setMTCN(row.getCell(0).getStringCellValue());
		// sData.setMTCN(ExcelFileReader.retrieveNumericCellValue(row.getCell(0)));

		GeneralName senderName = new GeneralName();
		GeneralName receiverName = new GeneralName();
		CountryCurrencyInfo sendCountryCurrency = new CountryCurrencyInfo();
		CountryCurrencyInfo receiveCountryCurrency = new CountryCurrencyInfo();

		String sNameType = row.getCell(1).getStringCellValue();
		if (sNameType != null && !sNameType.equals("")) {

			senderName.setNameType(NameType.fromValue(sNameType));

			if (sNameType.equals("D")) {

				String fn = readCellValue(row.getCell(2));
				String mn = readCellValue(row.getCell(3));
				String ln = readCellValue(row.getCell(4));

				if (fn != null && !fn.equals("") && ln != null && !ln.equals("")) {
					senderName.setFirstName(fn);
					senderName.setMiddleName(mn);
					senderName.setLastName(ln);
				} else {
					senderName.setFirstName(UtilFunctions.randomName());
					senderName.setMiddleName(UtilFunctions.randomName());
					senderName.setLastName(UtilFunctions.randomName());

					sData.setSenderNameAutoGen(true);
				}

			} else if (sNameType.equals("M")) {

				String gn = readCellValue(row.getCell(5));
				String pn = readCellValue(row.getCell(6));
				String mn = readCellValue(row.getCell(7));

				if (gn != null && !gn.equals("") && pn != null && !pn.equals("") && mn != null && !mn.equals("")) {
					senderName.setGivenName(gn);
					senderName.setPaternalName(pn);
					senderName.setMaternalName(mn);
				} else {
					senderName.setGivenName(UtilFunctions.randomName());
					senderName.setPaternalName(UtilFunctions.randomName());
					senderName.setMaternalName(UtilFunctions.randomName());

					sData.setSenderNameAutoGen(true);
				}

			} else if (sNameType.equals("C")) {

				String bn = readCellValue(row.getCell(8));

				if (bn != null && !bn.equals("")) {
					senderName.setBusinessName(bn);
				} else {
					senderName.setBusinessName(UtilFunctions.randomCorpName());

					sData.setSenderNameAutoGen(true);
				}
			}

		} else {

			senderName.setNameType(NameType.fromValue("D"));

			senderName.setFirstName(UtilFunctions.randomName());
			senderName.setMiddleName(UtilFunctions.randomName());
			senderName.setLastName(UtilFunctions.randomName());

			sData.setSenderNameAutoGen(true);
		}

		sData.setSenderName(senderName);

		IsoCode sendIsoCode = new IsoCode();
		sendIsoCode.setCountryCode(row.getCell(9).getStringCellValue());
		sendIsoCode.setCurrencyCode(row.getCell(10).getStringCellValue());
		sendCountryCurrency.setIsoCode(sendIsoCode);

		sData.setSendCountryCurrency(sendCountryCurrency);

		String rNameType = row.getCell(11).getStringCellValue();
		if (rNameType != null && !rNameType.equals("")) {

			receiverName.setNameType(NameType.fromValue(rNameType));

			if (rNameType.equals("D")) {

				String fn = row.getCell(12).getStringCellValue();
				String mn = row.getCell(13).getStringCellValue();
				String ln = row.getCell(14).getStringCellValue();

				if (fn != null && !fn.equals("") && ln != null && !ln.equals("")) {
					receiverName.setFirstName(fn);
					receiverName.setMiddleName(mn);
					receiverName.setLastName(ln);
				} else {
					receiverName.setFirstName(UtilFunctions.randomName());
					receiverName.setMiddleName(UtilFunctions.randomName());
					receiverName.setLastName(UtilFunctions.randomName());

					sData.setReceiverNameAutoGen(true);
				}

			} else if (rNameType.equals("M")) {

				String gn = row.getCell(15).getStringCellValue();
				String pn = row.getCell(16).getStringCellValue();
				String mn = row.getCell(17).getStringCellValue();

				if (gn != null && !gn.equals("") && pn != null && !pn.equals("") && mn != null && !mn.equals("")) {
					receiverName.setGivenName(gn);
					receiverName.setPaternalName(pn);
					receiverName.setMaternalName(mn);
				} else {
					receiverName.setGivenName(UtilFunctions.randomName());
					receiverName.setPaternalName(UtilFunctions.randomName());
					receiverName.setMaternalName(UtilFunctions.randomName());

					sData.setReceiverNameAutoGen(true);
				}
			}

		} else {

			receiverName.setNameType(NameType.fromValue("D"));

			receiverName.setFirstName(UtilFunctions.randomName());
			receiverName.setMiddleName(UtilFunctions.randomName());
			receiverName.setLastName(UtilFunctions.randomName());

			sData.setReceiverNameAutoGen(true);
		}

		sData.setReceiverName(receiverName);

		IsoCode receiveIsoCode = new IsoCode();
		receiveIsoCode.setCountryCode(row.getCell(18).getStringCellValue());
		receiveIsoCode.setCurrencyCode(row.getCell(19).getStringCellValue());
		receiveCountryCurrency.setIsoCode(receiveIsoCode);

		sData.setReceiveCountryCurrency(receiveCountryCurrency);

		sData.setTransactionType(row.getCell(20).getStringCellValue());

		// String amountStr = hssfRow.getCell(21).getStringCellValue();
		String amountStr = ExcelFileReader.retrieveNumericCellValue(row.getCell(21));

		if (amountStr == null || amountStr.equals("") || amountStr.equals("0")) {

			// String minStr = row.getCell(22).getStringCellValue();
			// String maxStr = row.getCell(23).getStringCellValue();

			String minStr = ExcelFileReader.retrieveNumericCellValue(row.getCell(22));
			String maxStr = ExcelFileReader.retrieveNumericCellValue(row.getCell(23));

			if (minStr == null || minStr.equals("") || maxStr == null || maxStr.equals("")) {

				sData.setAmount("10");

			} else {

				String amount = UtilFunctions.randomIntStr(Integer.valueOf(minStr).intValue(),
						Integer.valueOf(maxStr).intValue());

				sData.setAmount(amount);
			}

		} else {

			sData.setAmount(amountStr);
		}

		String feeCharge = ExcelFileReader.retrieveNumericCellValue(row.getCell(24));
		sData.setFee(feeCharge);

		sData.setServiceType(row.getCell(25).getStringCellValue());
		sData.setNaid(row.getCell(26).getStringCellValue());
		sData.setRefundMTCN(row.getCell(27).getStringCellValue());
		sData.setSenderAddress(readCellValue(row.getCell(28)));

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

			Cell cell = worksheet.getRow(sData.getRowNum()).getCell(0);
			cell.setCellValue(sData.getMTCN());

			cell = worksheet.getRow(sData.getRowNum()).getCell(27);
			cell.setCellValue(sData.getRefundMTCN());

			if (sData.isSenderNameAutoGen()) {

				cell = worksheet.getRow(sData.getRowNum()).getCell(1);
				cell.setCellValue(sData.getSenderName().getNameType().value());

				cell = worksheet.getRow(sData.getRowNum()).getCell(2);
				cell.setCellValue(sData.getSenderName().getFirstName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(3);
				cell.setCellValue(sData.getSenderName().getMiddleName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(4);
				cell.setCellValue(sData.getSenderName().getLastName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(5);
				cell.setCellValue(sData.getSenderName().getGivenName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(6);
				cell.setCellValue(sData.getSenderName().getPaternalName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(7);
				cell.setCellValue(sData.getSenderName().getMaternalName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(8);
				cell.setCellValue(sData.getSenderName().getBusinessName());
			}

			if (sData.isReceiverNameAutoGen()) {

				cell = worksheet.getRow(sData.getRowNum()).getCell(11);
				cell.setCellValue(sData.getReceiverName().getNameType().value());

				cell = worksheet.getRow(sData.getRowNum()).getCell(12);
				cell.setCellValue(sData.getReceiverName().getFirstName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(13);
				cell.setCellValue(sData.getReceiverName().getMiddleName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(14);
				cell.setCellValue(sData.getReceiverName().getLastName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(15);
				cell.setCellValue(sData.getReceiverName().getGivenName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(16);
				cell.setCellValue(sData.getReceiverName().getPaternalName());

				cell = worksheet.getRow(sData.getRowNum()).getCell(17);
				cell.setCellValue(sData.getReceiverName().getMaternalName());

			} /*
				 * else {
				 * 
				 * cell = worksheet.getRow(sData.getRowNum()).getCell(13);
				 * cell.setCellValue(sData.getReceiverName().getMiddleName());
				 * 
				 * cell = worksheet.getRow(sData.getRowNum()).getCell(15);
				 * cell.setCellValue(sData.getReceiverName().getGivenName());
				 * 
				 * cell = worksheet.getRow(sData.getRowNum()).getCell(16);
				 * cell.setCellValue(sData.getReceiverName().getPaternalName());
				 * 
				 * cell = worksheet.getRow(sData.getRowNum()).getCell(17);
				 * cell.setCellValue(sData.getReceiverName().getMaternalName()); }
				 */

			cell = worksheet.getRow(sData.getRowNum()).getCell(21);
			cell.setCellValue(sData.getAmount());

			cell = worksheet.getRow(sData.getRowNum()).getCell(24);
			cell.setCellValue(sData.getFee());
		}

		return true;
	}

	@Override
	public void buildDataRowsAppend(Sheet sheet) {
		// TODO Auto-generated method stub

	}
}
