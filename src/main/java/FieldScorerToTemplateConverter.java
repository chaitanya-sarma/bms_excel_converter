
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class FieldScorerToTemplateConverter {
	private static final String FAILURE = "Failure!!";
	private static String status = FAILURE;

	public static String convert(String templateFileName, String fieldScorerFileName) {

		if (!new File(templateFileName).isFile()) {
			status = "Given template file: " + templateFileName + " is not valid.";
			return status;
		}

		if (!new File(fieldScorerFileName).isFile()) {
			status = "Given field-Scorer file: " + fieldScorerFileName + " is not valid.";
			return status;
		}

		if (!templateFileName.substring(templateFileName.lastIndexOf(".")).equals(".xls")) {
			status = "Please select the template file.\nFile-name should end with .xls.";
			return status;
		}

		if (!fieldScorerFileName.substring(fieldScorerFileName.lastIndexOf(".")).equals(".csv")) {
			status = "Please select the field-scorer file.\nFile-name should end with .csv.";
			return status;
		}

		ArrayList<VariateEntry> VariateList = new ArrayList<VariateEntry>();

		indexesRequired(templateFileName, VariateList);
		if(status != FAILURE)return status;
		findIndexes(VariateList, fieldScorerFileName);
		if(status != FAILURE)return status;
		getPlot_ID()
		try {
			BufferedWriter templateBufferedWriter = new BufferedWriter(new FileWriter(templateFileName));
			BufferedReader fieldScorerBufferedReader = new BufferedReader(new FileReader(fieldScorerFileName));
			
			// Fill data
			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(inputFileName)));
			Sheet datatypeSheet = workbook.getSheetAt(1);

			int noOfCellsPerRow = 0;
			boolean isFirstRow = true;
			for (Row dataRow : datatypeSheet) {

				if (isFirstRow) {
					/*
					 * As the first row is a header, the assumption is that
					 * every column need to have a header. CellIterator skips
					 * the cells without any format or any data. So by counting
					 * the no of headers we are sure to handle the blank spaces.
					 */
					noOfCellsPerRow = dataRow.getLastCellNum();
					isFirstRow = false;
				} else {
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
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return status;
	}

	@SuppressWarnings("deprecation")
	private static void processDataSheet(Row dataRow, ArrayList<String> row, int noOfCellsPerRow) {
		for (int i = 0; i < noOfCellsPerRow; i++) {
			if (dataRow.getCell(i) == null) {
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
					if (value == Math.round(value)) {
						row.add(Long.toString(Math.round(value)));
					} else {
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

	private static void findIndexes(ArrayList<VariateEntry> variateList, String fieldScorerFileName) {

		HSSFWorkbook hssfWorkbook;
		try {
			hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(fieldScorerFileName)));
		} catch (IOException e) {
			status = "Cannot open the fieldScorer file:" + fieldScorerFileName;
			return;
		}
		Sheet datatypeSheet = hssfWorkbook.getSheetAt(0);
		ArrayList<String> row = new ArrayList<String>();
		for (Row dataRow : datatypeSheet) {
			// Read first header row and populate it into Row
			Util.processRow(dataRow, row);
			break;
		}

		try {
			hssfWorkbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int variatePos = 0, noOfVariate = variateList.size();
		for (int i = 0; i < row.size(); i++) {
			
			if(isFieldRequired(row.get(i),i,variateList)){
				
			}
			
			
			
			
			if (variatePos > noOfVariate) {
				status = "No of variate entries are not same in template and fieldScorer file. \nPls check!!";
				return;
			} else if (row.get(i) == variateList.get(variatePos).getVariateName()) {
				variateList.get(variatePos).setIndexInTemplate(i);
				variatePos++;
			} else if ((row.get(i) != variateList.get(variatePos).getVariateName()) && (variatePos > 1)) {
				status = "No of variate entries are either not same or not in same order. \nPls check!!";
				return;
			}
		}
		if (variatePos <= noOfVariate) {
			status = "No of variate entries are either not same in template and fieldScorer file. \nPls check!!";
			return;
		}
	}

	private static boolean isFieldRequired(String string, int pos, ArrayList<VariateEntry> variateList) {
		for(VariateEntry entry:variateList){
			if(entry.getVariateName().equalsIgnoreCase(string)){
				entry.setIndexInTemplate(pos);
			}
		}
		return false;
		
	}

	/**
	 * Process sheet 1. Read complete sheet to identify the required
	 * information.
	 * 
	 * @param fileName
	 * @param variateList
	 */
	private static void indexesRequired(String templateFile, ArrayList<VariateEntry> variateList) {

		final String VARIATE = "VARIATE";
		final String FACTOR = "FACTOR";
		// variateList.add(new VariateEntry("GenoType", -1));
		variateList.add(new VariateEntry("PLOT_ID", -1));
		try {

			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(templateFile)));
			Sheet datatypeSheet = hssfWorkbook.getSheetAt(0);

			Iterator<Row> iterator = datatypeSheet.iterator();
			int noOfFactor = 0;
			boolean readNext = true;
			while (iterator.hasNext() && readNext) {
				Row nextRow = iterator.next();
				ArrayList<String> row = new ArrayList<String>();
				// Read row and populate it into Row
				Util.processRow(nextRow, row);
				if (!row.isEmpty()) {
					if (row.get(0).equalsIgnoreCase(FACTOR)) {
						noOfFactor = processFactor(iterator,variateList);
					}

					else if (row.get(0).equalsIgnoreCase(VARIATE)) {
						processVariate(iterator, variateList);
						readNext = false;
						break;
					}
				}
			}
			hssfWorkbook.close();
		} catch (IOException e) {
			status = "Could not process Description sheet of template File.\n Pls Check.";
		}

	}

	private static int processFactor(Iterator<Row> iterator, ArrayList<VariateEntry> variateList) {
		int noOfFactor = 0;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			ArrayList<String> row = new ArrayList<String>();
			Util.processRow(nextRow, row);
			if (row.size() == 0) {
				break;
			}
			if (row.get(0).equalsIgnoreCase("PLOT_ID")) {
				variateList.get(0).setIndexInTemplate(noOfFactor);
			}
			noOfFactor++;
		}

		return noOfFactor;
	}

	/**
	 * Process the variate from sheet 1 of template.
	 * 
	 * @param iterator
	 * @param outputFormat
	 * @param factorColumnsNum
	 */
	private static void processVariate(Iterator<Row> iterator, ArrayList<VariateEntry> variateList) {
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			ArrayList<String> row = new ArrayList<String>();
			Util.processRow(nextRow, row);
			if (row.size() == 0) {
				break;
			}
			variateList.add(new VariateEntry(row.get(0), -1)); // 2
		}

	}
}

class VariateEntry {
	private String variateName;
	private int indexInTemplate;

	public VariateEntry(String variate, int index) {
		super();
		setVariateName(variate);
		setIndexInTemplate(index);
	}

	public String getVariateName() {
		return variateName;
	}

	public void setVariateName(String variate) {
		this.variateName = variate;
	}

	public int getIndexInTemplate() {
		return indexInTemplate;
	}

	public void setIndexInTemplate(int indexInTemplate) {
		this.indexInTemplate = indexInTemplate;
	}

}
