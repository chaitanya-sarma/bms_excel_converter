import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class FieldScorerToTemplateConverter {
	private static final String FAILURE = "Failure!!";
	private static String status;

	static String convert(String templateFileName, String fieldScorerFileName) {
		status = FAILURE;
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
		if (!status.equals(FAILURE))
			return status;
		findIndexesInTemplate(VariateList, templateFileName);
		findIndexesFromFieldScorer(VariateList, fieldScorerFileName);
		if (!status.equals(FAILURE))
			return status;
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		readFieldScorer(fieldScorerFileName, hm, VariateList);
		System.out.println(hm.size());
		ArrayList<String> extraPlot_IdTemplate = new ArrayList<String>();
		writeToTemplateFile(templateFileName, hm, VariateList, extraPlot_IdTemplate);
		if (!extraPlot_IdTemplate.isEmpty()) {
			status = "Missing plotid's in fieldscorer." + extraPlot_IdTemplate;
		}
		if (hm.size() > 0) {
			status = "Extra plotid's in fieldscorer." + hm.keySet();
		}
		if (status.equals(FAILURE))
			status = "SUCCESS!!";
		return status;
	}

	private static void writeToTemplateFile(String templateFileName, HashMap<String, ArrayList<String>> hm,
			ArrayList<VariateEntry> variateList, ArrayList<String> extraPlot_IdTemplate) {

		Workbook workbook;
		Sheet dataSheet;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(new File(templateFileName)));
			dataSheet = workbook.getSheetAt(1);
		} catch (IOException e) {
			status = "Unable to read Observation sheet of template file.";
			return;
		}
		try {

			boolean isFirstRow = true;

			for (Row dataRow : dataSheet) {
				ArrayList<String> row = new ArrayList<String>();
				// Read row and populate it into Row
				Util.processRow(dataRow, row);
				if (isFirstRow) {
					// As the first row is a header, the assumption is that
					isFirstRow = false;
				} else {
					String plotId = row.get(variateList.get(0).getIndexInTemplate());
					if (hm.get(plotId) == null)
						extraPlot_IdTemplate.add(plotId);
					else {
						ArrayList<String> dataList = hm.get(plotId);
						hm.remove(plotId);
						int dataListIndex = 0;
						for (VariateEntry entry : variateList) {
							Cell cell = dataRow.getCell(entry.getIndexInTemplate());
							if (cell == null)
								cell = dataRow.createCell(entry.getIndexInTemplate());
							cell.setCellValue(dataList.get(dataListIndex++));
						}
					}
				}
			}
			FileOutputStream outputStream = new FileOutputStream(templateFileName);
			workbook.write(outputStream);
			workbook.close();

		} catch (IOException e) {
			status = "Unable to read Observation sheet of template file.";
		}

	}

	private static void readFieldScorer(String fieldScorerFileName, HashMap<String, ArrayList<String>> hm,
			ArrayList<VariateEntry> variateList) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fieldScorerFileName));
		} catch (IOException e) {
			status = "Cannot open the fieldScorer file:" + fieldScorerFileName;
			return;
		}
		String[] row;
		String fileRow;
		try {
			boolean isFirst = true;
			while ((fileRow = br.readLine()) != null) {
				if (isFirst) {
					isFirst = false;
				} else {
					row = fileRow.split(",", -1);
					ArrayList<String> reqFields = new ArrayList<String>();
					for (VariateEntry entry : variateList) {
						if (entry.getIndexInFieldScorer() == -1) {
							reqFields.add("");
						} else {
							reqFields.add(row[entry.getIndexInFieldScorer()]);
						}
					}
					// Plot_ID is always at 0th location.
					hm.put(row[variateList.get(0).getIndexInFieldScorer()], reqFields);
				}
			}
			br.close();
		} catch (IOException e) {
			status = "Issue with reading fieldScorer file:" + fieldScorerFileName;
		}
	}

	private static void findIndexesInTemplate(ArrayList<VariateEntry> variateList, String templateFileName) {
		HSSFWorkbook hssfWorkbook;
		try {
			hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(templateFileName)));
		} catch (IOException e) {
			status = "Cannot open the template file:" + templateFileName;
			return;
		}
		Sheet dataSheet = hssfWorkbook.getSheetAt(1);
		ArrayList<String> row = new ArrayList<String>();
		Util.processRow(dataSheet.getRow(0), row);

		for (int i = 0; i < row.size(); i++) {
			int pos = isFieldRequired(row.get(i), variateList);
			if (pos != -1) {
				variateList.get(pos).setIndexInTemplate(i);
			}
		}
		try {
			hssfWorkbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void findIndexesFromFieldScorer(ArrayList<VariateEntry> variateList, String fieldScorerFileName) {

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fieldScorerFileName));
		} catch (IOException e) {
			status = "Cannot open the fieldScorer file:" + fieldScorerFileName;
			return;
		}
		String[] row;
		String fileRow;
		try {

			if ((fileRow = br.readLine()) != null) row = fileRow.split(",", -1);
			else {
                status = "Issue with reading fieldScorer file:" + fieldScorerFileName;
                br.close();
                return;
            }
		} catch (IOException e) {
			status = "Issue with reading fieldScorer file:" + fieldScorerFileName;
			return;
		}

		// From fieldScorer.
		for (int i = 0; i < row.length; i++) {
			int pos = isFieldRequired(row[i], variateList);
			if (pos != -1) {
				variateList.get(pos).setIndexInFieldScorer(i);
			}
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int isFieldRequired(String string, ArrayList<VariateEntry> variateList) {
		for (int i = 0; i < variateList.size(); i++) {
			if (variateList.get(i).getVariateName().equalsIgnoreCase(string)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Process sheet 1. Read complete sheet to identify the required
	 * information.
	 * 
	 */
	private static void indexesRequired(String templateFile, ArrayList<VariateEntry> variateList) {

		final String VARIATE = "VARIATE";
		// variateList.add(new VariateEntry("GenoType", -1));
		variateList.add(new VariateEntry("PLOT_ID", -1, -1));
		try {

			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(templateFile)));
			Sheet dataSheet = hssfWorkbook.getSheetAt(0);

			Iterator<Row> iterator = dataSheet.iterator();
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				ArrayList<String> row = new ArrayList<String>();
				// Read row and populate it into Row
				Util.processRow(nextRow, row);
				if (!row.isEmpty()) {
					if (row.get(0).equalsIgnoreCase(VARIATE)) {
						processVariate(iterator, variateList);
						break;
					}
				}
			}
			hssfWorkbook.close();
		} catch (IOException e) {
			status = "Could not process Description sheet of template File.\n Pls Check.";
		}

	}

	/**
	 * Process the variate from sheet 1 of template.
	 * 
	 */
	private static void processVariate(Iterator<Row> iterator, ArrayList<VariateEntry> variateList) {
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			ArrayList<String> row = new ArrayList<String>();
			Util.processRow(nextRow, row);
			if (row.size() == 0) {
				break;
			}
			variateList.add(new VariateEntry(row.get(0), -1, -1));
		}

	}
}

class VariateEntry {
	private String variateName;
	private int indexInTemplate;
	private int indexInFieldScorer;

	VariateEntry(String variate, int indexTemplate, int indexFieldScorer) {
		super();
		setVariateName(variate);
		setIndexInTemplate(indexTemplate);
		setIndexInFieldScorer(indexFieldScorer);
	}

	int getIndexInFieldScorer() {
		return indexInFieldScorer;
	}

	void setIndexInFieldScorer(int indexInFieldScorer) {
		this.indexInFieldScorer = indexInFieldScorer;
	}

	String getVariateName() {
		return variateName;
	}

	private void setVariateName(String variate) {
		this.variateName = variate;
	}

	int getIndexInTemplate() {
		return indexInTemplate;
	}

	void setIndexInTemplate(int indexInTemplate) {
		this.indexInTemplate = indexInTemplate;
	}

}
