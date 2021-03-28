package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import Model.PalletModel;

public class ExcelFile {

	private final static int NOT_FOUND = -1;
	private static long timeStamp = 0;
	private static Workbook workbook;
	private static HashMap<Integer, PalletModel> allRows;

	private static void ReadExcelFile() {
		File file = new File(Base.mainDbFile);
		FileInputStream streamFile = null;
		try {
			streamFile = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			workbook = new XSSFWorkbook(streamFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		timeStamp = file.lastModified();

		try {
			streamFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void CheckIfFileIsEdited() {
		File file = new File(Base.mainDbFile);
		long currentTimeStamp = file.lastModified();
		
		if (currentTimeStamp != timeStamp) {
			ReadExcelFile();
			ReadAllRows();
		}
	}

	public static void SaveData(PalletModel pm) throws IOException {
		FileInputStream file = new FileInputStream(new File(Base.mainDbFile));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);

//		Cell cell2Update = sheet.getRow(1).getCell(3);
//		cell2Update.setCellValue(49);

		XSSFCell cellToUpdate;

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(1);
		cellToUpdate.setCellValue(pm.getBatteryType());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(2);
		cellToUpdate.setCellValue(pm.getQuantityReal());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(3);
		cellToUpdate.setCellValue(pm.getQuantity());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(4);
		cellToUpdate.setCellValue(pm.getDestination());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(5);
		cellToUpdate.setCellValue(pm.getStatus());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(6);
		cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getIncomeDate()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(7);
		cellToUpdate.setCellValue(BaseMethods.FormatTime(pm.getIncomeTime()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(8);
		cellToUpdate.setCellValue(pm.getIsReserved());

		file.close();

		FileOutputStream outputStream = new FileOutputStream(Base.mainDbFile);
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	private static void ReadAllRows() {
		Sheet sheet = GetSheet();

		allRows = new HashMap<>();
		int i = 0;
		for (Row row : sheet) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(String.valueOf(i));
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case STRING:
					temp.add(cell.getStringCellValue());
					break;
				case NUMERIC:
					temp.add(String.valueOf(cell.getNumericCellValue()));
					break;
				case BOOLEAN:
					temp.add(String.valueOf(cell.getBooleanCellValue()));
					break;
				case BLANK:
					temp.add(" ");
					break;
				case _NONE:
					temp.add(" ");
					break;
				// case FORMULA: ... break;
				default:
					temp.add(" ");
				}
			}

			allRows.put(i, new PalletModel(temp));
			i++;
		}
	}

	private static Sheet GetSheet() {
		CheckIfFileIsEdited();
		Sheet sheet = workbook.getSheetAt(0);

		return sheet;
	}

	private static Cell GetCell(String column, int rowNumber, Sheet sheet) {
		CellReference cr = new CellReference(column + rowNumber);
		Row row = sheet.getRow(cr.getRow());
		Cell cell = row.getCell(cr.getCol());

		return cell;
	}
	
	public static String GetPalletName(int row) {
		CheckIfFileIsEdited();
		PalletModel pm;
		
		pm = allRows.get(row);
		
		return pm.getPalletName();
	}
	
	public static HashMap<Integer, PalletModel> GetAllRows(){
		CheckIfFileIsEdited();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		return data;
	}

	public static HashMap<Integer, PalletModel> GetAllFreePlaces() {
		CheckIfFileIsEdited();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (!pm.getStatus()) {
				it.remove();
			}
		}

		return data;
	}

	public static int GetFirstFreeRow() {
		int foundOnRow = NOT_FOUND;
		CheckIfFileIsEdited();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (pm.getStatus() && !pm.getIsReserved()) {
				foundOnRow = pm.getRow();
				break;
			}
		}

		return foundOnRow;
	}

	public static int GetPlaceRow(String pattern) {
		int foundOnRow = 0;
		CheckIfFileIsEdited();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (pm.getPalletName().equals(pattern)) {
				foundOnRow = pm.getRow();
				break;
			}
		}

		// System.out.println(foundOnRow);
		return foundOnRow;
	}

	public static int GetBatteryTypeRow(String pattern) {
		int foundOnRow = NOT_FOUND;
		CheckIfFileIsEdited();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (pm.getBatteryType().equals(pattern)) {
				foundOnRow = pm.getRow();
				break;
			}
		}

		// System.out.println(foundOnRow);
		return foundOnRow;
	}

	public static int GetClosestFreePlace(String pattern) {
		CheckIfFileIsEdited();
		int row = GetBatteryTypeRow(pattern);
		int i = row;
		int y = row;
		boolean found = false;
		int foundRow = NOT_FOUND;

		if (row != NOT_FOUND) {
			HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
			data.putAll(allRows);
			PalletModel pm;

			while (!found) {
				if (i > 0) {
					i--;
					pm = data.get(i);
					if ((pm.getBatteryType().isEmpty() || pm.getBatteryType().isBlank()) && !pm.getIsReserved()) {
						found = true;
						foundRow = i;
						break;
					}
				}

				if (y < data.size() - 1) {
					y++;
					pm = data.get(y);
					if ((pm.getBatteryType().isEmpty() || pm.getBatteryType().isBlank()) && !pm.getIsReserved()) {
						found = true;
						foundRow = y;
						break;
					}
				}

				if (i == 0 && y == data.size() - 1) {
					break;
				}
			}
		}
		return foundRow;
	}
}
