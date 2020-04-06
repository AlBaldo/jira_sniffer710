package logic.utils;

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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JiraManager {
	
	public List<String> retrieveTicketsId(String url) throws IOException, JSONException {
		List<String> tickid = new ArrayList<String>();
		
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		
		String tmp = "";
		
		//Get JSON API for closed bugs w/ AV in the project
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
			j = i + MyConstants.FILES_PER_CYCLE;
			tmp = url + "&startAt=" + i.toString() + "&maxResults=" + j.toString();
			
			Log.getLog().infoMsg(tmp);			
			JSONObject json = readJsonFromUrl(tmp);
			JSONArray issues = json.getJSONArray("issues");			
			
			total = json.getInt("total");
			for (; i < total && i < j; i++) {
	            //Iterate through each bug
	            String key = issues.getJSONObject(i%1000).get("key").toString();
	            tickid.add(key);
	         }  
		}while (i < total);
		
   		return tickid;
	}

	public List<LocalDate> getOrderedDatesFromIssues(List<JSONArray> tickets) throws JSONException, ParseException {
		List<LocalDate> dts = new ArrayList<>();
		String tmpstr;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
		for(JSONArray ja : tickets) {
			
			int size = ja.length();
			for(int i = 0; i < size; i++) {
				tmpstr = ja.getJSONObject(i).getJSONObject(MyConstants.FIELDS_JSON).get(MyConstants.RES_DATE).toString();
				if(tmpstr.equals("null")) {
					dts.add(sdf.parse(ja.getJSONObject(i).getJSONObject(MyConstants.FIELDS_JSON).get("created").toString())
							.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				}else {
					dts.add(sdf.parse(ja.getJSONObject(i).getJSONObject(MyConstants.FIELDS_JSON).get(MyConstants.RES_DATE).toString())
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				}
			}
		}
		
		Collections.sort(dts, (o1, o2) -> {
			if (o1 == null || o2 == null)
				return 0;
			return o1.compareTo(o2);
		});
			
		return dts;
	}
	

	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		

		try (InputStream is = new URL(url).openStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));){
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		}
	}

	   
	public JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		
		try (InputStream is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));){
			
			String jsonText = readAll(rd);
			
			return new JSONArray(jsonText);
		}
	}
	   
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
		   sb.append((char) cp);
		}
		
		return sb.toString();
	}
}
