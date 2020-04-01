package logic.utils;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Log{
	private static Log l = null;
	private static Logger lg = null;
	
	private Log(){}
	
	public static synchronized Log getLog() {
		if(lg == null) {

			ConsoleHandler ch = null;
			
			l = new Log();
			
			lg = Logger.getLogger("jslog");
			

	        LogManager.getLogManager().reset();
	        lg.setLevel(Level.ALL);
	        
	        ch = new ConsoleHandler();
	        ch.setLevel(Level.FINE);
	        lg.addHandler(ch);
			
			try {
				FileHandler fh = new FileHandler("/logger.log", true);
				fh.setLevel(Level.SEVERE);
		        lg.addHandler(fh);
			}catch (IOException e) {
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(MyConstants.LOG_SETUP_ERROR + "file handler");
			}
			
			
		}
		return l;
	}
	
	public void severeMsg(String msg) {
		lg.log(Level.SEVERE, msg);
	}
	
	public void debugMsg(String msg) {
		lg.log(Level.WARNING, msg);
	}
	
	public void infoMsg(String msg) {
		lg.log(Level.FINE, msg);
	}

}
