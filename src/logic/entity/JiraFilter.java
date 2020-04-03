package logic.entity;

import java.util.List;

import logic.utils.Log;

public class JiraFilter {
	
	private String url;
	
	private List<String> resolution;
	private List<String> type;
	private List<String> status;
	
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
		StringBuilder sbuilder = new StringBuilder("https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + this.name + "%22");
		
		if(!type.isEmpty()) {
			sbuilder.append("AND" + addlistof("t"));
		}
		if(!status.isEmpty()) {
			sbuilder.append("AND" + addlistof("s"));
		}
		if(!resolution.isEmpty()) {
			sbuilder.append("AND" + addlistof("r"));
		}
		
		sbuilder.append("&fields=key,resolutiondate,versions,created");
		
		return sbuilder.toString();
		
	}

	private String addlistof(String code){
		StringBuilder tmp = new StringBuilder("");
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
			tmp.append("(%22");
		}else {
			tmp.append("%22");
		}
		
		for(int i = 0; i < li.size(); i++) {
			
			switch(code) {
				case "t":
					tmp.append("issueType%22=%22" + type.get(i) + "%22");
					break;
				case "s":
					tmp.append("status%22=%22" + status.get(i) + "%22");
					break;
				case "r":
					tmp.append("resolution%22=%22" + resolution.get(i) + "%22");
					break;
				default:
					Log.getLog().severeMsg("Code is not supposed to get here!");
					return "";
			}
			if(i + 1 != li.size()) {
				tmp.append("OR%22");
			}
		}
		if(li.size() > 1) {
			tmp.append(")");
		}
			
		return tmp.toString();	
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public List<String> getResolution() {
		return resolution;
	}


	public void setResolution(List<String> resolution) {
		this.resolution = resolution;
	}


	public List<String> getType() {
		return type;
	}


	public void setType(List<String> type) {
		this.type = type;
	}


	public List<String> getStatus() {
		return status;
	}


	public void setStatus(List<String> status) {
		this.status = status;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}



	
	
	
}
