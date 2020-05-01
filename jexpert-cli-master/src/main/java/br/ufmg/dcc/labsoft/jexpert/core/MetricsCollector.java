package br.ufmg.dcc.labsoft.jexpert.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Multimap;

import br.ufmg.dcc.labsoft.jexpert.util.JExpertUtils;

public class MetricsCollector {

	public static void generateExpertList(String metricsOutputFile, String localDirectory) throws IOException {
		System.out.println("##############################################");
		System.out.println("##	    Module: Metrics Collector          ###");
		System.out.println("##############################################");
		System.out.println("Metrics Collector consolidating... ");
		File directory = new File(localDirectory);
		String consolidatedMetricsOutputFile = localDirectory + File.separatorChar + JExpertUtils.JEXPERT_DIR
				+ File.separatorChar + directory.getName() + "-experts-list" + ".xlsx";
		System.out.println("    ... List of Experts=" + metricsOutputFile);

		FileUtils.copyFile(new File(metricsOutputFile), new File(consolidatedMetricsOutputFile));
	}

	public static void execute(String frameworkKeywords, String metricsOutputFile,
			Multimap<String, String> allCommitsSource, Map<String, Integer> mapCountFramework,
			Map<String, Integer> countImportsCommmitsFW, Map<String, Integer> uniqueCommits,
			Map<String, Integer> sourceCodeRelationFrame, Map<String, Integer> mapCountAllCommits,
			Map<String, Integer> importsGeneral) throws IOException {

		System.out.println("##############################################");
		System.out.println("##	    Module: Metrics Collector          ###");
		System.out.println("##############################################");
		System.out.println("Metrics Collector collecting... ");
		System.out.println("    ... metrics file=" + metricsOutputFile);

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
//		CreationHelper createHelper = workbook.getCreationHelper();

		// Create a Sheet
		Sheet sheetFrame = workbook.createSheet(frameworkKeywords);

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRowFrame = sheetFrame.createRow(0);
		// Creating cells
		for (int b = 0; b < JExpertUtils.JEXPERT_XLS_COLUMNS.length; b++) {

			Cell cell2 = headerRowFrame.createCell(b);
			cell2.setCellValue(JExpertUtils.JEXPERT_XLS_COLUMNS[b]);
			cell2.setCellStyle(headerCellStyle);
		}

		int rowNum = 1;
		float ratioLocImportGeneral;
		float locRelationFramework;
		float locAlteredFramework;

		try {
			for (String key : allCommitsSource.keySet()) {
//				System.out.println(key);

				if (mapCountFramework.containsKey(key)) {
					Row row = sheetFrame.createRow(rowNum++);
					row.createCell(0).setCellValue(key);

					if (mapCountFramework.containsKey(key)) {
						row.createCell(1).setCellValue(mapCountFramework.get(key));
					}
					if (countImportsCommmitsFW.containsKey(key)) {
						row.createCell(2).setCellValue(countImportsCommmitsFW.get(key));
					}

					/*
					 * if (importsGeneral.containsKey(key)) { //Imports General-All commits
					 * row.createCell(3).setCellValue(importsGeneral.get(key)); }
					 */
					if (uniqueCommits.containsKey(key)) {
						row.createCell(3).setCellValue(uniqueCommits.get(key));
					}
					/*
					 * LOC altered in Total if (mapCountAllCommits.containsKey(key)) {
					 * row.createCell(4).setCellValue(mapCountAllCommits.get(key)); }
					 */
					if (sourceCodeRelationFrame.containsKey(key)) {
						// row.createCell(6).setCellValue(sourceCodeRelationFrame.get(key));
						row.createCell(4)
								.setCellValue(sourceCodeRelationFrame.get(key) - countImportsCommmitsFW.get(key));
					}
					if (mapCountAllCommits.containsKey(key) & importsGeneral.containsKey(key)
							&& mapCountFramework.containsKey(key)) {

						locAlteredFramework = sourceCodeRelationFrame.get(key) - countImportsCommmitsFW.get(key);

						ratioLocImportGeneral = (float) mapCountFramework.get(key)
								/ (float) countImportsCommmitsFW.get(key);
						locRelationFramework = (float) ratioLocImportGeneral * (float) locAlteredFramework;

						row.createCell(5).setCellValue(round((float) ratioLocImportGeneral, 2));
						row.createCell(6).setCellValue(round(locRelationFramework, 2));

					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}

		for (int x = 0; x < JExpertUtils.JEXPERT_XLS_COLUMNS.length; x++) {
			sheetFrame.autoSizeColumn(x);
		}

		FileOutputStream fileOut = new FileOutputStream(metricsOutputFile);

		workbook.write(fileOut);

		fileOut.close();

		workbook.close();
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}