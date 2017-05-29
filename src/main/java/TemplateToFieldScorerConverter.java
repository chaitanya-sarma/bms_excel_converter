
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class TemplateToFieldScorerConverter {
	private static final String FAILURE = "Failure!!";
	private static String status = FAILURE;
	
	public static String convert(String inputFileName, String outputFileName, int year, String siteName) {
		if (!new File(inputFileName).isFile()) {
			status = "Given input file: " + inputFileName + " is not valid.";
			return status;
		}

		if (!inputFileName.substring(inputFileName.lastIndexOf(".")).equals(".xls")) {
			status = "Please select the template file.\nFile-name should end with .xls.";
			return status;
		}

		if (!outputFileName.substring(outputFileName.lastIndexOf(".")).equals(".csv")) {
			status = "File-name should end with .csv.";
			return status;
		}
		
		ArrayList<FileLineEntry> outputFormat = new ArrayList<FileLineEntry>();
		if(year == 0)
				outputFormat.add(new FileLineEntry("SiteYear", false, Integer.toString(2000)));
			else
				outputFormat.add(new FileLineEntry("SiteYear", false, Integer.toString(year)));	
		if (siteName.isEmpty()) {
			status = "Please enter Site-Name";
			return status;
		}
		outputFormat.add(new FileLineEntry("SiteName", false, siteName));

		prepareOutputLineFormat(inputFileName, outputFormat);
		if(status != FAILURE)return status;
		try {
			BufferedWriter tempFileBufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
			writeHeader(outputFormat, tempFileBufferedWriter);
			if(status != FAILURE)return status;
			
			// Write rest of lines
			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(inputFileName)));
			Sheet datatypeSheet = workbook.getSheetAt(1);
	
			int noOfCellsPerRow = 0;
			boolean isFirstRow = true;
			for(Row dataRow : datatypeSheet) {
				
				if(isFirstRow){
					/*
					 * As the first row is a header, the assumption is that every column need to have a header.
					 * CellIterator skips the cells without any format or any data.
					 * So by counting the no of headers we are sure to handle the blank spaces.
					 */
					noOfCellsPerRow = dataRow.getLastCellNum();
					isFirstRow = false;
				}
				else{
					ArrayList<String> row = new ArrayList<String>();
					processDataSheet(dataRow, row, noOfCellsPerRow);
					
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
			}
			tempFileBufferedWriter.flush();
			tempFileBufferedWriter.close();
			workbook.close();
			status = "Success!!";
		} catch (IOException e) {
			status = "Please check the template file and retry.";
		}
		return status;
	}

	@SuppressWarnings("deprecation")
	private static void processDataSheet(Row dataRow, ArrayList<String> row, int noOfCellsPerRow) {
		for(int i =0 ; i < noOfCellsPerRow;i++){
			 if( dataRow.getCell(i) == null ){
                 row.add("");
             } else {
            	 switch (dataRow.getCell(i).getCellType()) {
            	 case Cell.CELL_TYPE_STRING:
     				row.add(dataRow.getCell(i).getStringCellValue());
     				break;
     			case Cell.CELL_TYPE_BOOLEAN:
     				row.add(Boolean.toString(dataRow.getCell(i).getBooleanCellValue()));
     				break;
     			case Cell.CELL_TYPE_NUMERIC:
     	        	 Double value = dataRow.getCell(i).getNumericCellValue();
                	 // If numeric check whether it is an integer/(float/double).
     	        	 if(value == Math.round(value)){
     	        		row.add(Long.toString(Math.round(value)));
                	 }else{
          				row.add(Double.toString(value));
                	 }
     				break;
     			case Cell.CELL_TYPE_BLANK:
     				row.add("");
     				break;

            	 }
             }
		}
	}

	private static void writeHeader(ArrayList<FileLineEntry> outputFormat, BufferedWriter tempFileBufferedWriter){
		StringBuilder outputHeaderLine = new StringBuilder();

		// Print the first line.
		for (FileLineEntry outputHeader : outputFormat) {
			outputHeaderLine.append(outputHeader.getEntryName());
			outputHeaderLine.append(",");
		}
		try {
			tempFileBufferedWriter.write(outputHeaderLine.toString());
			tempFileBufferedWriter.write("\n");
		} catch (IOException e) {
			status = "Exception in writing header to FieldScorer.\n Delete field scorer file and try again.";
		}
	}

	/**
	 * Process sheet 1.
	 * Read complete sheet to identify the required information.
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
			outputFormat.add(new FileLineEntry("ENTRY_TYPE", true, "-1"));//10
			outputFormat.add(new FileLineEntry("PLOT_ID", true, "-1"));//11

			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
			Sheet datatypeSheet = hssfWorkbook.getSheetAt(0);

			Iterator<Row> iterator = datatypeSheet.iterator();

			int factorColumnsNum = 0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				ArrayList<String> row = new ArrayList<String>();
				// Read row and populate it into Row
				Util.processRow(nextRow, row);
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
		} catch (IOException e) {
			status = "Issue with reading the Description sheet.\n Please check it.!!";
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
			ArrayList<String> row = new ArrayList<String>();
			Util.processRow(nextRow, row);
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
		requiredFieldsFromFactor.add("ENTRY_TYPE");
		requiredFieldsFromFactor.add("PLOT_ID");
		int noOfFactor = 0;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			ArrayList<String> row = new ArrayList<String>();
			Util.processRow(nextRow, row);
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
			if (requiredFieldsFromFactor.get(4).equalsIgnoreCase(row.get(0))) {
				FileLineEntry flEntry = outputFormat.get(10);
				flEntry.setValue(Integer.toString(noOfFactor));
			}
			if (requiredFieldsFromFactor.get(5).equalsIgnoreCase(row.get(0))) {
				FileLineEntry flEntry = outputFormat.get(11);
				flEntry.setValue(Integer.toString(noOfFactor));
			}

			noOfFactor++;
		}
		return noOfFactor;
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