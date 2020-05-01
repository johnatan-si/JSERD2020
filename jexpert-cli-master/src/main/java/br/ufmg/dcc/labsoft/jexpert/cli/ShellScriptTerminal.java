package br.ufmg.dcc.labsoft.jexpert.cli;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import br.ufmg.dcc.labsoft.jexpert.core.ActivityExtractor;
import br.ufmg.dcc.labsoft.jexpert.core.DeveloperDataAnalyzer;
import br.ufmg.dcc.labsoft.jexpert.core.MetricsCollector;

public class ShellScriptTerminal {

	public static void main(String args[]) throws InvalidFormatException {
		if (args == null || args.length < 2) {
			System.out.println("JExpert CLI usage: ");
			System.out.println("   java -jar jexpert-cli.jar <library> <local-directory>");
			System.exit(0);
		} else {

			System.out.println("JExpert CLI started...");
			try {
				long t0 = System.currentTimeMillis();
				String keywords = args[0];
				String localDirectory = args[1];
				ActivityExtractor.execute(localDirectory);
				String metricsOutputFile = DeveloperDataAnalyzer.execute(keywords, localDirectory);
				MetricsCollector.generateExpertList(metricsOutputFile, localDirectory);
				long t1 = System.currentTimeMillis();
				System.out.println("... JExpert CLI finished. (" + ((t1 - t0) / 1000) + "s)");
			} catch (IOException | InterruptedException e) {
				System.out.println("... JExpert CLI stoped (Error: " + e.getMessage() + ")");
			}
		}
	}
}