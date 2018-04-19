package com.twb.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.twb.entity.TransactionLog;
import com.twb.service.TransactionLogService;


@Component
public class TransactionLogTask
{
	Logger logger = LoggerFactory.getLogger(TransactionLogTask.class);

	@Resource
	TransactionLogService transactionLogService;

	public static boolean firstRun = true;

	@Value("${subscribe_address}")
	private String subscribeAddress;

	public static Set taskSet = new HashSet();

	public static List<TransactionLog> tobeCheckTranList = new ArrayList();

	@Scheduled(cron = "1 0/1 * * * ?")
	public void task()
	{

		logger.info("TransactionLogTask.task start");
		if (firstRun)
		{
			firstRun = false;
			logger.info("TransactionLogTask.task first run");
			// 没有验证过的数据，放入待验证List

			try
			{
				tobeCheckTranList = transactionLogService.getTobeCheckTran();
				
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
				tobeCheckTranList = new ArrayList();
			}
		}
		logger.info("tobeCheckTranList Size:"+tobeCheckTranList.size());
		
		
		String lastHash = "";
		// 获得最后一条数据的hash
		try
		{
			lastHash = transactionLogService.getLastTranHash();
		}
		catch (Exception e1)
		{
			lastHash = "";
			e1.printStackTrace();
		}
		
		logger.info("TransactionLogTask.task lastHash"+lastHash);

		
		try
		{
			List<TransactionLog> list = transactionLogService.getTranFromJingtong(subscribeAddress,
					lastHash);
			tobeCheckTranList.addAll(list);

		}
		catch (Exception e)
		{
			logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
		}
		logger.info("tobeCheckTranList Size:"+tobeCheckTranList.size());

		try
		{
			tobeCheckTranList = transactionLogService.checkWithdrawal(tobeCheckTranList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
		}
		logger.info("tobeCheckTranList Size:"+tobeCheckTranList.size());

		logger.info("TransactionLogTask.task end");

	}

}
