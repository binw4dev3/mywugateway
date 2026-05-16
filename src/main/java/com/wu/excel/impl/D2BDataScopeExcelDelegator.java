package com.wu.excel.impl;

import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.APNTemplateDataEntry;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;
import java.util.stream.Collectors;

public class D2BDataScopeExcelDelegator implements IExcelObjectDelegator {

    private ArrayList<APNTemplate> apnTemplateList = null;

    private int dataRowNum = 1;

    public D2BDataScopeExcelDelegator(ArrayList<APNTemplate> apnTemplateList) {
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

        /*ArrayList<String> headerList = new ArrayList<String>(
                Arrays.asList("Country/Currency", "Field Name", "Field Type", "Required?",
                        "Max Length", "Cascade List Name", "Cascade List Level"));*/

        ArrayList<String> headerList = new ArrayList<String>(
                Arrays.asList("Country/Currency", "Field Name", "Field Type", "Required?",
                        "Max Length", "Cascade List Name", "Cascade List Level"));

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

            for(APNTemplateDataEntry apnfield : fieldList) {
                boolean wroteFieldInfo = false;
                Row dataRow = sheet.createRow(dataRowNum);
                dataRowNum++;
                Cell cell = null;
                if (!wroteTemplateInfo) {
                    cell = dataRow.createCell(0);
                    cell.setCellValue(apnTemplate.getCorridor());
                    wroteTemplateInfo = true;
                } else {
                    cell = dataRow.createCell(0);
                    cell.setCellValue("");
                }

                cell = dataRow.createCell(1);
                String fieldName = apnfield.getFieldName();
                String gwFieldName = dataMapping.get(fieldName);
                if(gwFieldName != null) {
                    cell.setCellValue(gwFieldName);
                } else {
                    cell.setCellValue(fieldName);
                }
                cell = dataRow.createCell(2);
                String fieldType = apnfield.getFieldType();
                String translatedFieldType = dataTypeMapping.get(fieldType);
                if(translatedFieldType != null) {
                    cell.setCellValue(translatedFieldType);
                } else {
                    cell.setCellValue(fieldType);
                }
                cell = dataRow.createCell(3);
                cell.setCellValue(apnfield.getRequired());

                cell = dataRow.createCell(4);
                cell.setCellValue(apnfield.getMaxLength());

                cell = dataRow.createCell(5);
                cell.setCellValue(apnfield.getCascadeListName());

                cell = dataRow.createCell(6);
                cell.setCellValue(apnfield.getCascadeListLevel());
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

    private static HashMap<String, String> dataMapping = new HashMap<>();
    private static HashMap<String, String> dataTypeMapping = new HashMap<>();
    static {
        dataMapping.put("ABA Routing Number", "bank_details/routing_number");
        dataMapping.put("Account Number", "bank_details/account_number");
        dataMapping.put("Account Number Suffix", "bank_details/account_suffix");
        dataMapping.put("Account Type", "bank_details/account_type");
        dataMapping.put("Address", "receiver/address/addr_line1");
        dataMapping.put("Address 1", "receiver/address/addr_line1");
        dataMapping.put("Address 2", "receiver/address/addr_line2");
        dataMapping.put("Bank Account Number", "bank_details/account_number");
        dataMapping.put("Bank Account Number/CBU", "bank_details/account_number");
        dataMapping.put("Bank Card Number", "bank_details/account_number");
        dataMapping.put("Bank Code", "bank_details/bank_code");
        dataMapping.put("Bank Name", "bank_details/name");
        dataMapping.put("BBAN/IBAN", "bank_details/account_number");
        dataMapping.put("BIC", "bank_details/bic");
        dataMapping.put("Branch Code", "bank_details/bank_location");
        dataMapping.put("Branch Name", "bank_details/bank_location");
        dataMapping.put("Branch Name/Address", "bank_details/bank_location");
        dataMapping.put("BSB (Bank/State/Branch)", "bank_details/bank_location");
        dataMapping.put("City", "receiver/address/city");
        dataMapping.put("Country Code", "receiver/mobile_phone/phone_number/country_code");
        dataMapping.put("District", "bank_details/bank_location");
        dataMapping.put("Fiscal Code", "bank_details/rcv_financial_id");
        dataMapping.put("IBAN", "bank_details/account_number");
        dataMapping.put("IBAN / Account Number", "bank_details/account_number");
        dataMapping.put("IFSC Code", "bank_details/bank_code");
        dataMapping.put("Individual Identification Number (IIN)", "bank_details/rcv_financial_id");
        dataMapping.put("Institution Number", "bank_details/bank_code");
        dataMapping.put("Is Resident", "receiver/Is_a_resident");
        dataMapping.put("National ID Number (DNI)", "receiver/compliance_details/id_details/id_number");
        dataMapping.put("Postal Code", "receiver/address/postal_code");
        dataMapping.put("Purpose of Remittance", "reason_for_sending");
        dataMapping.put("Purpose of Send", "reason_for_sending");
        dataMapping.put("Purpose of Transaction", "reason_for_sending");
        dataMapping.put("Reason for remittance", "reason_for_sending");
        dataMapping.put("Receiver Address", "receiver/address/addr_line1");
        dataMapping.put("Receiver City", "receiver/address/city");
        dataMapping.put("Receiver Country Code", "receiver/mobile_phone/phone_number/country_code");
        dataMapping.put("Receiver Email", "receiver/email");
        dataMapping.put("Receiver Nationality", "receiver/nationality");
        dataMapping.put("Receiver Phone Number", "receiver/contact_phone");
        dataMapping.put("Receiver Province", "receiver/address/state");
        dataMapping.put("Receiver's Mobile Country Code", "receiver/mobile_phone/phone_number/country_code");
        dataMapping.put("Receiver's Mobile Number", "receiver/mobile_phone/phone_number/national_number");
        dataMapping.put("RUT (Tax ID)", "bank_details/rcv_financial_id");
        dataMapping.put("Sender Relationship to Receiver", "receiver/sender_relation_receiver");
        dataMapping.put("Sort Code", "bank_details/sort_code");
        dataMapping.put("State", "receiver/address/state");
        dataMapping.put("State/Province", "receiver/address/state");
        dataMapping.put("Tax ID #/(CUIL)", "bank_details/rcv_financial_id");
        dataMapping.put("Transit Number", "bank_details/branch_number");
        dataMapping.put("ZIP Code", "receiver/address/postal_code");
        dataMapping.put("Zip/Postal Code", "receiver/address/postal_code");

        dataTypeMapping.put("ALPHANUM", "文本字段");
        dataTypeMapping.put("DIGIT", "文本字段");
        dataTypeMapping.put("UPRSTRING", "文本字段");
        dataTypeMapping.put("DropDown List", "下拉菜单");
        dataTypeMapping.put("DropDown Cascade List", "级联下拉菜单");
    }
}
