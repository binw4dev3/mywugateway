package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;

public interface DASQueryService {
	public boolean execute(String data, WUTransaction txnData);
}
