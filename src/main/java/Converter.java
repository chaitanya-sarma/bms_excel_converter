

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Converter {

	public static String convert(String inputFileName, String outputFileName, int year, String siteName) {
		String status = "Success!!";
		if (!new File(inputFileName).isFile()) {
			status = "Given input file: " + inputFileName + " is not valid.";
			return status;
		}
		
		if (!outputFileName.substring(outputFileName.lastIndexOf(".")).equals(".csv")) {
			status = "File-name should end with .csv.";
			return status;
		}
		
		ArrayList<FileLineEntry> outputFormat = new ArrayList<FileLineEntry>();
		outputFormat.add(new FileLineEntry("SiteYear", false, Integer.toString(year)));
		if (siteName.isEmpty()) {
			status = "Please enter Site-Name";
			return status;
		}
		outputFormat.add(new FileLineEntry("SiteName", false, siteName));

		prepareOutputLineFormat(inputFileName, outputFormat);
		try {
			BufferedWriter tempFileBufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
			writeHeader(outputFormat, tempFileBufferedWriter);

			// Write rest of lines
			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(inputFileName)));
			Sheet datatypeSheet = workbook.getSheetAt(1);
			Iterator<Row> iterator = datatypeSheet.iterator();
			// Ignore the first line as it is header information.
			if (iterator.hasNext()) {
				iterator.next();
			}
			while (iterator.hasNext()) {
				Row dataRow = iterator.next();
				Iterator<Cell> cellIterator = dataRow.cellIterator();
				ArrayList<String> row = new ArrayList<String>();
				processRow(cellIterator, row);
				StringBuilder outputLine = new StringBuilder();
				if (row.isEmpty())
					break;
				else {
					for (FileLineEntry outputEntry : outputFormat) {
						if (outputEntry.isIndex && outputEntry.getValue() != "-1") {
							outputLine.append(row.get((int) Float.parseFloat(outputEntry.getValue())));
							outputLine.append(",");
						} else if (outputEntry.getValue() != "-1") {
							outputLine.append(outputEntry.getValue());
							outputLine.append(",");
						} else {
							outputLine.append(",");
						}
					}
					tempFileBufferedWriter.write(outputLine.toString());
					tempFileBufferedWriter.write("\n");
				}

			}
			tempFileBufferedWriter.flush();
			tempFileBufferedWriter.close();
			workbook.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return status;
	}

	private static void writeHeader(ArrayList<FileLineEntry> outputFormat, BufferedWriter tempFileBufferedWriter)
			throws IOException {
		StringBuilder outputHeaderLine = new StringBuilder();

		// Print the first line.
		for (FileLineEntry outputHeader : outputFormat) {
			outputHeaderLine.append(outputHeader.getEntryName());
			outputHeaderLine.append(",");
		}
		tempFileBufferedWriter.write(outputHeaderLine.toString());
		tempFileBufferedWriter.write("\n");
	}

	/**
	 * Process sheet 1.
	 * 
	 * @param fileName
	 * @param outputFormat
	 */
	private static void prepareOutputLineFormat(String fileName, ArrayList<FileLineEntry> outputFormat) {
		try {

			ArrayList<String> requiredFieldsFromFirstSheet = new ArrayList<String>();
			requiredFieldsFromFirstSheet.add("STUDY TYPE");
			requiredFieldsFromFirstSheet.add("TRIAL_INSTANCE");
			requiredFieldsFromFirstSheet.add("FACTOR");
			requiredFieldsFromFirstSheet.add("VARIATE");

			outputFormat.add(new FileLineEntry("TrialType", false, "T/N")); // 2
			outputFormat.add(new FileLineEntry("TrialNumber", false, "-1")); // 3
			outputFormat.add(new FileLineEntry("PlotBarcode", true, "-1")); // 4
			outputFormat.add(new FileLineEntry("Column", false, "")); // 5
			outputFormat.add(new FileLineEntry("Row", false, "")); // 6
			outputFormat.add(new FileLineEntry("Replicate", true, "-1")); // 7
			outputFormat.add(new FileLineEntry("GenoType", true, "-1")); // 8
			outputFormat.add(new FileLineEntry("Pedigree", true, "-1")); // 9

			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
			Sheet datatypeSheet = hssfWorkbook.getSheetAt(0);

			Iterator<Row> iterator = datatypeSheet.iterator();

			int factorColumnsNum = 0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				ArrayList<String> row = new ArrayList<String>();
				// Read row and populate it into Row
				processRow(cellIterator, row);
				if (!row.isEmpty()) {
					if (row.get(0).equalsIgnoreCase(requiredFieldsFromFirstSheet.get(0))) {
						FileLineEntry flEntry = outputFormat.get(2);
						flEntry.setValue(row.get(1));
					}
					// Trial Number
					if (row.get(0).equalsIgnoreCase(requiredFieldsFromFirstSheet.get(1))) {
						FileLineEntry flEntry = outputFormat.get(3);
						flEntry.setValue(row.get(6));
					}
					if (row.get(0).equalsIgnoreCase(requiredFieldsFromFirstSheet.get(2))) {
						factorColumnsNum = processFactor(iterator, outputFormat);
					}
					if (row.get(0).equalsIgnoreCase(requiredFieldsFromFirstSheet.get(3))) {
						processVariate(iterator, outputFormat, factorColumnsNum);
						break;
					}
				}
			}
			hssfWorkbook.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Process the variate from sheet 1.
	 * 
	 * @param iterator
	 * @param outputFormat
	 * @param factorColumnsNum
	 */
	private static void processVariate(Iterator<Row> iterator, ArrayList<FileLineEntry> outputFormat,
			int factorColumnsNum) {
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			ArrayList<String> row = new ArrayList<String>();
			processRow(cellIterator, row);
			if (row.size() == 0) {
				break;
			}
			outputFormat.add(new FileLineEntry(row.get(0), true, Integer.toString(factorColumnsNum++))); // 2
		}

	}

	/**
	 * Read the Factor data from sheet 1.
	 * 
	 * @param iterator
	 * @param outputFormat
	 * @return
	 */
	private static int processFactor(Iterator<Row> iterator, ArrayList<FileLineEntry> outputFormat) {
		ArrayList<String> requiredFieldsFromFactor = new ArrayList<String>();
		requiredFieldsFromFactor.add("PLOT_NO");
		requiredFieldsFromFactor.add("Rep");
		requiredFieldsFromFactor.add("DESIGNATION");
		requiredFieldsFromFactor.add("CROSS");
		int noOfFactor = 0;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			ArrayList<String> row = new ArrayList<String>();
			processRow(cellIterator, row);
			if (row.size() == 0) {
				break;
			}
			if (requiredFieldsFromFactor.get(0).equalsIgnoreCase(row.get(0))) {
				FileLineEntry flEntry = outputFormat.get(4);
				flEntry.setValue(Integer.toString(noOfFactor));
			}
			if (requiredFieldsFromFactor.get(1).equalsIgnoreCase(row.get(0))) {
				FileLineEntry flEntry = outputFormat.get(7);
				flEntry.setValue(Integer.toString(noOfFactor));
			}
			if (requiredFieldsFromFactor.get(2).equalsIgnoreCase(row.get(0))) {
				FileLineEntry flEntry = outputFormat.get(8);
				flEntry.setValue(Integer.toString(noOfFactor));
			}
			if (requiredFieldsFromFactor.get(3).equalsIgnoreCase(row.get(0))) {
				FileLineEntry flEntry = outputFormat.get(9);
				flEntry.setValue(Integer.toString(noOfFactor));
			}

			noOfFactor++;
		}
		return noOfFactor;
	}

	/*
	 * Read row and populate it into Row
	 */
	@SuppressWarnings("deprecation")
	private static void processRow(Iterator<Cell> cellIterator, ArrayList<String> row) {
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				row.add(cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				row.add(String.valueOf(cell.getBooleanCellValue()));
				break;
			case Cell.CELL_TYPE_NUMERIC:
				row.add(String.valueOf(cell.getNumericCellValue()));
				break;
			}
		}

	}

}

class FileLineEntry {
	String entryName;
	boolean isIndex;
	String value;

	public FileLineEntry(String entryName, boolean isIndex, String value) {
		super();
		this.entryName = entryName;
		this.isIndex = isIndex;
		this.value = value;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}