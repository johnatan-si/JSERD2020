package br.ufmg.dcc.labsoft.jexpert.util;

import java.io.File;

public class JExpertUtils {
	
	public static final String JEXPERT_DIR = ".jexpert";
	
	public static final String JEXPERT_ACTIVITIES_DIR = JEXPERT_DIR + File.separatorChar + "activities";
	
	public static final String JEXPERT_METRICS_DIR = JEXPERT_DIR + File.separatorChar + "metrics";
	
	public static String[] JEXPERT_XLS_COLUMNS = { "Author", "# Imports to Library", "Total Imports of Commits",
			"# Commits to Library", "Total LOC of Commits", "Ratio of Imports",
			"# LOC to Library" };
	
	private static final String OS = System.getProperty("os.name").toLowerCase();
	
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}

	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}
	
}
