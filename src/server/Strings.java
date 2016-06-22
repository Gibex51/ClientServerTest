package server;

import java.io.File;

public final class Strings {
	public static final String PATH_SEP = File.separator;
	public static final String APP_HOME = System.getProperty("user.home") + PATH_SEP + "testapp";
	public static final String SHARED_DIRECTORY = APP_HOME + PATH_SEP + "shared";	
	public static final String STAT_FILE = APP_HOME + PATH_SEP + "stat.txt";
	public static final String LOG_FILE = APP_HOME + PATH_SEP + "serverlog.txt";
}
