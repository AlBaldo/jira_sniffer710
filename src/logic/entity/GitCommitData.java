package logic.entity;

import java.time.LocalDate;

import logic.utils.MyUtils;

public class GitCommitData {
	private String message; 
	private LocalDate date;
	private String id;
	private String author;
	private String issueId;
	
	public GitCommitData(String m, LocalDate d, String i, String a) {
		this.message = m;
		this.date = d;
		this.id = i;
		this.author = a;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public void findIssueIdInCommit(final String jiraName) {
		int index = this.message.indexOf(jiraName);
		int len = jiraName.length();
		
		if(index == -1) {
			this.issueId = "";
			return;
		}

		if(len == this.message.length() - 1){
			this.issueId = "";
			return;
		}
		
		for(int i = index + len; i < this.message.length(); i++) {
			char c = this.message.charAt(i);
			if(!MyUtils.isANumber(c)) {
				this.issueId = jiraName + this.message.substring(index + len, i);
				return;
			}
		}
		this.issueId = jiraName + this.message.substring(index + len + 1);
	}
	
	
}
