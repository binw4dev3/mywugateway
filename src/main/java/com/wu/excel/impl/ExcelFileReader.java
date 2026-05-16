package com.wu.excel.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wu.excel.ExcelException;
import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.util.UtilFunctions;

public class ExcelFileReader extends ExcelFileProcessor {

	public static ArrayList<Object> readFromExcel(String path, IExcelObjectDelegator delegator) throws ExcelException, IOException {
		
		if (path == null || EMPTY.equals(path)) {
			
			throw new ExcelException(ExcelException.EMPTY_PATH_ERROR);
			
		} else {
			
			String postfix = getPostfix(path);
			
			if (!EMPTY.equals(postfix)) {
				
				InputStream is = new FileInputStream(path);
				
				ArrayList<Object> list = null;
				
				UtilFunctions.logger.info(PROCESSING_FOR_READ + path);
				
				Workbook workbook = null;
				
				if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
					
					workbook = new HSSFWorkbook(is);
					
				} else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
					
					workbook = new XSSFWorkbook(is);					
				}
				
				list = parseXls(workbook, delegator);
				
				is.close();
				
				return list;
				
			} else {
				
				throw new ExcelException(ExcelException.NOT_AN_EXCEL_FILE_ERROR);
				
			}
		}
	}

	private static ArrayList<Object> parseXls(Workbook workBook, IExcelObjectDelegator delegator) throws IOException {

		ArrayList<Object> list = new ArrayList<Object>();

		// Read the Sheet
		Sheet sheet = delegator.parseXlsSheet(workBook);

		if (sheet == null) {
			return list;
		}

		// Read the Row
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row != null) {

				Object rowData = delegator.parseDataFromRowXls(row);

				if (rowData != null && !list.contains(rowData)) {
					list.add(rowData);
				}
			}
		}

		return list;
	}

	public static String parseCellValue(Cell cell) {

		if (cell == null) {

			return null;

		}

		String returnValue = null;

		switch (cell.getCellType()) {

			case NUMERIC:
				
				Double doubleValue = cell.getNumericCellValue();
	
				DecimalFormat df = new DecimalFormat("0");
				
				returnValue = df.format(doubleValue);
				
				break;
				
			case STRING:
				
				returnValue = cell.getStringCellValue();
				
				break;
				
			case BOOLEAN:
				
				Boolean booleanValue = cell.getBooleanCellValue();
				
				returnValue = booleanValue.toString();
				
				break;
				
			case BLANK:
				
				break;
				
			case FORMULA:
				
				returnValue = cell.getCellFormula();
				
				break;
				
			case ERROR:
				
				break;
				
			default:
				
				break;
				
			}
		
		return returnValue;
	}
	
	public static String retrieveNumericCellValue(Cell cell) {
		if(cell != null) {
			try {
				Double cellValue = cell.getNumericCellValue();
				Double dTemp = Math.floor(cellValue);
				Double rm = cellValue-dTemp;
				if(rm != 0) {
					return String.valueOf(cellValue);
				} else {
					return String.valueOf(Math.round(cellValue));
				}
			} catch(IllegalStateException e1) {
				try {
					String cellValue = cell.getStringCellValue();
					return cellValue;
				} catch(IllegalStateException e2) {
					
				}
			}
		}
		
		return "";
	}
}
