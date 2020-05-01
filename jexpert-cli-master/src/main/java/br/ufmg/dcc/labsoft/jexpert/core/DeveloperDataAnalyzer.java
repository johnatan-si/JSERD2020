package br.ufmg.dcc.labsoft.jexpert.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import br.ufmg.dcc.labsoft.jexpert.util.JExpertUtils;

public class DeveloperDataAnalyzer {

	private static Map<String, ArrayList<String>> stringsMap = new HashMap<String, ArrayList<String>>();

	public static String execute(String frameworkSearch, String localDirectory)
			throws IOException, InvalidFormatException, InterruptedException {

		System.out.println("##############################################");
		System.out.println("##	 Module: Developer Data Analyzer       ###");
		System.out.println("##############################################");
		System.out.println("Developer Data Analyzer starting... ");
		System.out.println("    ... localDirectory=" + localDirectory);
		File directory = new File(localDirectory);
		System.out.println("    ... library=" + directory.getName());
		System.out.println("    ... keywords=" + frameworkSearch);

		String saveOutputGeneral = localDirectory + File.separatorChar + JExpertUtils.JEXPERT_METRICS_DIR;
		File directoryOutput = new File(saveOutputGeneral);
		if (directoryOutput.exists()) {
			FileUtils.forceDelete(directoryOutput);
		}
		FileUtils.forceMkdir(directoryOutput);

		Map<String, Integer> mapCountFramework = new HashMap<>();
		Map<String, Integer> importsGeneral = new HashMap<>();
		Map<String, Integer> mapCountAllCommits = new HashMap<>();
		Multimap<String, String> hasMapimportsFramework = ArrayListMultimap.create();
		Multimap<String, String> importsCommmitsFw = ArrayListMultimap.create();
		Multimap<String, String> allCommitsSource = ArrayListMultimap.create();

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "	";

		String activitiesDir = localDirectory + File.separatorChar + JExpertUtils.JEXPERT_ACTIVITIES_DIR;
		File activitiesDirectory = new File(activitiesDir);
		File[] activitiesFiles = activitiesDirectory.listFiles();

		System.out.println("    ... activitiesDirectory=" + activitiesDir);

		String metricsOutputFile = null;
		if (activitiesFiles != null) {
			int length = activitiesFiles.length;
			for (int i = 0; i < length; ++i) {
				File f = activitiesFiles[i];

				double a = i;
				double c = length;
				double value = a / c;
				double percent = ((value) * 100);

				System.out.println("[" + percent + "%] Processing '" + f.getName() + "'... ");

				if (f.isFile()) {

					Boolean flag = false;

					try {

						br = new BufferedReader(new FileReader(f.getAbsoluteFile()));

						while ((line = br.readLine()) != null) {
							// use comma as separator
							String[] country = line.split(cvsSplitBy);
							if (country.length < 4) {
								continue;
							}
							country[3] = country[3].replaceAll("[()]", "").replaceAll("[0-9]", "");

							if (calcData(country[2].substring(0, 10).trim()) <= 1000) {
								allCommitsSource.put(country[1].replaceAll("[()]", "").trim(), country[0].trim());

								// cria a planilha com todos os imports daquele usuário
								if (isContain(country[3].toLowerCase(), "import")) {

									country[3] = country[3].replace("import", "").trim();
									// cria planilha com imports de angular no geral / com duplicados
									if (isContain(country[3].toLowerCase(), frameworkSearch)
											|| country[3].toLowerCase().contains(frameworkSearch)
											|| StringUtils.containsIgnoreCase(country[3], frameworkSearch)
											|| Pattern.compile(Pattern.quote(frameworkSearch), Pattern.CASE_INSENSITIVE)
													.matcher(country[3].toString()).find()) {

										incrementValueAuthor(mapCountFramework,
												country[1].replaceAll("[()]", "").trim());
										hasMapimportsFramework.put(country[1].replaceAll("[()]", "").trim(),
												country[0].trim());

										flag = true;
//										getEmail.searchUserEmail(fw, bw, diretorio.getPath() + "/",
//												f.getName().replaceAll("_output.csv", "/").trim(), saveOutputEmailUser,
//												country[0]);

									}
									incrementValueAuthor(importsGeneral,
											country[1].replaceAll("[()]", "").trim().toString());
									// hasMapimportsGeneral.put(country[1].replaceAll("[()]",//
									// "").trim(),country[0].trim());
									importsCommmitsFw.put(country[1].replaceAll("[()]", "").trim(), country[0].trim());
								}
								incrementValueAuthor(mapCountAllCommits,
										country[1].replaceAll("[()]", "").trim().toString());
							}
						}

						Map<String, Integer> countImportsCommmitsFW = new HashMap<>();

						for (String valueFr : Sets.newHashSet(hasMapimportsFramework.values())) {
							int count = Collections.frequency(new ArrayList<String>(importsCommmitsFw.values()),
									valueFr);
							if (count > 0) {
								String author = getKeyFromValue(importsCommmitsFw, valueFr).toString();

								if (countImportsCommmitsFW.get(author) == null
										|| countImportsCommmitsFW.get(author) == 0) {
									countImportsCommmitsFW.put(author, count);
								} else {
									countImportsCommmitsFW.put(author, countImportsCommmitsFW.get(author) + count);
								}
							}
						}

						Map<String, Integer> sourceCodeRelationFrame = new HashMap<>();
						// para remover registros duplicados
						for (String valueFr : Sets.newHashSet(hasMapimportsFramework.values())) {
							int count = Collections.frequency(new ArrayList<String>(allCommitsSource.values()),
									valueFr);
							if (count > 0) {
								String author = getKeyFromValue(allCommitsSource, valueFr).toString();
								if (sourceCodeRelationFrame.get(author) == null
										|| sourceCodeRelationFrame.get(author) == 0) {
									sourceCodeRelationFrame.put(author, count);
								} else {
									sourceCodeRelationFrame.put(author, sourceCodeRelationFrame.get(author) + count);
								}
							}
						}

						Map<String, Integer> uniqueCommits = new HashMap<>();
						// para remover registros duplicados
						for (String valueFr : Sets.newHashSet(hasMapimportsFramework.values())) {
							int count = Collections.frequency(
									new ArrayList<String>(Sets.newHashSet(hasMapimportsFramework.values())), valueFr);
							if (count > 0) {
								String author = getKeyFromValue(hasMapimportsFramework, valueFr).toString();

								if (uniqueCommits.get(author) == null || uniqueCommits.get(author) == 0) {
									uniqueCommits.put(author, count);
								} else {
									uniqueCommits.put(author, uniqueCommits.get(author) + count);
								}
							}
						}

						if (flag) {
							metricsOutputFile = saveOutputGeneral + File.separatorChar + f.getName();
							metricsOutputFile = metricsOutputFile.replaceAll(".csv", "") + ".xlsx";
							MetricsCollector.execute(frameworkSearch, metricsOutputFile, allCommitsSource,
									mapCountFramework, countImportsCommmitsFW, uniqueCommits, sourceCodeRelationFrame,
									mapCountAllCommits, importsGeneral);

						}

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (br != null) {
							try {
								br.close();

							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			mapCountFramework = new HashMap<>();
			importsGeneral = new HashMap<>();
		}
		return metricsOutputFile;
	}

	public static Object getKeyFromValue(Multimap<String, String> hm, String value) {
		for (String o : hm.keySet()) {

			if (hm.get(o).contains(value)) {
				return o;
			}
		}
		return null;
	}

	private static void incrementValueAuthor(Map<String, Integer> map, String key) {
		Integer count = map.get(key);

		if (count == null) {
			map.put(key, 1);
		} else {
			map.put(key, count + 1);
		}

	}

	public void add(String key, String value) {
		ArrayList<String> values = stringsMap.get(key);
		if (values == null) {
			values = new ArrayList<String>();
			stringsMap.put(key, values);
		}

		values.add(value);
	}

	private static boolean isContain(String source, String subItem) {
		String pattern = "\\b" + subItem + "\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		return m.find();
	}

	private static int calcData(String data) {
		int ano = Integer.parseInt(data.substring(0, 4));
		int mes = Integer.parseInt(data.substring(5, 7));
		int dia = Integer.parseInt(data.substring(8, 10));

		LocalDate dataCommit = LocalDate.of(ano, mes, dia);

		// Cria um Objeto LocalDate com a data 26/09/2020.
		LocalDate dataInicial = LocalDate.of(2020, 01, 30);

		// Calcula a diferença de meses entre as duas datas
		long diferencaEmMes = ChronoUnit.MONTHS.between(dataCommit, dataInicial);

		return Integer.parseInt("" + diferencaEmMes);
	}
}