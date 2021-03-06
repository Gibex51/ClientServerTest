package server;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import utils.*;

public class Statistic {
	
	private static final String STR_LOAD_STAT_NOFILE = "Statistic file not found";
	private static final String STR_LOAD_STAT_FAILED = "Statistic file not loaded: ";
	private static final String STR_LOAD_STAT_OK = "Statistic file loaded";
	
	private static final String STR_SAVE_STAT_NOTCREATED = "Statistic file not created: ";
	private static final String STR_SAVE_STAT_OK = "Statistic file saved";
	private static final String STR_SAVE_STAT_FAILED = "Statistic file not saved: ";
	
	private static final Map<String, Integer> fileStats = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static Timer timer = null;
	private static String customSharedDir = Strings.SHARED_DIRECTORY;
	
	public static boolean setCustomSharedDir(String path) {
		File file = new File(path);
		if (!file.exists()) file.mkdirs();
		if (!file.isDirectory()) return false;
		
		customSharedDir = file.getAbsolutePath();
		return true;
	}
	
	public static String getFileListFromSharedDirectory() {
		File folder = new File(customSharedDir);
        if (!folder.exists()) folder.mkdirs();
        File[] files = folder.listFiles(new FileFilter() {
    		@Override
    	    public boolean accept(File file) {
    	      return file.isFile();
    	    }
        });

        if (files.length == 0)
        	return "";
        StringBuilder builder = new StringBuilder();
        for(File f : files)
            builder.append(f.getName() + "\n");
		return builder.toString();
	}
	
	public static File getFileFromSharedDirectory(String fileName) {
		File file = new File(customSharedDir + Strings.PATH_SEP + FileUtils.filterFileName(fileName));
		if (file.exists()) {
			String absFilePath = file.getAbsolutePath();
			fileStats.put(absFilePath, fileStats.getOrDefault(absFilePath, 0) + 1);
		}
		return file;
	}
	
	public static String saveStatistic() {
		File file = new File(Strings.STAT_FILE);		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				return STR_SAVE_STAT_NOTCREATED + e.getMessage(); 
			}
		}
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (Entry<String, Integer> entry : fileStats.entrySet())
				writer.write(entry.getKey() + "=" + entry.getValue().toString());
			return STR_SAVE_STAT_OK;
		} catch (Exception e) {
			return STR_SAVE_STAT_FAILED + e.getMessage();
		}
	}
	
	public static void startSaveStatisticTimer(int periodInSeconds) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Statistic.saveStatistic();
			}
		}, periodInSeconds * 1000, periodInSeconds * 1000);
	}
	
	public static void stopSaveStatisticTimer() {
		if (timer == null) return;
		timer.cancel();
		timer = null;
	}
	
	public static String loadStatistic() {		
		File file = new File(Strings.STAT_FILE);		
		if (!file.exists())
			return STR_LOAD_STAT_NOFILE;
		
		try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				Integer count = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1, line.length()));
				String filePath = line.substring(0, line.lastIndexOf("="));
				fileStats.put(filePath, count);
			}		
			return STR_LOAD_STAT_OK;
		} catch (Exception e) {
			return STR_LOAD_STAT_FAILED + e.getMessage();
		}
	}
}
