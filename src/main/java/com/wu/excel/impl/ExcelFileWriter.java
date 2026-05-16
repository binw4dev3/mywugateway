package com.wu.excel.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wu.excel.ExcelException;
import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.util.UtilFunctions;

public class ExcelFileWriter extends ExcelFileProcessor {

	@SuppressWarnings("finally")
	public static File writeToExcel(String path, IExcelObjectDelegator delegator) throws ExcelException, IOException {

		if (path == null || EMPTY.equals(path)) {

			throw new ExcelException(ExcelException.EMPTY_PATH_ERROR);

		} else {

			String postfix = getPostfix(path);
			
			Workbook workbook = null;

			if (!EMPTY.equals(postfix)) {

				UtilFunctions.logger.info(PROCESSING_FOR_WRITE + path);
				
				if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {

					workbook = new HSSFWorkbook();

				} else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {

					workbook = new XSSFWorkbook();
				}

			} else {

				throw new ExcelException(ExcelException.NOT_AN_EXCEL_FILE_ERROR);
			}
			
			Sheet sheet = workbook.createSheet();
			
			delegator.buildHeader(sheet);
			
			delegator.buildDataRows(sheet);

			FileOutputStream fileOut = null;

			File exportFile = null;
			
			try {
				
				exportFile = new File(path);

				if (!exportFile.exists()) {

					exportFile.createNewFile();

				}

				fileOut = new FileOutputStream(exportFile, true);

				workbook.write(fileOut);

				fileOut.flush();

			} catch (Exception e) {

				throw new ExcelException(ExcelException.EXPORT_EXCEL_ERROR, e.getMessage());

			} finally {
				
				try {
					
					if (null != fileOut) {
						
						fileOut.close();
					}
					
					if (null != workbook) {
						
						workbook.close();
						
					}
					
					return exportFile;
					
				} catch (final IOException e) {
					
					throw new ExcelException(ExcelException.CLOSE_EXCEL_ERROR, e.getMessage());
				}
			}
		}
	}
	
	@SuppressWarnings("finally")
	public static File writeToExcelAppend(String path, IExcelObjectDelegator delegator) throws ExcelException, IOException {

		if (path == null || EMPTY.equals(path)) {

			throw new ExcelException(ExcelException.EMPTY_PATH_ERROR);

		} else {

			FileInputStream excelFile = new FileInputStream(path);
			
			String postfix = getPostfix(path);
			
			Workbook workbook = null;

			if (!EMPTY.equals(postfix)) {

				UtilFunctions.logger.info(PROCESSING_FOR_WRITE + path);
				
				if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {

					workbook = new HSSFWorkbook(excelFile);

				} else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {

					workbook = new XSSFWorkbook(excelFile);
				}

			} else {

				excelFile.close();
				
				throw new ExcelException(ExcelException.NOT_AN_EXCEL_FILE_ERROR);
			}
			
			Sheet sheet = workbook.getSheetAt(0);
						
			delegator.buildDataRowsAppend(sheet);

			FileOutputStream fileOut = null;

			File exportFile = null;
			
			try {
				
				exportFile = new File(path);

				fileOut = new FileOutputStream(exportFile, true);

				workbook.write(fileOut);

				fileOut.flush();

			} catch (Exception e) {

				throw new ExcelException(ExcelException.EXPORT_EXCEL_ERROR, e.getMessage());

			} finally {
				
				try {
					
					excelFile.close();
					
					if (null != fileOut) {
						
						fileOut.close();
					}
					
					if (null != workbook) {
						
						workbook.close();
						
					}
					
					return exportFile;
					
				} catch (final IOException e) {
					
					throw new ExcelException(ExcelException.CLOSE_EXCEL_ERROR, e.getMessage());
				}
			}
		}
	}
}
