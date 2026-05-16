package com.wu.gw.model;

import java.util.HashMap;

public class APNDataDropDownOption {

	private String optionValue;
	
	private String optionText;
	
	private HashMap<String, APNDataDropDownOption> subDropdownOptions = null;

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public HashMap<String, APNDataDropDownOption> getSubDropdownOptions() {
		return subDropdownOptions;
	}

	public void setSubDropdownOptions(HashMap<String, APNDataDropDownOption> subDropdownOptions) {
		this.subDropdownOptions = subDropdownOptions;
	}
}
