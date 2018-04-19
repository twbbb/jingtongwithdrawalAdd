package com.twb.service;

import com.twb.entity.Withdrawal;

public interface WithdrawalService {
	
	Withdrawal handlerSubscribeMsg(String msg) throws Exception;

	/**
	 * 
	 * @Title: withdrawalForCheck   
	 * @Description: withdrawal添加到缓存,用来与TransactionLog比对
	 * @param: @throws Exception      
	 * @return: void      
	 * @throws
	 */
	void withdrawalForCheck() throws Exception;
}
