package com.twb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@ComponentScan
public class JingtongwithdrawalAddApplication {

	public static void main(String[] args) {
		SpringApplication.run(JingtongwithdrawalAddApplication.class, args);
	}
}
