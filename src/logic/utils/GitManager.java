package logic.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import logic.entity.GitCommitData;

public class GitManager{
	private String defFolderPath;
	private String githubUrl;
	
	public GitManager(String pUrl) throws InvalidRemoteException, TransportException, GitAPIException {
		githubUrl = pUrl;
		defFolderPath  = System.getProperty("user.home") + "/Desktop/0-Magistrale/ISW2/Exam/Project_analysis" + parseProjName();
		
		checkIfRepoExists();
		
		this.cloneOrUpdate();
		
	}
	
	public List<GitCommitData> getLogForCurrentRepo() throws IOException, NoHeadException, GitAPIException {
		List<GitCommitData> logMessages = new ArrayList<>();
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repo = builder.setGitDir(new File(defFolderPath + "/.git")).setMustExist(true).build();
		RevCommit rev;
		PersonIdent pi;
		
		try(Git git = new Git(repo)){
			Iterable<RevCommit> log = git.log().call();
			for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
				rev = iterator.next();
				pi = rev.getAuthorIdent();
				Date d = pi.getWhen();
				LocalDate l = d.toInstant()
			      .atZone(pi.getTimeZone().toZoneId())
			      .toLocalDate();
				
				logMessages.add(new GitCommitData(rev.getFullMessage(), l, rev.getId() + "", pi.getName()));
			}
		}
		
		return logMessages;
	}

	private String parseProjName() {
		
		int i = githubUrl.length()-1;
		for(; i >= 0; i--) {
			if(githubUrl.charAt(i) == '/') {
				break;
			}
		}
		return githubUrl.substring(i, githubUrl.length()-4);
	}

	private void cloneOrUpdate() throws InvalidRemoteException, TransportException, GitAPIException{

		File dir = new File(defFolderPath);
		
	    if (!dir.exists()) dir.mkdirs();
	    
		if(dir.isDirectory() && dir.list().length <= 0){
			Log.getLog().infoMsg("Cloning repo...");
			Git.cloneRepository()
			  .setURI(githubUrl)
			  .setDirectory(dir)
			  .call();

			Log.getLog().infoMsg("Done.");
		} else {
			Log.getLog().infoMsg("Updating repo...");
			try (Git git = new Git(new FileRepository(defFolderPath + "/.git"))) {
			    PullResult call = git.pull().call();
				Log.getLog().infoMsg("Done: " + humanReadable(call));
			 } catch (IOException e) {
				 Log.getLog().infoMsg("IOException: " + e.getMessage());
			}

		}
		
	}

	private String humanReadable(PullResult call) {
		String c = call + "";
		int i = c.length() - 1;
		
		for(; i >= 0; i--) {
			if(c.charAt(i) == ':') {
				return c.substring(i+1, c.length());
			}
		}
		return call + "";
	}

	private void checkIfRepoExists() throws InvalidRemoteException, TransportException, GitAPIException{
		LsRemoteCommand lrc = new LsRemoteCommand(null);
		
		lrc.setRemote(githubUrl);
		lrc.call();
	}
}
