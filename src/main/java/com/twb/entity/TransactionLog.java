package com.twb.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "transaction_log") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class TransactionLog
{

	public static final String CHECKFLAG_TOBE = "1";// 1.待验证
	public static final String CHECKFLAG_SUCCESS = "2";// 2.验证成功
	public static final String CHECKFLAG_SOCKETNULL = "3";// 3.socket没有，待添加。
	public static final String CHECKFLAG_SOCKETNULL_ADD = "4";// 4socket没有，已添加
	public static final String CHECKFLAG_FAIL = "5";// 5.fail
	public static final String CHECKFLAG_NONEED = "6";// 6.无需比对

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	private Date date; // 交易时间

	private String hash; // 交易hash
	private String type; // 类型
	private String result; // 结果
	private String fee; // 交易费用，井通计价
	private String memos; // 交易备注
	private String counterparty; // 交易对家
	private String amountvalue; // 交易金额
	private String amountcurrency; // 货币类型
	private String amountissuer; // 货币发行方
	private String checkflag; // 定时任务验证socket 1.待比对验证
								// 2.验证成功3.socket没有，待添加。4.socket没有，已添加5.异常
	private String checkmsg; // 比对信息

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getHash()
	{
		return hash;
	}

	public void setHash(String hash)
	{
		this.hash = hash;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getFee()
	{
		return fee;
	}

	public void setFee(String fee)
	{
		this.fee = fee;
	}

	public String getMemos()
	{
		return memos;
	}

	public void setMemos(String memos)
	{
		
		this.memos = memos;
	}

	public String getCounterparty()
	{
		return counterparty;
	}

	public void setCounterparty(String counterparty)
	{
		this.counterparty = counterparty;
	}

	public String getAmountvalue()
	{
		return amountvalue;
	}

	public void setAmountvalue(String amountvalue)
	{
		this.amountvalue = amountvalue;
	}

	public String getAmountcurrency()
	{
		return amountcurrency;
	}

	public void setAmountcurrency(String amountcurrency)
	{
		this.amountcurrency = amountcurrency;
	}

	public String getAmountissuer()
	{
		return amountissuer;
	}

	public void setAmountissuer(String amountissuer)
	{
		this.amountissuer = amountissuer;
	}

	public String getCheckflag()
	{
		return checkflag;
	}

	public void setCheckflag(String checkflag)
	{
		this.checkflag = checkflag;
	}

	public String getCheckmsg()
	{
		return checkmsg;
	}

	public void setCheckmsg(String checkmsg)
	{
		this.checkmsg = checkmsg;
	}

}
