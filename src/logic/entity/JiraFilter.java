package logic.entity;

public class JiraFilter {
	
	private String url;
	private String resolution, type, status;
	private String name;
	
	public JiraFilter(String r, String t, String s) {
		this.resolution = r;
		this.type = t;
		this.status = s;
	}
	
	public JiraFilter(String url){
		
	}

}
