package com.twb.task;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twb.service.WithdrawalService;
import com.twb.socket.WebSocketChatClient;

@Component
public class WebSocketStartComponent 
{
	private static final Logger logger = LoggerFactory.getLogger(WebSocketStartComponent.class);

	@Autowired
	private WithdrawalService withdrawalService;


	@Value("${subscribe_address}")
	private String subscribeAddress;
	
	
	@Value("${jingtong_uri}")
	private String jingtongUri;
	
	
	@PostConstruct
	public void init()
	{
		logger.info("connectJingtong");
		try
		{
			WebSocketChatClient wcc = new WebSocketChatClient(withdrawalService, jingtongUri, subscribeAddress);
			wcc.connectJingtong();
			withdrawalService.withdrawalForCheck();
			
		}
		catch (Exception e)
		{
			logger.error("connectJingtong error!");
			e.printStackTrace();
			logger.error(e.toString() + "," + Arrays.toString(e.getStackTrace()));
		}
	}

}
