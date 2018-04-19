package com.twb.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jingtongsdk.bean.Jingtong.reqrsp.Transaction;
import com.jingtongsdk.utils.JingtongRequstConstants;
import com.twb.data.SubscribeData;
import com.twb.entity.Withdrawal;
import com.twb.repository.WithdrawalRepository;
import com.twb.service.WithdrawalService;
import com.twb.utils.WithdrawalData;

@Service
public class WithdrawalServiceImp implements WithdrawalService
{

	private static final Logger logger = LoggerFactory.getLogger(WithdrawalServiceImp.class);

	@Autowired
	private WithdrawalRepository withdrawalRepository;

	@Transactional(rollbackFor = Exception.class)
	public Withdrawal handlerSubscribeMsg(String msg) throws Exception
	{
		SubscribeData sd = JingtongRequstConstants.PRETTY_PRINT_GSON.fromJson(msg, SubscribeData.class);
		if (sd == null)
		{
			logger.error("SubscribeData is null," + msg);
			return null;
		}
		if(!"Payment".equals(sd.getType()))
		{
			logger.info("SubscribeData is not Payment," + msg);
			return null;
		}
		Transaction transaction = sd.getTransaction();
		if (transaction == null)
		{
			logger.error("transaction is null," + msg);
			return null;
		}
		if (!sd.isSuccess())
		{
			logger.info("sd is not Success," + msg);
			return null;
		}
		if (!"received".equals(transaction.getType()))
		{
			logger.info("transaction is not received," + msg);
			return null;
		}
		
		Withdrawal withdrawal = new Withdrawal();
		withdrawal.setDate(new Date((long)(transaction.getDate())*1000));
		withdrawal.setAmountcurrency(transaction.getAmount().getCurrency());
		withdrawal.setAmountissuer(transaction.getAmount().getIssuer());
		withdrawal.setAmountvalue(transaction.getAmount().getValue());
		withdrawal.setFee(transaction.getFee());
		withdrawal.setHash(transaction.getHash());
		if(transaction.getMemos()!=null&&transaction.getMemos().length>0)
		{
			String memos = transaction.getMemos()[0];
			if(memos!=null&&memos.length()>1000)
			{
				memos = memos.substring(0, 1000);
			}
			withdrawal.setMemos(memos);
		}
		withdrawal.setCounterparty(transaction.getCounterparty());
		withdrawal.setState(withdrawal.STATE_TOBE_WITHDRAWAL);
		withdrawal.setDatasource(withdrawal.DATASOURCE_SOCKET);
		withdrawal=withdrawalRepository.save(withdrawal);
		return withdrawal;
	}

	@Override
	public void withdrawalForCheck() throws Exception
	{
		List<Withdrawal> list = withdrawalRepository.getWithdrawal1day();
		if(list!=null&&!list.isEmpty())
		{
			for(Withdrawal w :list)
			{
				WithdrawalData.add(w.getHash());
			}
		}
		
	}

}
