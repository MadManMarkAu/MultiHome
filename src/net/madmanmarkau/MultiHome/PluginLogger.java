package net.madmanmarkau.MultiHome;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

// PluginLogger.java
// Provides some basic log-to-file procedures for plugins.

public class PluginLogger {
	String logFile;
	
	public PluginLogger(String pluginPath, String logFile) {
		// Make sure plugin folder exists
		File file = new File(pluginPath);
		if ( !(file.exists()) ) {
			file.mkdir();
		}

		// Compile log filename
		this.logFile = pluginPath + File.separator + logFile;
		writeLog("*** Logging start");
	}
	
	protected void finalize() {
		writeLog("*** Logging stop");
	}
	
	public void writeLog(String logText) {
		try {
			// Open the log file for output
			FileWriter fstream = new FileWriter(this.logFile, true);
			BufferedWriter writer = new BufferedWriter(fstream);

			// Get a time stamp string
			Date now = new Date();
			String timeStamp = now.toString();
			
			// Write the log with time stamp
			writer.write(timeStamp + ": " + logText);
			writer.newLine();

			// Close file
			writer.close();
		} catch (Exception e) {
		}
	}
}
