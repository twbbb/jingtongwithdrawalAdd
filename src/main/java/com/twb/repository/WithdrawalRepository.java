package com.twb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twb.entity.Withdrawal;


//继承JpaRepository来完成对数据库的操作
public interface WithdrawalRepository extends JpaRepository<Withdrawal,Integer>{
	
  
	/**
	 * 
	 * @Title: getWithdrawalBefore15   
	 * @Description: 获取15分钟内数据
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: List<Withdrawal>      
	 * @throws
	 */
	@Query(value="select * from withdrawal WHERE date > DATE_SUB(NOW(), INTERVAL 15 MINUTE)",nativeQuery = true)
	public List<Withdrawal> getWithdrawalIn15m() throws Exception;
	
	@Query(value="select * from withdrawal WHERE date > DATE_SUB(NOW(), INTERVAL 1 DAY)",nativeQuery = true)
	public List<Withdrawal> getWithdrawal1day() throws Exception;
	
	@Query(value="select o from Withdrawal o")
	public List<Withdrawal> getAllWithdrawal() throws Exception;
	
}
