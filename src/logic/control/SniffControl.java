package logic.control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.json.JSONException;

import com.opencsv.CSVWriter;

import logic.entity.ChartData;
import logic.entity.GitCommitData;
import logic.entity.JiraFilter;
import logic.utils.GitManager;
import logic.utils.Log;
import logic.utils.MyConstants;
import logic.utils.JiraManager;
import logic.utils.MyUtils;

public class SniffControl {
	
	private JiraFilter jf;

	static final String[] CSV_COLS = {"id", MyConstants.RES_DATE, "created"};

	public void snifJira(String jiraName, String projGUrl, List<String> resol, List<String> stat, List<String> typ) {
		
		jf = new JiraFilter(jiraName, projGUrl, resol, stat, typ);
		
		try {
			mergeGitAndJira();	
		} catch (IOException e) {
			MyUtils.fastAlert(MyConstants.OPS_M, MyConstants.UNAUTH_REQUEST);
		} catch (JSONException e) {
			Log.getLog().infoMsg(MyConstants.NO_MATCH_FOUND);
		} catch (ParseException e) {
			Log.getLog().debugMsg("Error parsing dates :(");
		}
		
	}

	private void mergeGitAndJira() throws IOException, JSONException, ParseException {
		List<String> tickets;
		
		JiraManager jm = new JiraManager();
			
		Log.getLog().infoMsg("Getting tickets id from Jira...");
		tickets = jm.retrieveTicketsId(jf.getUrl());
		Log.getLog().infoMsg("Done.");
		
		List<String[]> tickNdates = getIssueBugsWithDate(jf, tickets);
	
		if(tickNdates.isEmpty()) {
			MyUtils.fastAlert(":(", "Nothing to show");
		}
		
		
		List<LocalDate> bugDates = new ArrayList<LocalDate>();
		
		for(String[] x : tickNdates) {
			bugDates.add(LocalDate.parse(x[1]));
		}
		
		Collections.sort(bugDates, (o1, o2) -> {
			if (o1 == null || o2 == null)
				return 0;
			return o1.compareTo(o2);
		});
		
		List<ChartData> lcd = getChartDataFromDateList(bugDates);
		
		List<String[]> csvStr = new ArrayList<String[]>();
		
		for(ChartData cd : lcd) {
			String[] sa = new String[2];
			sa[0] = MyUtils.getMonthValueOf(cd.getMonth()) + " " + cd.getYear();
			sa[1] = cd.getY() + "";
			csvStr.add(sa);
		}
		
		String[] cols = {"YearMonth", "NumberOfBugs"};
		
		if(jf.getType().size() == 1 && jf.getType().get(0).equals("Bug")) {
				
			if(MyUtils.ynAlert("Export", "Do you want to create a result.csv file in: " + MyConstants.filepath + "?")) {
				Log.getLog().infoMsg("Exporting...");
				exportResultToCsv(MyConstants.filepath, cols, csvStr);
				Log.getLog().infoMsg("Export done.");
			}
			
			if(MyUtils.ynAlert("Graph function", "Do you want to visualize the embedded graph?")) {
				Log.getLog().infoMsg("About to show graph...");

				MyIssueGrapher mig = new MyIssueGrapher(lcd);
			
				Log.getLog().infoMsg("About to show graph");
				mig.showGraph("Bug graph: " + jf.getName(), "Bugs", "Time");
				
				Log.getLog().infoMsg("Here it is.");
			}
		}else {
			Log.getLog().infoMsg("Got the tickets");
			MyUtils.fastAlert("Unimplemented yet", "The tickets exist but tecnology is not there to show them.");
		}
	}

	private List<String[]> getIssueBugsWithDate(JiraFilter jf, List<String> tickets) {
		List<String[]> lcd = new ArrayList<String[]>();
		
		List<GitCommitData> log = getCommitDataFromLog(jf.getGitUrl(), tickets);

		getIssueIdForGitCommits(log, tickets);
		
		for(String s : tickets) {
			List<GitCommitData> curr = new ArrayList<GitCommitData>();
			for(GitCommitData g : log) {
				if(g.getIssueId().equals(s)) {
					curr.add(g);
				}
			}
			String[] dat = new String[2];
			dat[0] = s;
			
			if(curr.isEmpty()) {
				continue;
			}else if(curr.size() == 1) {
				dat[1] = curr.get(0).getDate().toString();
				lcd.add(dat);
			}else {
				dat[1] = curr.get(0).getDate().toString();
				LocalDate currdate = curr.get(0).getDate();
				
				for(GitCommitData gcdCurr : curr) {
					if(gcdCurr.getDate().isAfter(currdate)) {
						dat[1] = gcdCurr.getDate().toString();
					}
				}
				lcd.add(dat);
			}			
		}
		
		return lcd;
	}
	

	public void getIssueIdForGitCommits(List<GitCommitData> gcds, List<String> tic){
		Log.getLog().infoMsg("Parsing commits comments...");
		
		for(GitCommitData g : gcds) {
			g.findIssueIdInCommit(jf.getName() + "-");
		}
		
		Log.getLog().infoMsg("Done");
	}

	public List<GitCommitData> getCommitDataFromLog(String pgUrl, List<String> tickets) {
		try {
			GitManager gm = new GitManager(pgUrl);
			
			List<GitCommitData> log = gm.getLogForCurrentRepo();
			Log.getLog().infoMsg("Got log messages");
			
			return log;
			
			
		} catch (InvalidRemoteException e) {
			MyUtils.fastAlert("Error:", "InvalidRemoteException: " + e.getMessage());
		} catch (TransportException e) {
			MyUtils.fastAlert("Error:", "TransportException: " + e.getMessage());
		} catch (GitAPIException e) {
			MyUtils.fastAlert("Error:", "GitAPIException: " + e.getMessage());
		} catch (IOException e) {
			MyUtils.fastAlert("Error:", "IOException: " + e.getMessage());
		}
		return new ArrayList<GitCommitData>();
	}

	public void exportResultToCsv(String filepath, String[] cols, List<String[]> vals ) {
		
		File f = new File(filepath);
		
		try (CSVWriter cw = new CSVWriter(new BufferedWriter(new FileWriter(f)))){
			cw.writeNext(cols);
			
			for(String[] v : vals) {
				cw.writeNext(v);
			}
			
		} catch (IOException e) {
			MyUtils.fastAlert(MyConstants.OPS_M, MyConstants.UNAUTH_REQUEST);
		}
		MyUtils.fastAlert("Complete!", "Export completed with no errors :)");
	}

	public void showUrl(String name, List<String> resol, List<String> stat, List<String> typ) {
		MyUtils.fastAlert("Generated query URL", new JiraFilter(name, null, resol, stat, typ).getUrl());
	}

	
	
	/***support methods ahead***/
	private List<ChartData> getChartDataFromDateList(List<LocalDate> bugsDt) {
		List<ChartData> lcd = new ArrayList<>();
		int cursor = 0;
		
		int month;
		int year;
		int y;
		
		if(bugsDt.isEmpty()) {
			return lcd;
		}
		
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
				}else if(bugsDt.get(i-1).getMonthValue() > bugsDt.get(i).getMonthValue()) {
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

}
