package com.wu.console;

import com.wu.WUGWRuntime;

public class ExitRequest extends AbstractServiceRequest {

	public ExitRequest() {
		super();
	}
	
	@Override
	protected boolean doService() {
		return false;
	}
}
