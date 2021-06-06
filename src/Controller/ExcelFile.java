package controller;

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

import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import model.PalletModel;
import views.IncomeView;
import views.LoadingScreen;
import views.MainView;
import views.OutcomeView;

public class ExcelFile {

	private final static int NOT_FOUND = -1;
	private final static String TABLE_NAME = "AllWarehouse";
	private static long timeStamp = 0;
	private static Workbook workbook;
	private static HashMap<Integer, PalletModel> allRows;
	private static Boolean tableExists = false;

	private static int callingFrame;

	private static Connection mainConnection;
	private static Statement mainStatement;

	public static int getCallingFrame() {
		return callingFrame;
	}

	public static void setCallingFrame(int callingFrame) {
		ExcelFile.callingFrame = callingFrame;
	}

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

	public static synchronized void CheckIfFileIsEdited() {
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

	public static void FillOutcomeReport(List<PalletModel> pmList) {
		PalletModel pmTemp = pmList.get(0);
		int i = 1;

		String fileName = pmTemp.getPalletName() + "_" + Base.dateFormat.format(LocalDate.now()) + "_"
				+ Base.fileNameTimeFormat.format(LocalTime.now());

		CreateNewWorkbook(fileName);

		FileInputStream file = null;
		XSSFWorkbook workbook = null;
		try {
			file = new FileInputStream(new File(Base.mainReportAddress + fileName + ".xlsx"));
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XSSFSheet sheet = workbook.getSheetAt(0);

		// Create row object
		XSSFRow row;

		// This data needs to be written (Object[])
		Map<String, Object[]> empinfo = new TreeMap<String, Object[]>();
		empinfo.put(String.valueOf(i), new Object[] { "PalletName", "BatteryType", "QuantityReal", "Quantity",
				"Production Date", "Date", "Time" });

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pm = (PalletModel) itr.next();
			i++;
			empinfo.put(String.valueOf(i),
					new Object[] { pm.getPalletName(), pm.getBatteryType(), String.valueOf(pm.getQuantityReal()),
							String.valueOf(pm.getQuantity()), Base.dateFormat.format(pm.getProductionDate()),
							Base.dateFormat.format(LocalDate.now()), Base.fileNameTimeFormat.format(LocalTime.now()) });
		}

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
		FileOutputStream out;
		try {
			out = new FileOutputStream(new File(Base.mainReportAddress + fileName + ".xlsx"));
			workbook.write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SaveDataAtOutcome(List<PalletModel> pmList, int quantityLeft) {
		FileInputStream file = null;
		XSSFWorkbook workbook = null;
		try {
			file = new FileInputStream(new File(Base.mainDbFile));
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XSSFSheet sheet = workbook.getSheetAt(0);

//		Cell cell2Update = sheet.getRow(1).getCell(3);
//		cell2Update.setCellValue(49);

		XSSFCell cellToUpdate;

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pm = (PalletModel) itr.next();

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (quantityLeft != 0) {
				cellToUpdate.setCellValue(pm.getBatteryType());
			} else {
				cellToUpdate.setCellValue(""); // clear Battery type
			}

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (quantityLeft != 0) {
				cellToUpdate.setCellValue(String.valueOf(quantityLeft));
			} else {
				cellToUpdate.setCellValue(""); // clear Quantity real
			}

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(3, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (quantityLeft != 0) {
				cellToUpdate.setCellValue(String.valueOf(quantityLeft));
			} else {
				cellToUpdate.setCellValue(""); // clear Quantity
			}

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(4, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (quantityLeft != 0) {
				cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getProductionDate()));
			} else {
				cellToUpdate.setCellValue(""); // clear Production date
			}

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getStatus());

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(6, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (quantityLeft != 0) {
				cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getIncomeDate()));
			} else {
				cellToUpdate.setCellValue(""); // clear Income date
			}

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(7, MissingCellPolicy.CREATE_NULL_AS_BLANK);

			if (quantityLeft != 0) {
				cellToUpdate.setCellValue(BaseMethods.FormatTime(pm.getIncomeTime()));
			} else {
				cellToUpdate.setCellValue(""); // clear Production time
			}

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(8, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getIsReserved());
		}

		try {
			file.close();
			FileOutputStream outputStream = new FileOutputStream(Base.mainDbFile);
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SaveSwapData(List<PalletModel> pmList) {
		FileInputStream file = null;
		XSSFWorkbook workbook = null;
		try {
			file = new FileInputStream(new File(Base.mainDbFile));
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XSSFSheet sheet = workbook.getSheetAt(0);

		XSSFCell cellToUpdate;

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pm = (PalletModel) itr.next();

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getBatteryType());

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getQuantityReal());

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(3, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getQuantity());

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(4, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getProductionDate()));

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getStatus());

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(6, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getIncomeDate()));

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(7, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(BaseMethods.FormatTime(pm.getIncomeTime()));

			cellToUpdate = sheet.getRow(pm.getRow()).getCell(8, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellToUpdate.setCellValue(pm.getIsReserved());
		}

		try {
			file.close();
			FileOutputStream outputStream = new FileOutputStream(Base.mainDbFile);
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SaveData(PalletModel pm) {
		FileInputStream file = null;
		XSSFWorkbook workbook = null;
		try {
			file = new FileInputStream(new File(Base.mainDbFile));
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getProductionDate()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getStatus());

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(6, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(BaseMethods.FormatDate(pm.getIncomeDate()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(7, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(BaseMethods.FormatTime(pm.getIncomeTime()));

		cellToUpdate = sheet.getRow(pm.getRow()).getCell(8, MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cellToUpdate.setCellValue(pm.getIsReserved());

		try {
			file.close();
			FileOutputStream outputStream = new FileOutputStream(Base.mainDbFile);
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void ReadAllRows() {

		MainView.SetVisibleButtons(false);
		allRows = new HashMap<>();
		DeleteDataFromTable();

		mainConnection = getConnection();
		try {
			mainStatement = mainConnection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LoadingScreen ls = new LoadingScreen();
		Sheet sheet = GetSheet();

		new SwingWorker<Void, Integer>() {
			int i = 0;
			double percentage = 0;

			@Override
			public Void doInBackground() {

				int rowCount = sheet.getPhysicalNumberOfRows();

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

					percentage = i * 100 / rowCount;
					publish((int) percentage);
					// ls.UpdateProgress(percentage);
				}
				return null;
			}

			@Override
			public void done() {

				try {
					mainStatement.close();
					mainConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ls.setVisible(false);
				ls.dispose();

				MainView.UpdateGui();
				MainView.SetVisibleButtons(true);

				int frame = getCallingFrame();

				switch (frame) {
				case 1:
					IncomeView.UpdateTable();
					break;
				case 2:
					OutcomeView.UpdateTable();
					break;

				default:
					break;
				}
			}

			@Override
			protected void process(List<Integer> ints) {
				ls.setProgress(ints.get(0));
			}

		}.execute();
	}

	private static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String URL = "jdbc:derby:" + Base.DB_PATH + "warehouseDb;create=true";

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
					+ "QuantityReal Int, " + "Quantity Int, " + "ProductionDate VARCHAR(50), " + "IsFree VARCHAR(5), "
					+ "Date VARCHAR(50), " + "Time VARCHAR(50), " + "IsReserved VARCHAR(5), " + "PRIMARY KEY (Id))";
			try {
				tableExists = true;
				statement.execute(query);
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

	public static void DropTable() {
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

		String query = "DROP TABLE " + TABLE_NAME;
		try {
			statement.execute(query);
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

	private static void DeleteDataFromTable() {
		if (tableExists) {
			Connection connection = null;
			Statement statement = null;

			connection = getConnection();
			try {
				statement = connection.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String query = "DELETE FROM " + TABLE_NAME;
			try {
				statement.execute(query);
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

		String query = "INSERT INTO " + TABLE_NAME
				+ "(Row, PalletName, BatteryType, QuantityReal, Quantity, ProductionDate, IsFree, Date, Time, IsReserved) VALUES "
				+ "('" + pm.getRow() + "','" + pm.getPalletName() + "','" + pm.getBatteryType() + "',"
				+ pm.getQuantityReal() + "," + pm.getQuantity() + ",'" + pm.getProductionDate() + "','" + pm.getStatus()
				+ "','" + pm.getIncomeDate() + "','" + pm.getIncomeTime() + "','" + pm.getIsReserved() + "')";
		try {
			mainStatement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void UpdateTable(List<PalletModel> pmList, int quantityLeft) {
		Connection connection = getConnection();
		Statement statement = null;

		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pm = (PalletModel) itr.next();
			if (quantityLeft != 0) {
				String query = "UPDATE " + TABLE_NAME + " SET QuantityReal = " + pm.getQuantityReal() + ", Quantity = "
						+ pm.getQuantity() + " WHERE PalletName='" + pm.getPalletName() + "'";

				try {
					statement.executeUpdate(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String query = "UPDATE " + TABLE_NAME
						+ " SET BatteryType = '', QuantityReal = 0, Quantity = 0, ProductionDate = '', IsFree = '"
						+ pm.getStatus() + "', Date = '', Time = '', IsReserved = '" + pm.getIsReserved()
						+ "' WHERE PalletName='" + pm.getPalletName() + "'";
				try {
					statement.executeUpdate(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashMap<Integer, PalletModel> FilterPallet(String text) {
		Connection connection = getConnection();
		Statement statement = null;

		Set<Integer> set = new HashSet<Integer>();
		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);

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

	public static int FindPalletRow(String text) {
		Connection connection = getConnection();
		Statement statement = null;

		int row = -1;

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
				row = Integer.parseInt(rs.getString("Row"));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return row;
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
		Sheet sheet = workbook.getSheetAt(0);

		return sheet;
	}

	private static Cell GetCell(String column, int rowNumber, Sheet sheet) {
		CellReference cr = new CellReference(column + rowNumber);
		Row row = sheet.getRow(cr.getRow());
		Cell cell = row.getCell(cr.getCol());

		return cell;
	}

	public static HashMap<Integer, PalletModel> GetAllRows() {
		CheckIfFileIsEdited();

		HashMap<Integer, PalletModel> data = new HashMap<Integer, PalletModel>();
		data.putAll(allRows);
		return data;
	}

	public static int GetFirstFreeRow() {
		int foundOnRow = NOT_FOUND;
		CheckIfFileIsEdited();

		Connection connection = null;
		Statement statement = null;

		connection = getConnection();
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String query = "SELECT * FROM " + TABLE_NAME
				+ " WHERE IsFree = true AND IsReserved = false FETCH FIRST ROW ONLY";
		ResultSet rs;
		try {
			rs = statement.executeQuery(query);

			if (rs.next()) {
				foundOnRow = Integer.parseInt(rs.getString("Row"));
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private static int GetBatteryTypeRow(String pattern) {
		int foundOnRow = NOT_FOUND;

		Connection connection = null;
		Statement statement = null;

		connection = getConnection();
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE BatteryType='" + pattern + "' " + "FETCH FIRST ROW ONLY";
		ResultSet rs;
		try {
			rs = statement.executeQuery(query);

			if (rs.next()) {
				foundOnRow = Integer.parseInt(rs.getString("Row"));
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
