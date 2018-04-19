package com.twb.service;

import java.util.List;

import com.twb.entity.TransactionLog;

public interface TransactionLogService {
	
	/**
	 * 
	 * @Title: getTranFromJingtong   
	 * @Description: 根据地址，去井通拿数据直到lastTran这条
	 * @param: @param address
	 * @param: @param tran
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: List      
	 * @throws
	 */
	List<TransactionLog> getTranFromJingtong(String address,String lastHash) throws Exception;
	
	/**
	 * 
	 * @Title: getLastTranHash   
	 * @Description: 拿到最后一条hash
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: String      
	 * @throws
	 */
	
	String getLastTranHash() throws Exception;
	
	/**
	 * 
	 * @Title: getTobeCheckTran   
	 * @Description:  没有验证过的数据，放入待验证List
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: List      
	 * @throws
	 */
	List<TransactionLog> getTobeCheckTran() throws Exception;;
	
	
	/**
	 * 
	 * @Title: checkWithdrawal   
	 * @Description: 检查Withdrawal
	 * @param: @param list
	 * @param: @throws Exception      
	 * @return: List      
	 * @throws
	 */
	List checkWithdrawal(List<TransactionLog> list) throws Exception;;

}
