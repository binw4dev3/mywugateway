package com.wu.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface IExcelObjectDelegator {
	
	public Sheet parseXlsSheet(Workbook workbook);
	
	public Object parseDataFromRowXls(Row row);
	
	public void buildHeader(Sheet sheet);
	
	public void buildDataRows(Sheet sheet);
	
	public void buildDataRowsAppend(Sheet sheet);
	
	public boolean dataUpdate(Workbook workBook, Object updateData);
}