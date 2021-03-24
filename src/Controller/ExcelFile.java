package Controller;

import java.io.File;
import java.io.FileInputStream;
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

	private static String FREE = "FREE";
	private static int NOT_FOUND = -1;

	private static Workbook ReadExcelFile() throws IOException {
		FileInputStream file = new FileInputStream(new File(Base.dbFile));
		Workbook workbook = new XSSFWorkbook(file);

		file.close();
		return workbook;
	}

	public static void SaveData(int row, String batteryType, String quantityReal, String quantity, String destination,
			Boolean status, String date, String time, Boolean isReserved) throws IOException {
		FileInputStream file = new FileInputStream(new File(Base.dbFile));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);

//		Cell cell2Update = sheet.getRow(1).getCell(3);
//		cell2Update.setCellValue(49);

		XSSFCell cellToUpdate;

		cellToUpdate = sheet.getRow(row).getCell(1);
		cellToUpdate.setCellValue(batteryType);

		cellToUpdate = sheet.getRow(row).getCell(2);
		cellToUpdate.setCellValue(quantityReal);

		cellToUpdate = sheet.getRow(row).getCell(3);
		cellToUpdate.setCellValue(quantity);

		cellToUpdate = sheet.getRow(row).getCell(4);
		cellToUpdate.setCellValue(destination);

		cellToUpdate = sheet.getRow(row).getCell(5);
		cellToUpdate.setCellValue(status);

		cellToUpdate = sheet.getRow(row).getCell(6);
		cellToUpdate.setCellValue(date);

		cellToUpdate = sheet.getRow(row).getCell(7);
		cellToUpdate.setCellValue(time);

		cellToUpdate = sheet.getRow(row).getCell(8);
		cellToUpdate.setCellValue(isReserved);

		file.close();

		FileOutputStream outputStream = new FileOutputStream(Base.dbFile);
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	public static HashMap<Integer, PalletModel> GetAllRows() throws IOException {
		Sheet sheet = GetSheet();

		HashMap<Integer, PalletModel> data = new HashMap<>();
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

			data.put(i, new PalletModel(temp));
			i++;
		}
		return data;
	}

	private static Sheet GetSheet() throws IOException {
		Workbook workbook = ReadExcelFile();
		Sheet sheet = workbook.getSheetAt(0);

		return sheet;
	}

	private static Cell GetCell(String column, int rowNumber, Sheet sheet) {
		CellReference cr = new CellReference(column + rowNumber);
		Row row = sheet.getRow(cr.getRow());
		Cell cell = row.getCell(cr.getCol());

		return cell;
	}

	public static HashMap<Integer, PalletModel> GetAllFreePlaces() throws IOException {

		HashMap<Integer, PalletModel> data = GetAllRows();
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (!pm.getStatus().equals(FREE)) {
				it.remove();
			}
		}

		return data;
	}

	public static int GetPlaceRow(String pattern) throws IOException {

		int foundOnRow = 0;

		HashMap<Integer, PalletModel> data = GetAllRows();
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (pm.getPalletName().equals(pattern)) {
				foundOnRow = pm.getRow();
				break;
			}
		}

		System.out.println(foundOnRow);
		return foundOnRow;
	}

	public static int GetBatteryTypeRow(String pattern) throws IOException {

		int foundOnRow = NOT_FOUND;

		HashMap<Integer, PalletModel> data = GetAllRows();
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (pm.getBatteryType().equals(pattern)) {
				foundOnRow = pm.getRow();
				break;
			}
		}

		System.out.println(foundOnRow);
		return foundOnRow;
	}

	public static int GetClosestFreePlace(String pattern) throws IOException {
		int row = GetBatteryTypeRow(pattern);
		int i = row;
		int y = row;
		boolean found = false;
		int foundRow = -1;

		if (row == NOT_FOUND) {

		} else {
			HashMap<Integer, PalletModel> data = GetAllRows();
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
