package logic.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logic.entity.JiraFilter;
import logic.utils.Log;
import logic.utils.MyUtils;

public class SniffControl {

	public void snifJira(String name, List<String> resol, List<String> stat, List<String> typ) {
		List<JSONArray> tickets;
		
		JiraFilter jf = new JiraFilter(name, resol, stat, typ);
		
		try {
			tickets = retrieveTicketsId(jf.getUrl());
			Log.getLog().infoMsg("Got the tickets, eg: " + tickets.get(0).getJSONObject(0).get("key").toString());
		} catch (IOException e) {
			Log.getLog().infoMsg("Seem to be some aythorization missing :(");
		} catch (JSONException e) {
			Log.getLog().infoMsg("No match found :(");
		}
		
	}

	public void snifJira(String url) {
		
		MyUtils.fastAlert("", url);
	}
	
	public static List<JSONArray> retrieveTicketsId(String url) throws IOException, JSONException {
		   
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		
		List<JSONArray> t = new ArrayList<>();
		//Get JSON API for closed bugs w/ AV in the project
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
			j = i + 1000;
			url += "&startAt=" + i.toString() + "&maxResults=" + j.toString();
			
			Log.getLog().infoMsg(url);			
			JSONObject json = readJsonFromUrl(url);
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
