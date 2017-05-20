package excelReader.excelReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Converter {

	public static void convert(String fileName, String siteYear, String siteName) {
		String outputFile = fileName.substring(0, fileName.lastIndexOf(".")) + "_processed"
				+ fileName.substring(fileName.lastIndexOf("."));
		HSSFWorkbook outputWorkbook = new HSSFWorkbook();
		HSSFSheet outputSheet = outputWorkbook.createSheet();
		ArrayList<FileLineEntry> outputFormat = new ArrayList<FileLineEntry>();
		outputFormat.add(new FileLineEntry("SiteYear", false, siteYear));
		outputFormat.add(new FileLineEntry("SiteName", false, siteName));

		prepareOutputLineFormat(fileName, outputFormat);

		try {
			Workbook workbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
			Sheet datatypeSheet = workbook.getSheetAt(1);

			int outputRowNum = 0;
			Row outputRow = outputSheet.createRow(outputRowNum++);
			int colNo = 0;
			// Write the first Line
			for (FileLineEntry outputHeader : outputFormat) {
				Cell outputCell = outputRow.createCell(colNo++);
				outputCell.setCellValue(outputHeader.getEntryName());
			}
			Iterator<Row> iterator = datatypeSheet.iterator();
			if (iterator.hasNext()) {
				iterator.next();
			}
			while (iterator.hasNext()) {
				colNo = 0;
				outputRow = outputSheet.createRow(outputRowNum++);
				Row dataRow = iterator.next();
				Iterator<Cell> cellIterator = dataRow.cellIterator();
				ArrayList<String> row = new ArrayList<String>();
				processRow(cellIterator, row);
				if (row.isEmpty())
					break;
				int i =0;
				for (FileLineEntry outputEntry : outputFormat) {
					Cell outputCell = outputRow.createCell(colNo++);
					if (outputEntry.isIndex) {
						if (outputEntry.getValue().equals("-1")) {
							outputCell.setCellValue("");
						} else {
							
							outputCell.setCellValue(row.get((int) Float.parseFloat(outputEntry.getValue())));
							if(i == 0){
								outputCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							}
						}
					} else {
						
						try {
							   Double value = Double.parseDouble(outputEntry.getValue());
							   outputCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							   outputCell.setCellValue(value.doubleValue());
							}
							catch(NumberFormatException nfe) {
							   outputCell.setCellValue(outputEntry.getValue());
							}
						
						outputCell.setCellValue(outputEntry.getValue());
					}
					i++;
				}

			}
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputWorkbook.write(outputStream);
			outputWorkbook.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNumeric(String str)
	  {
	    try
	    {
	      double d = Double.parseDouble(str);
	    }
	    catch(NumberFormatException nfe)
	    {
	      return false;
	    }
	    return true;
	  }
	private static void prepareOutputLineFormat(String fileName, ArrayList<FileLineEntry> outputFormat) {
		try {

			ArrayList<String> requiredFieldsFromFirstSheet = new ArrayList<String>();
			requiredFieldsFromFirstSheet.add("STUDY TYPE");
			requiredFieldsFromFirstSheet.add("TRIAL_INSTANCE");
			requiredFieldsFromFirstSheet.add("FACTOR");
			requiredFieldsFromFirstSheet.add("VARIATE");
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
			Sheet datatypeSheet = hssfWorkbook.getSheetAt(0);

			Iterator<Row> iterator = datatypeSheet.iterator();

			outputFormat.add(new FileLineEntry("TrialType", false, "T/N")); // 2
			outputFormat.add(new FileLineEntry("TrialNumber", false, "-1")); // 3
			outputFormat.add(new FileLineEntry("PlotBarcode", true, "-1")); // 4
			outputFormat.add(new FileLineEntry("Column", false, "")); // 5
			outputFormat.add(new FileLineEntry("Row", false, "")); // 6
			outputFormat.add(new FileLineEntry("Replicate", true, "-1")); // 7
			outputFormat.add(new FileLineEntry("GenoType", true, "-1")); // 8
			outputFormat.add(new FileLineEntry("Pedigree", true, "-1")); // 9

			int factorColumnsNum = 0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				ArrayList<String> row = new ArrayList<String>();
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

	// Read all the next lines till you reach an empty line
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