package com.mobile.rec;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mobile.constants.ApiConstants;

public class Info {

	//"https://api.stackexchange.com/2.2/info?site=stackoverflow";
	//"https://api.stackexchange.com/2.2/questions?order=desc&sort=activity&tagged=java&site=stackoverflow";
	// "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&site=stackoverflow";
	// http://search.twitter.com/search.json?q=%40apple
	public static String tDate,fDate;
	public static String tDateDisplay,fDateDisplay;
	public static String ITEMS = "items";
	public static String LINK = "link";
	public static String TITLE = "title";
	public static String GET = "GET";
	private String youroption,options;
	private static ArrayList<String> list;
	public static ArrayList<ArrayList<String>> figures;

	public static int curPage = 0;
	public static int pageSize = 100;
	private static boolean end = false;
	
	public static ArrayList<ArrayList<String>> getFigures() {
		return figures;
	}

	public static ArrayList<String> getList() throws IOException {

		return list;
	}

	public static JsonObject fileToDownload;
	public static String[] split;	
	public static ArrayList<ArrayList<String>> answers ;
	public static ArrayList<ArrayList<String>> getAnswers() {
		//System.out.println("Info get Answers: "+answers.size());
		return answers;
	}

	public static List<String> answersList= null;
	public static ArrayList<String> titles = null;

	public static ArrayList<String> getTitles() {
		return titles;
	}

	public String gettDate() {
		return tDate;
	}

	public String getfDate() {
		return fDate;
	}
	
	public int getcurPage() {
		return curPage;
	}
	
	public int getpageSize() {
		return pageSize;
	}

	public void setfDate(String fDate) throws ParseException {
		fDateDisplay = fDate;
		fDate = dateFormat(fDate);
		//System.out.println("formated From date is : "+fDate);
		Info.fDate = fDate;
	}

	public void settDate(String tDate) throws ParseException {		
		tDateDisplay = tDate;
		tDate = dateFormat(tDate);
		//System.out.println("formated TO date is : "+tDate);
		Info.tDate = tDate;
	}

	private String dateFormat(String inputDate) throws ParseException {
		//System.out.println("dateFormat");
		SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yyyy");	
		Date date = dt.parse(inputDate);
		Date stacticDate = dt.parse("01/01/1970");
		//System.out.println("stacticDate date is: "+stacticDate);
		long output = TimeUnit.MILLISECONDS.toSeconds(date.getTime()-stacticDate.getTime()) ;
		//System.out.println(inputDate+" >> input :output <<: "+output);
		String outDate = ""+output;
		return outDate;
	}


	public String getOptions() {
		//System.out.println(" options: "+options);
		return options;
	}

	public void setYouroption(String youroption)  {
		//System.out.println("setyourOptions: "+youroption);
		split = new String[2];
		if(youroption.contains(",")){
			split = youroption.split(",");
			System.out.println(split[0]+" "+split[1]);
		}else if(youroption.contains("Questions")){
			split[0] = "Questions";
			split[1] = null;
		}else{
			split[0] = null;
			split[1] = "Answers";
		}

		this.youroption = youroption;
	}

	public Info(){
	}

	public String execute() throws Exception {	
		list = new ArrayList<String>();
		if(list.size()==0)
			list = getValueRest();
		return "success";
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getYouroption() throws IOException {
		return youroption;
	}

	private static ArrayList<String> getValueRest() throws IOException, InterruptedException {
		ArrayList<String> value = new ArrayList<String>();
		while(!end){
			ApiConstants.setPage(curPage+1);
			URL url = new URL(ApiConstants.QUESTIONS);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();
			//System.out.println(request.getContentEncoding());
			InputStream jsonContent = new BufferedInputStream(new GZIPInputStream(request.getInputStream()));
			value = JsonReaderQuestions(jsonContent);
			++curPage;
			Thread.sleep(2000);
		}
		return value;
	}

	private static ArrayList<String> JsonReaderQuestions(InputStream jsonContent) throws IOException, InterruptedException {
		JsonParser parser = new JsonParser();
		//System.out.println("JsonReaderQuestions");
		titles = new ArrayList<String>();
		int count =0;
		ArrayList<String> httpcontent = new ArrayList<String>();
		JsonObject jsonObj,innerObj = null;
		
		String file_name_head = fDateDisplay+'-'+tDateDisplay;
		file_name_head = file_name_head.replace('/', '_');
		String qfile_name = "D:/ASU2015Spring/Sharon/Stack_data/question/"+
				file_name_head + "q[" + (curPage/100+1) + "].xls";
		FileWriter qfile = new FileWriter(qfile_name, true);
		
		String afile_name = "D:/ASU2015Spring/Sharon/Stack_data/answer/"+
				file_name_head + "a[" + (curPage/100+1) + "].xls";
		FileWriter afile = new FileWriter(afile_name, true);
		try {
			if(curPage % 100 == 0){
				qfile.close();
				afile.close();
				qfile_name = "D:/ASU2015Spring/Sharon/Stack_data/question/"+
						file_name_head + "q[" + (curPage/100+1) + "].xls";
				qfile = new FileWriter(qfile_name, true);
				qfile.write("question_id\tcreation_date\tview_count\tis_answered\tanswer_count"
						+ "\taccepted_answer_id\tscore\ttags\tlink\tasker_user_id\tasker_reputation"
						+ "\tasker_user_type\tasker_accept_rate\tasker_display_name\ttitle\tcontent\n");
				
				afile_name = "D:/ASU2015Spring/Sharon/Stack_data/answer/"+
						file_name_head + "a[" + (curPage/100+1) + "].xls";
				afile = new FileWriter(afile_name, true);
				afile.write("answer_id\tquestion_id\tcreation_date\tscore\tis_accepted\tanswer_user_id"
						+ "\tanswer_reputation\tanswer_user_type\tanswer_accept_rate\tanswer_display_name\tcontent\n");
			}
			jsonObj = (JsonObject)parser.parse(new InputStreamReader(
					jsonContent, "UTF-8"));
			fileToDownload = jsonObj;
			JsonArray lang= (JsonArray) jsonObj.get(ITEMS);
			boolean hasMore = jsonObj.get("has_more").getAsBoolean();
			int throttle_left = jsonObj.get("quota_remaining").getAsInt();
			String[] quesLinks = new String[lang.size()] ;
			ArrayList<String> quesIDs = new ArrayList<String>();
			Iterator<JsonElement> i = lang.iterator();
			while(i.hasNext()){
				innerObj = (JsonObject) i.next();
				
				quesLinks[count] = innerObj.get(LINK).toString().replace('"', ' ').trim();
				//System.out.println(quesLinks[count]);
				titles.add(innerObj.get(TITLE).toString().replace('"', ' ').trim());
				System.out.println("titles "+count+": "+titles.get(count));
				count++;
				
				String qID = parseQuestions(innerObj, qfile);
				quesIDs.add(qID);
			}
			
			if(!hasMore){
				end = true;
			}
			httpcontent = HttpParser(quesLinks); // used for parsing HTTP content
			System.out.println("rate left: "+throttle_left);
			System.out.println("current page: "+curPage);
			Date date = new java.util.Date(innerObj.get("creation_date").getAsLong()*1000);
			System.out.println("Date reach to: "+date);
			qfile.flush();
			if(throttle_left <= 0){
				for(int period = 0; period < 24; ++period){
					System.out.println(" "+(24 - period)+"minutes to wait");
					Thread.sleep(60*60*1000);
				}
			}
			if(split[1] != null)
				getAnswers(quesIDs, afile);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpcontent;
	}

	public static String parseQuestions(JsonObject innerObj, FileWriter qfile) throws IOException{
		String question_id = "";
		if(innerObj.has("question_id")){
			question_id = innerObj.get("question_id").toString();
		}
		String accepted_answer_id = "";
		if(innerObj.has("accepted_answer_id")){
			accepted_answer_id = innerObj.get("accepted_answer_id").toString();
		}
		
		JsonArray tags;
		String tag = "";
		if(innerObj.has("tags")){
			tags = (JsonArray)innerObj.get("tags");
			for(int tag_count = 0; tag_count<tags.size(); ++tag_count){
				tag = tag.concat(tags.get(tag_count).toString()+' ');
			}
		}
		
		JsonObject owner;
		String user_id = "";
		String reputation = "0";
		String user_type = "";
		String accept_rate = "0";
		String display_name = "";
		
		if(innerObj.has("owner")){
			owner = innerObj.getAsJsonObject("owner");
			if(innerObj.has("user_id")){
				user_id = owner.get("user_id").toString();
			}if(innerObj.has("reputation")){
				reputation = owner.get("reputation").toString();
			}if(innerObj.has("user_type")){
				user_type = owner.get("user_type").toString();
			}if(innerObj.has("accept_rate")){
				accept_rate = owner.get("accept_rate").toString();
			}if(innerObj.has("display_name")){
				display_name = owner.get("display_name").toString().replace('"', ' ').trim();
			}
		}
		String link = innerObj.get(LINK).toString().replace('"', ' ').trim();
		
		qfile.write(question_id+'\t'+
				innerObj.get("creation_date").toString()+'\t'+
				innerObj.get("view_count").toString()+'\t'+
				innerObj.get("is_answered").toString()+'\t'+
				innerObj.get("answer_count").toString()+'\t'+
				accepted_answer_id+'\t'+
				innerObj.get("score").toString()+'\t'+
				tag+'\t'+
				link+'\t'+
				user_id+'\t'+
				reputation+'\t'+
				user_type+'\t'+
				accept_rate+'\t'+
				display_name+'\t'+
				innerObj.get("title").toString().replace('"', ' ').trim()+'\t'+
				innerObj.get("body").toString().replace('"', ' ').trim()+'\n');
		
		return question_id;
		
	}
	
	public static void getAnswers(ArrayList<String> question_id, FileWriter afile) throws IOException, JsonParseException, InterruptedException{
		int aPage = 1;
		int aPageSize = 100;
		boolean end = false;
		String api_head = "https://api.stackexchange.com/2.2/questions/";
		String ids = "";
		for(int i=0; i<question_id.size(); ++i){
			if(i != 0)
				ids = ids.concat(";");
			ids = ids.concat(question_id.get(i));
		}
		while(!end){
			String api_tail = "/answers?page="+aPage+"&pagesize="+aPageSize+"&order=desc&sort=votes&site=stackoverflow&filter=withbody";
			URL url = new URL(api_head + ids + api_tail);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();
			//System.out.println(request.getContentEncoding());
			InputStream jsonContent = new BufferedInputStream(new GZIPInputStream(request.getInputStream()));
			end = JsonReaderAnswers(jsonContent, afile);
			++aPage;
		}
	}
	
	public static boolean JsonReaderAnswers(InputStream jsonContent, FileWriter afile) throws JsonParseException, IOException, InterruptedException{
		JsonParser parser = new JsonParser();
		JsonObject jsonObj,innerObj = null;
		jsonObj = (JsonObject)parser.parse(new InputStreamReader(
				jsonContent, "UTF-8"));
		JsonArray lang= (JsonArray) jsonObj.get(ITEMS);
		boolean hasMore = jsonObj.get("has_more").getAsBoolean();
		int throttle_left = jsonObj.get("quota_remaining").getAsInt();
		System.out.println("throttle left; "+throttle_left);
		Iterator<JsonElement> i = lang.iterator();
		while(i.hasNext()){
			innerObj = (JsonObject) i.next();
			parseAnswers(innerObj, afile);
		}
		if(throttle_left <= 0){
			for(int period = 0; period < 24; ++period){
				System.out.println(" "+(24 - period)+"minutes to wait");
				Thread.sleep(60*60*1000);
			}
		}
		return !hasMore;
	}
	
	public static void parseAnswers(JsonObject innerObj, FileWriter afile) throws IOException{
		JsonObject owner;
		String user_id = "";
		String reputation = "0";
		String user_type = "";
		String accept_rate = "0";
		String display_name = "";
		
		if(innerObj.has("owner")){
			owner = innerObj.getAsJsonObject("owner");
			if(innerObj.has("user_id")){
				user_id = owner.get("user_id").toString();
			}if(innerObj.has("reputation")){
				reputation = owner.get("reputation").toString();
			}if(innerObj.has("user_type")){
				user_type = owner.get("user_type").toString();
			}if(innerObj.has("accept_rate")){
				accept_rate = owner.get("accept_rate").toString();
			}if(innerObj.has("display_name")){
				display_name = owner.get("display_name").toString().replace('"', ' ').trim();
			}
		}
		
		afile.write(innerObj.get("answer_id").toString()+'\t'+
				innerObj.get("question_id").toString()+'\t'+
				innerObj.get("creation_date").toString()+'\t'+
				innerObj.get("score").toString()+'\t'+
				innerObj.get("is_accepted").toString()+'\t'+
				user_id+'\t'+
				reputation+'\t'+
				user_type+'\t'+
				accept_rate+'\t'+
				display_name+'\t'+
				innerObj.get("body").toString().replace('"', ' ').trim()+'\n');	
	}
	
	public static void setAnswersList(List<String> answersList) {
		Info.answersList = answersList;
	}

	public static void setTitles(ArrayList<String> titles) {
		Info.titles = titles;
	}

	private static ArrayList<String> HttpParser(String[] quesLink) throws IOException {

		ArrayList<String> out = new ArrayList<String>();
		return out;
	}
}
