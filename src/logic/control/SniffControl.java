package logic.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logic.entity.ChartData;
import logic.entity.JiraFilter;
import logic.utils.Log;
import logic.utils.MyUtils;

public class SniffControl {

	public void snifJira(String name, List<String> resol, List<String> stat, List<String> typ) {
		List<JSONArray> tickets;
		
		JiraFilter jf = new JiraFilter(name, resol, stat, typ);
		
		try {
			tickets = retrieveTicketsId(jf.getUrl());
			
			if(typ.contains("Bug")) {
				
				List<LocalDate> bugsDt = getOrderedDatesFromIssues(tickets);
				
				List<ChartData> lcd = getChartDataFromDateList(bugsDt);

				MyIssueGrapher mig = new MyIssueGrapher("Bug graph: " + name, "Bugs", "Time", lcd);
				
				Log.getLog().infoMsg("About to show graph");
				mig.showGraph();
			}else {
				Log.getLog().infoMsg("Got the tickets, eg: " + tickets.get(0).getJSONObject(0).get("key").toString());	
			}
		} catch (IOException e) {
			MyUtils.fastAlert("Oops!", "Unauthorized request.");
		} catch (JSONException e) {
			Log.getLog().infoMsg("No match found :(");
		} catch (ParseException e) {
			Log.getLog().debugMsg("Error parsing dates :(");
		}
		
	}

	private List<ChartData> getChartDataFromDateList(List<LocalDate> bugsDt) {
		List<ChartData> lcd = new ArrayList<>();
		int cursor = 0;
		
		int month;
		int year;
		int y;
		
		do{
			month = bugsDt.get(cursor).getMonthValue();
			year = bugsDt.get(cursor).getYear();
			y = getNBugsForCurrent(bugsDt, cursor + 1);
			
			lcd.add(new ChartData(month, year, y));
			
			cursor += y;

		}while(cursor + 1 < bugsDt.size());
		
		return lcd;
	}

	private int getNBugsForCurrent(List<LocalDate> bugsDt, int cursor) {
		int n = 0;
		
		for(int i = cursor; i < bugsDt.size(); i++){
			n++;
			if(bugsDt.get(i-1).getYear() == bugsDt.get(i).getYear()){
				if(bugsDt.get(i-1).getMonthValue() < bugsDt.get(i).getMonthValue()){
					return n;
				}else if(bugsDt.get(i-1).getMonthValue() == bugsDt.get(i).getMonthValue()) {
					continue;
				}else {
					Log.getLog().debugMsg("Month error: dates should be sorted");
				}
				
			}else if(bugsDt.get(i-1).getYear() < bugsDt.get(i).getYear()){
				return n;
			}else {
				Log.getLog().debugMsg("Year error: dates should be sorted");
			}
		}
		
		return n;
	}

	private List<LocalDate> getOrderedDatesFromIssues(List<JSONArray> tickets) throws JSONException, ParseException {
		List<LocalDate> dts = new ArrayList<>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
		for(JSONArray ja : tickets) {
			
			int size = ja.length();
			for(int i = 0; i < size; i++) {

				dts.add(sdf.parse(ja.getJSONObject(i).getJSONObject("fields").get("resolutiondate").toString())
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			}
		}
		
		Collections.sort(dts, new Comparator<LocalDate>() {
			public int compare(LocalDate o1, LocalDate o2) {
				if (o1 == null || o2 == null)
					return 0;
				return o1.compareTo(o2);
			}
		});
			
		return dts;
	}

	public void snifJira(String url) {
		
		MyUtils.fastAlert("", url);
	}
	
	public static List<JSONArray> retrieveTicketsId(String url) throws IOException, JSONException {
		   
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		
		String tmp = "";
		
		List<JSONArray> t = new ArrayList<>();
		//Get JSON API for closed bugs w/ AV in the project
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
			j = i + 1000;
			tmp = url + "&startAt=" + i.toString() + "&maxResults=" + j.toString();
			
			Log.getLog().infoMsg(tmp);			
			JSONObject json = readJsonFromUrl(tmp);
			JSONArray issues = json.getJSONArray("issues");			
			
			total = json.getInt("total");
			t.add(issues);

			i += 1000;
			
		}while (i < total);
		
   		return t;
	}
	

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		

		try (InputStream is = new URL(url).openStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));){
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		}
	}

	   
	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		
		try (InputStream is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));){
			
			String jsonText = readAll(rd);
			
			return new JSONArray(jsonText);
		}
	}
	   
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
		   sb.append((char) cp);
		}
		
		return sb.toString();
	}


}
