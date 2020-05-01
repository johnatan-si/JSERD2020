package br.ufmg.dcc.labsoft.jexpert.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.ufmg.dcc.labsoft.jexpert.util.JExpertUtils;

public class ActivityExtractor {

	public static void execute(String localDirectory) throws IOException, InterruptedException {
		BufferedWriter bw = null;
		FileWriter fw = null;

		System.out.println("##############################################");
		System.out.println("##	   Module: Activity Exctractor         ###");
		System.out.println("##############################################");
		System.out.println("Activity Extractor starting... localDirectory=" + localDirectory);
		File directory = new File(localDirectory);
		System.out.println("Library to extract: " + directory.getName());
		File[] files = directory.listFiles();
		
		String outputDirName = localDirectory + File.separatorChar + JExpertUtils.JEXPERT_ACTIVITIES_DIR;

		File outputDir = new File(outputDirName);
		if (outputDir.exists()) {
			FileUtils.forceDelete(outputDir);
		}
		FileUtils.forceMkdir(outputDir);

		if (files != null) {
			int length = files.length;

			for (int i = 0; i < length; ++i) {
				File f = files[i];

				double a = i;
				double c = length;
				double value = a / c;
				double percent = ((value) * 100);

				System.out.println("[" + percent + "%] GitHub project '" + f.getName() + "'... ");

				if (f.isDirectory()) {
					String outputFileName = outputDirName + File.separatorChar + f.getName() + "_extracted.csv";
					fw = new FileWriter(outputFileName);
					System.out.println("   ... extracting in " + outputFileName);
					bw = new BufferedWriter(fw);
					calc(f, bw, fw);
				}
			}

		}
	}

	private static void calc(File f, BufferedWriter bw, FileWriter fw) throws IOException, InterruptedException {

		File dir = new File(f.getAbsolutePath() + File.separatorChar);
		String[] extensions = new String[] { "java" };
		Process process = null;
		@SuppressWarnings("unchecked")
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		for (File file : files) {

			String[] cmd = null;
			if (JExpertUtils.isWindows()) {
				String[] cmdWin = { "PowerShell", "-Command",
						"cd " + dir.toString()
								+ "; git blame -l -c --date=iso --pretty=format:'\"%h\",\"%an\",\"%ad\",\"%s\"' "
								+ file.getCanonicalFile() };
				cmd = cmdWin;
			} else { // Linux or Mac
				String[] cmdLinux = { "/bin/sh", "-c",
						"cd " + dir.toString()
								+ "; git blame -l -c --date=iso --pretty=format:'\"%h\",\"%an\",\"%ad\",\"%s\"' "
								+ file.getCanonicalFile() };
				cmd = cmdLinux;
			}

			process = Runtime.getRuntime().exec(cmd);

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer output = new StringBuffer();

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
				bw.write(line + "\n");
			}

		}

		bw.close();
		fw.close();
	}
}