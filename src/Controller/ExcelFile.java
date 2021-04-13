package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import Model.PalletModel;

public class ExcelFile {

	private final static int NOT_FOUND = -1;
	private final static String TABLE_NAME = "AllWarehouse";
	private static long timeStamp = 0;
	private static Workbook workbook;
	private static HashMap<Integer, PalletModel> allRows;
	private static Boolean tableExists = false;

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

	private static void CreateNewWorkbook(String fileName) {
		// Create Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet("Report");

		// Create file system using specific name
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(Base.mainReportAddress + fileName + ".xlsx"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// write operation workbook using file out object
			workbook.write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void FillOutcomeReport(PalletModel pm) throws IOException {
		
		String fileName = pm.getPalletName() + "_" + Base.dateFormat.format(LocalDate.now()) + "_" + Base.fileNameTimeFormat.format(LocalTime.now());
		
		CreateNewWorkbook(fileName);
		
		FileInputStream file = new FileInputStream(new File(Base.mainReportAddress + fileName + ".xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);

		// Create row object
		XSSFRow row;

		// This data needs to be written (Object[])
		Map<String, Object[]> empinfo = new TreeMap<String, Object[]>();
		empinfo.put("1", new Object[] { "PalletName", "BatteryType", "QuantityReal", "Quantity", "Destination",
				"IsFree", "Date", "Time", "IsReserved" });
		empinfo.put("2", new Object[] { pm.getPalletName(), pm.getBatteryType(), String.valueOf(pm.getQuantityReal()), String.valueOf(pm.getQuantity()),
				pm.getDestination(), pm.getStatus().toString(), Base.dateFormat.format(LocalDate.now()), Base.fileNameTimeFormat.format(LocalTime.now()), pm.getIsReserved().toString() });

		// Iterate over data and write to sheet
		Set<String> keyid = empinfo.keySet();
		int rowid = 0;

		for (String key : keyid) {
			row = sheet.createRow(rowid++);
			Object[] objectArr = empinfo.get(key);
			int cellid = 0;

			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}

		// Write the workbook in file system
		FileOutputStream out = new FileOutputStream(new File(Base.mainReportAddress + fileName + ".xlsx"));
		workbook.write(out);
		out.close();
	}

	public static void SaveData(PalletModel pm) throws IOException {
		FileInputStream file = new FileInputStream(new File(Base.mainDbFile));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);

//		Cell cell2Update = sheet.getRow(1).getCell(3);
//		cell2Update.setCellValue(49);

		XSSFCell cellToUpdate;

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getBatteryType());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getQuantityReal());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(3, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getQuantity());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(4, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getDestination());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getStatus());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(6, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getIncomeDate()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(7, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(BaseMethods.FormatTime(pm.getIncomeTime()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(8, MissingCellPolicy.CREATE_NULL_AS_BLANK);
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

			// Reading cells in this way because For loop and Iterator can handle with blank
			// cells
			for (int cn = 0; cn <= Base.LAST_ROW; cn++) {
				Cell cell = row.getCell(cn, MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell == null) {
					temp.add(" ");
				} else {
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
			}

			PalletModel pm = new PalletModel(temp);
			allRows.put(i, pm);
			FillTable(pm);
			i++;
		}
	}

	private static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String URL = "jdbc:derby:memory:warehouseDb;create=true";

		try {
			connection = DriverManager.getConnection(URL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

	private static void CreateTable() {
		if (!tableExists) {

			Connection connection = null;
			Statement statement = null;

			connection = getConnection();
			try {
				statement = connection.createStatement();
				// String query = "DROP TABLE AllWarehouse";
				// statement.execute(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String query = "CREATE TABLE " + TABLE_NAME + "( " + "Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
					+ "Row VARCHAR(10), " + "PalletName VARCHAR(50), " + "BatteryType VARCHAR(50), "
					+ "QuantityReal Int, " + "Quantity Int, " + "Destination VARCHAR(50), " + "IsFree VARCHAR(5), "
					+ "Date VARCHAR(50), " + "Time VARCHAR(50), " + "IsReserved VARCHAR(5), " + "PRIMARY KEY (Id))";
			try {
				statement.executeUpdate(query);
				tableExists = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void FillTable(PalletModel pm) {
		if (!tableExists) {
			CreateTable();
		}
		Connection connection = null;
		Statement statement = null;

		connection = getConnection();
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String query = "INSERT INTO " + TABLE_NAME
				+ "(Row, PalletName, BatteryType, QuantityReal, Quantity, Destination, IsFree, Date, Time, IsReserved) VALUES "
				+ "('" + pm.getRow() + "','" + pm.getPalletName() + "','" + pm.getBatteryType() + "',"
				+ pm.getQuantityReal() + "," + pm.getQuantity() + ",'" + pm.getDestination() + "','" + pm.getStatus()
				+ "','" + pm.getIncomeDate() + "','" + pm.getIncomeTime() + "','" + pm.getIsReserved() + "')";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void GetMemoryDb() {
		// CreateTable();
		// FillTable();
	}

	public static HashMap<Integer, PalletModel> FilterPallet(String text) {
		Connection connection = null;
		Statement statement = null;

		Set<Integer> set = new HashSet<Integer>();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);

		connection = getConnection();
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE PalletName='" + text + "'";
		ResultSet rs;
		try {
			rs = statement.executeQuery(query);
			while (rs.next()) {
				set.add(Integer.parseInt(rs.getString("Row")));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		data.keySet().retainAll(set);

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	public static HashMap<Integer, PalletModel> FilterBatteryType(String text) {
		Connection connection = null;
		Statement statement = null;

		Set<Integer> set = new HashSet<Integer>();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);

		connection = getConnection();
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE BatteryType='" + text + "'";
		ResultSet rs;
		try {
			rs = statement.executeQuery(query);
			while (rs.next()) {
				set.add(Integer.parseInt(rs.getString("Row")));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		data.keySet().retainAll(set);

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
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

	public static HashMap<Integer, PalletModel> GetAllRows() {
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

	public static HashMap<Integer, PalletModel> GetAllNotFreePlaces() {
		CheckIfFileIsEdited();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		PalletModel pm;

		for (Iterator<Map.Entry<Integer, PalletModel>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, PalletModel> entry = it.next();
			pm = entry.getValue();

			if (pm.getStatus() || pm.getIsReserved()) {
				it.remove();
			}
		}

		return data;
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
					if ((pm.getBatteryType().trim().isEmpty()) && !pm.getIsReserved()) {
						found = true;
						foundRow = i;
						break;
					}
				}

				if (y < data.size() - 1) {
					y++;
					pm = data.get(y);
					if ((pm.getBatteryType().trim().isEmpty()) && !pm.getIsReserved()) {
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