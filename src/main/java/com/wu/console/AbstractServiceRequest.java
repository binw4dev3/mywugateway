package com.wu.console;

import com.wu.WUGWRuntime;
import com.wu.gw.util.UtilFunctions;

public abstract class AbstractServiceRequest {
	
	protected String requestID = null;
	protected String description = null;
	protected WUGWRuntime wugwRuntime;
	
	public AbstractServiceRequest() {
	}
	
	public boolean serve(WUGWRuntime wugwRuntime) {
		UtilFunctions.logger.info("\nServing...\n");
		this.wugwRuntime = wugwRuntime;
		boolean standby = doService();
		UtilFunctions.logger.info("\nFinished\n");
		
		return standby;
	}
	
	protected boolean doService() {
		return true;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
