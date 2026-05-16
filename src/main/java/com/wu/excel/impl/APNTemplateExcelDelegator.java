package com.wu.excel.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.APNTemplateDataEntry;

public class APNTemplateExcelDelegator implements IExcelObjectDelegator {

	private ArrayList<APNTemplate> apnTemplateList = null;

	private int dataRowNum = 1;

	public APNTemplateExcelDelegator(ArrayList<APNTemplate> apnTemplateList) {
		this.apnTemplateList = apnTemplateList;
	}

	@Override
	public HSSFSheet parseXlsSheet(Workbook workbook) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseDataFromRowXls(Row row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void buildHeader(Sheet sheet) {

		Row head = sheet.createRow(0);

		ArrayList<String> headerList = new ArrayList<String>(
				Arrays.asList("Country/Currency", "Description", "Service Code", "Route", "Template ID", "Field Name", "Field Type", "Required?",
						"Max Length", "Cascade List Name", "Cascade List Level", "Dropdown List"));
		
		if (headerList != null) {
			for (int i = 0, n = headerList.size(); i < n; i++) {
				Cell cell = head.createCell(i);
				cell.setCellValue(headerList.get(i));
			}
		}
	}

	@Override
	public void buildDataRows(Sheet sheet) {
		if (apnTemplateList == null) {
			return;
		}
	
		for (int i = 0, n = apnTemplateList.size(); i < n; i++) {
			APNTemplate apnTemplate = apnTemplateList.get(i);
			HashMap<String, APNTemplateDataEntry> apnFields = apnTemplate.getDataFields();

			boolean wroteTemplateInfo = false;

			Iterator<String> fieldKeys = apnFields.keySet().iterator();
			ArrayList<APNTemplateDataEntry> fieldList = new ArrayList<APNTemplateDataEntry>();
			while (fieldKeys.hasNext()) {
				APNTemplateDataEntry apnfield = apnFields.get(fieldKeys.next());
				fieldList.add(apnfield);
			}
			
			fieldList.sort(new Comparator<APNTemplateDataEntry>() {
				@Override
				public int compare(APNTemplateDataEntry o1, APNTemplateDataEntry o2) {
					String lvlStr1 = o1.getCascadeListLevel();
					String lvlStr2 = o2.getCascadeListLevel();
					int lvl1 = 0;
					int lvl2 = 0;
					if(!lvlStr1.equals("")) {
						lvl1 = Integer.parseInt(lvlStr1);
					}
					if(!lvlStr2.equals("")) {
						lvl2 = Integer.parseInt(lvlStr2);
					}
					return lvl1 - lvl2;
				}
				
			});

			//while (fieldKeys.hasNext()) {
			for(APNTemplateDataEntry apnfield : fieldList) {
				//APNTemplateDataEntry apnfield = apnFields.get(fieldKeys.next());
				ArrayList<String> optionList = apnfield.getDropdownOptions();
				List<String> sortedOptionList = new ArrayList<String>();
				if(apnfield.isCascadeList()) {
					sortedOptionList.add("");
				} else {
					sortedOptionList = optionList.stream().sorted().collect(Collectors.toList());
				}
				
				boolean wroteFieldInfo = false;
				
				for(int j = 0, m = sortedOptionList.size(); j < m; j++) {
					Row dataRow = sheet.createRow(dataRowNum);
					dataRowNum++;
					Cell cell = null;
					if (!wroteTemplateInfo) {
						cell = dataRow.createCell(0);
						cell.setCellValue(apnTemplate.getCorridor());

						cell = dataRow.createCell(1);
						cell.setCellValue(apnTemplate.getServiceDesp());
						
						cell = dataRow.createCell(2);
						cell.setCellValue(apnTemplate.getServiceCode());
						
						cell = dataRow.createCell(3);
						cell.setCellValue(apnTemplate.getRoute());
						
						cell = dataRow.createCell(4);
						cell.setCellValue(apnTemplate.getTemplateID());
						
						wroteTemplateInfo = true;
						
					} else {
						cell = dataRow.createCell(0);
						cell.setCellValue("");

						cell = dataRow.createCell(1);
						cell.setCellValue("");

						cell = dataRow.createCell(2);
						cell.setCellValue("");
						
						cell = dataRow.createCell(3);
						cell.setCellValue("");

						cell = dataRow.createCell(4);
						cell.setCellValue("");
					}
					
					if (!wroteFieldInfo) {
						cell = dataRow.createCell(5);
						cell.setCellValue(apnfield.getFieldName());
	
						cell = dataRow.createCell(6);
						cell.setCellValue(apnfield.getFieldType());
						
						cell = dataRow.createCell(7);
						cell.setCellValue(apnfield.getRequired());
	
						cell = dataRow.createCell(8);
						cell.setCellValue(apnfield.getMaxLength());
	
						cell = dataRow.createCell(9);
						cell.setCellValue(apnfield.getCascadeListName());
						
						cell = dataRow.createCell(10);
						cell.setCellValue(apnfield.getCascadeListLevel());
						
						wroteFieldInfo = true;
						
					} else {
						cell = dataRow.createCell(5);
						cell.setCellValue("");
	
						cell = dataRow.createCell(6);
						cell.setCellValue("");
						
						cell = dataRow.createCell(7);
						cell.setCellValue("");
	
						cell = dataRow.createCell(8);
						cell.setCellValue("");
	
						cell = dataRow.createCell(9);
						cell.setCellValue("");
						
						cell = dataRow.createCell(10);
						cell.setCellValue("");
					}
					
					cell = dataRow.createCell(11);
					cell.setCellValue(sortedOptionList.get(j));
				}
			}
			
			HashMap<String, ArrayList<String>> dropdownListMap = apnTemplate.getDropdownListMap();
			Iterator<String> listKeys = dropdownListMap.keySet().iterator();
			while (listKeys.hasNext()) {
				String key = listKeys.next();
				Row dataRow = sheet.createRow(dataRowNum);
				dataRowNum++;
				Cell cell = null;
				for(int j = 0; j < 11; j++) {
					cell = dataRow.createCell(j);
					cell.setCellValue("");
				}
				
				cell = dataRow.createCell(11);
				cell.setCellValue("Cascade List - " + key);
				
				ArrayList<String> cascadeList = dropdownListMap.get(key);
				for(String option : cascadeList) {
					dataRow = sheet.createRow(dataRowNum);
					dataRowNum++;
					cell = null;
					for(int j = 0; j < 11; j++) {
						cell = dataRow.createCell(j);
						cell.setCellValue("");
					}
					
					cell = dataRow.createCell(11);
					cell.setCellValue(option);
				}
			}
		}
	}
	
	@Override
	public void buildDataRowsAppend(Sheet sheet) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean dataUpdate(Workbook workBook, Object updateData) {
		// TODO Auto-generated method stub
		return true;
	}
}
