package com.mobile.constants;



import com.mobile.rec.Info;


public class ApiConstants {
	
	static String fDate;
	static String tDate;
	static int page;
	static int pageSize;
	static{
		
		Info info = new Info();
		fDate  = info.getfDate();
		tDate = info.gettDate();
		page = info.getcurPage();
		pageSize = info.getpageSize();
		
		//System.out.println(tDate+ " :Apiconstants values f date anf t date: "+fDate);
	}
	
	public static final String key = "2LPKeqys0JRDuPqHMt59MQ";
	public static String QUESTIONS;
	public static void setPage(int page){
		ApiConstants.page = page;
		QUESTIONS = "https://api.stackexchange.com/2.2/questions?page="+page+
				"&pagesize="+pageSize+"&fromdate="+fDate+"&todate="+tDate+
				"&order=desc&sort=creation&tagged=java&site=stackoverflow&"
				+ "filter=withbody&key=fA27AIi8HgzgbvBhE89UUg((";
		
		
	}
}
