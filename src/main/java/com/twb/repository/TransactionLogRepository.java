package com.twb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twb.entity.TransactionLog;


//继承JpaRepository来完成对数据库的操作
public interface TransactionLogRepository extends JpaRepository<TransactionLog,Integer>{
	
	@Query(value="select o from TransactionLog o where checkflag = '1' or checkflag = '3'")
	public List<TransactionLog> getCheckTranList() throws Exception;
	
	@Query(value="select o from TransactionLog o where id = (select max(id) from TransactionLog)")
	public TransactionLog getLastTran() throws Exception;
	
	
	  
  
	
}
