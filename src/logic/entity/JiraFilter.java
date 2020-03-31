package logic.entity;

import java.util.List;

import logic.utils.Log;

public class JiraFilter {
	
	private String url;
	
	private List<String> resolution, type, status;
	private String name;
	

	public JiraFilter(String n, List<String> resol, List<String> stat, List<String> typ) {
		this.name = n;
		this.resolution = resol;
		this.status = stat;
		this.type = typ;
		
		this.url = composeUrl();
	}
	

	public JiraFilter(String u){
		this.url = u;
		this.name = "";
		this.resolution = null;
		this.type = null;
		this.status = null;
	}
	
	private String composeUrl() {
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + this.name + "%22";
		
		if(type.size() > 0) {
			url += "AND" + addlistof("t");
		}
		if(status.size() > 0) {
			url += "AND" + addlistof("s");
		}
		if(resolution.size() > 0) {
			url += "AND" + addlistof("r");
		}
		
		url += "&fields=key,resolutiondate,versions,created";
		
		return url;
		
	}

	private String addlistof(String code){
		String tmp = "";
		List<String> li;
		
		switch(code) {
			case "t":
				li = this.type;
				break;
			case "s":
				li = this.status;
				break;
			case "r":
				li = this.resolution;
				break;
			default:
				Log.getLog().severeMsg("Code is not supposed to get here!");
				return "";
		}
		
		if(li.size() > 1) {
			tmp += "(%22";
		}else {
			tmp += "%22";
		}
		
		for(int i = 0; i < li.size(); i++) {
			
			switch(code) {
				case "t":
					tmp += "issueType%22=%22" + type.get(i) + "%22";
					break;
				case "s":
					tmp += "status%22=%22" + status.get(i) + "%22";
					break;
				case "r":
					tmp += "resolution%22=%22" + resolution.get(i) + "%22";
					break;
				default:
					Log.getLog().severeMsg("Code is not supposed to get here!");
					return "";
			}
			if(i + 1 != li.size()) {
				tmp += "OR%22";
			}
		}
		if(li.size() > 1) {
			tmp += ")";
		}
			
		return tmp;	
		
	}

	public String getUrl() {
		return url;
	}

}
