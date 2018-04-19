package com.twb.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jingtongsdk.bean.Jingtong.reqrsp.Transaction;
import com.jingtongsdk.bean.Jingtong.reqrsp.TransactionAmount;
import com.jingtongsdk.bean.Jingtong.reqrsp.TransactionsRecordRequest;
import com.jingtongsdk.bean.Jingtong.reqrsp.TransactionsRecordResponse;
import com.jingtongsdk.utils.JingtongRequestUtils;
import com.twb.entity.TransactionLog;
import com.twb.entity.Withdrawal;
import com.twb.repository.TransactionLogRepository;
import com.twb.repository.WithdrawalRepository;
import com.twb.service.TransactionLogService;
import com.twb.utils.WithdrawalData;

@Service
public class TransactionLogServiceImp implements TransactionLogService
{

	private static final Logger logger = LoggerFactory.getLogger(TransactionLogServiceImp.class);

	@Autowired
	private TransactionLogRepository transactionLogRepository;

	@Autowired
	private WithdrawalRepository withdrawalRepository;

	@Transactional(rollbackFor = Exception.class)
	public void handlerSubscribeMsg(String msg) throws Exception
	{

	}

	@Transactional(rollbackFor = Exception.class)
	public List<TransactionLog> getTranFromJingtong(String address, String lastHash) throws Exception
	{
		logger.info("getTranFromJingtong:" + address + "," + lastHash);
		List<TransactionLog> list = new ArrayList();

		// 井通取出来的数据，在数据库中是否存在
		boolean checkLastHash = false;

		// 需要添加数据库的list
		List<Transaction> addList = new ArrayList<Transaction>();

		TransactionsRecordRequest oblr = new TransactionsRecordRequest();
		oblr.setAddress(address);
		while (!checkLastHash)
		{
			TransactionsRecordResponse jrr = (TransactionsRecordResponse) JingtongRequestUtils.sendRequest(oblr);
			if (jrr == null)
			{
				break;
			}

			// 接口返回的数据是根据时间倒序排列,最新的数据排在前面
			Transaction[] transactions = jrr.getTransactions();
			for (Transaction tran : transactions)
			{
				if (lastHash != null && lastHash.length() > 0 && lastHash.equals(tran.getHash()))
				{
					checkLastHash = true;
					break;
				}

				addList.add(tran);
			}

			// 下条数据marker
			if (jrr.getMarker() != null)
			{
				oblr.setMarker(jrr.getMarker());
			}
			else
			{
				break;
			}

		}

		Collections.reverse(addList);
		for (Transaction tran : addList)
		{
			TransactionLog transactionLog = new TransactionLog();
			TransactionAmount amount = tran.getAmount();
			transactionLog.setAmountcurrency(amount.getCurrency());
			transactionLog.setAmountissuer(amount.getIssuer());
			transactionLog.setAmountvalue(amount.getValue());
			
			transactionLog.setCounterparty(tran.getCounterparty());
			transactionLog.setDate(new Date((long) (tran.getDate()) * 1000));
			transactionLog.setFee(tran.getFee());
			transactionLog.setHash(tran.getHash());
			if (tran.getMemos() != null && tran.getMemos().length > 0)
			{
				String memos = tran.getMemos()[0];
				if (memos != null && memos.length() > 1000)
				{
					memos = memos.substring(0, 1000);
				}
				transactionLog.setMemos(memos);
			}

			transactionLog.setResult(tran.getResult());
			transactionLog.setType(tran.getType());
			
			// 成功，并且是接收别人支付,添加到待比对List
			if ("tesSUCCESS".equals(transactionLog.getResult()) && "received".equals(transactionLog.getType()))
			{
				list.add(transactionLog);
				transactionLog.setCheckflag(TransactionLog.CHECKFLAG_TOBE);
			}
			else
			{
				transactionLog.setCheckflag(TransactionLog.CHECKFLAG_NONEED);
			}
			transactionLogRepository.save(transactionLog);
		}
		return list;
	}

	@Override
	public String getLastTranHash() throws Exception
	{
		TransactionLog transactionlog = transactionLogRepository.getLastTran();
		if (transactionlog == null)
		{
			return "";
		}
		else
		{
			return transactionlog.getHash();
		}

	}

	@Override
	public List<TransactionLog> getTobeCheckTran() throws Exception
	{
		List list = transactionLogRepository.getCheckTranList();
		if (list == null)
		{
			return new ArrayList();
		}
		return list;
	}

	@Transactional(noRollbackFor = Exception.class)
	public List checkWithdrawal(List<TransactionLog> list) throws Exception
	{
		logger.info("checkWithdrawal start");
		if (list == null || list.isEmpty())
		{
			return new ArrayList();
		}

		// 要继续比对的list
		List continueList = new ArrayList();

		for (TransactionLog transaction : list)
		{
			
			
			
			try
			{
				//如果不是成功，或者是收账，无需比对
				if(!"tesSUCCESS".equals(transaction.getResult())||!"received".equals(transaction.getType()))
				{
					logger.warn("异常数据进行比对："+transaction.getHash());
					transaction.setCheckflag(TransactionLog.CHECKFLAG_NONEED);
					transactionLogRepository.save(transaction);
					continue;
				}
				
				// Withdrawal不存在
				if (WithdrawalData.remove(transaction.getHash()))
				{
					logger.info("socket已监听到，核对成功："+transaction.getHash());
					transaction.setCheckflag(TransactionLog.CHECKFLAG_SUCCESS);
				}
				else
				{
					if (TransactionLog.CHECKFLAG_SOCKETNULL.equals(transaction.getCheckflag()))
					{
						Withdrawal withdrawal = new Withdrawal();
						withdrawal.setDate(transaction.getDate());
						withdrawal.setAmountcurrency(transaction.getAmountcurrency());
						withdrawal.setAmountissuer(transaction.getAmountissuer());
						withdrawal.setAmountvalue(transaction.getAmountvalue());
						withdrawal.setFee(transaction.getFee());
						withdrawal.setHash(transaction.getHash());
						withdrawal.setMemos(transaction.getMemos());
						withdrawal.setCounterparty(transaction.getCounterparty());
						withdrawal.setState(withdrawal.STATE_TOBE_WITHDRAWAL);
						withdrawal.setDatasource(withdrawal.DATASOURCE_SCHEDULING);
						withdrawalRepository.save(withdrawal);
						logger.info("socket未监听到,已添加："+transaction.getHash());
						transaction.setCheckflag(TransactionLog.CHECKFLAG_SOCKETNULL_ADD);
					}
					else if (TransactionLog.CHECKFLAG_TOBE.equals(transaction.getCheckflag()))
					{
						logger.info("socket未监听到，待添加："+transaction.getHash());
						transaction.setCheckflag(TransactionLog.CHECKFLAG_SOCKETNULL);
						continueList.add(transaction);
					}
					else
					{
						logger.error("transaction has error data:" + transaction.getHash());

						transaction.setCheckmsg("transaction has error flag :" + transaction.getCheckflag());

						transaction.setCheckflag(TransactionLog.CHECKFLAG_FAIL);
					}
				}
				transactionLogRepository.save(transaction);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.toString() + "," + Arrays.toString(e.getStackTrace()));
				logger.error("withdrawal 添加错误：" + transaction.getHash());
				try
				{
					transaction.setCheckflag(TransactionLog.CHECKFLAG_FAIL);
					transaction.setCheckmsg(e.toString());
					transactionLogRepository.save(transaction);

				}
				catch (Exception e1)
				{
					logger.error(e1.toString() + "," + Arrays.toString(e1.getStackTrace()));
					e1.printStackTrace();
				}
			}

		}
		return continueList;

	}

}
