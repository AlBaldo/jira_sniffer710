package logic.utils;

public class MyConstants {
	
	public static final String LOG_SETUP_ERROR = "Error configurating jslog: ";
	public static final String NO_MATCH_FOUND = "No match found :(";
	public static final String OPS_M = "OPS";
	public static final String UNAUTH_REQUEST = "Unauthorized request.";
	
	public static final String FIELDS_JSON = "fields";
	public static final String RES_DATE = "resolutiondate";
	
	public static final String[] CSV_COLS = {"id", MyConstants.RES_DATE, "created"};
	
	public static final int FILES_PER_CYCLE = 1000;
	
	private MyConstants(){}
}
