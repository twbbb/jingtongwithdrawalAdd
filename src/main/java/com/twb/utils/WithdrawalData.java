package com.twb.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WithdrawalData
{
	private static Set withdrawalSet =  Collections.synchronizedSet(new HashSet());

	public static boolean check(Object obj)
	{
		return withdrawalSet.contains(obj);
	}
	
	public static boolean add(Object obj)
	{
		return withdrawalSet.add(obj);
	}
	
	public static boolean remove(Object obj)
	{
		return withdrawalSet.remove(obj);
	}
	
	public static int size()
	{
		return withdrawalSet.size();
	}
	
	
	
	
}
