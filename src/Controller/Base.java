package Controller;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import View.LoadingScreen;

public class Base {
	public final static String DOT = ".";
	public final static String BACKSLASH = "\\";
	public final static String DELIMITER = ";";

	// Paths
	private final static String MAIN_PATH = "C:\\Warehouse\\sys\\settings.ini";
	public final static String DB_PATH = "C:\\Warehouse\\DB\\";

	// database
	// public static LinkedHashMap<Integer, DowntimeModel> downtimeDb;
	// public static HashMap<Integer, ActionModel> actionDb;

	// combobox
	public static String workshopFile;
	public static List<String> workshopData;

	// Labels
	public final static String FRAME_CAPTION = "MONBAT";
	public final static String CREATE_DOWNTIME_LABEL = "Въвеждане на формуляр";
	public final static String ACTION_LABEL = "Въвеждане действие \nкъм формуляр";
	public final static String MAINTENANCE_LABEL = "Поддръжка: статистика";
	public static String fullFrameCaption;

	// buttons
	public final static String MAINTENANCE = "maintenance";
	public final static String STATISTICS = "statistics";

	// Size
	public final static int WIDTH = 1366;
	public final static int HEIGHT = 768;
	public final static int ELEMENT_HEIGHT = 30;
	public final static int ELEMENT_OFFSET = 37;
	public final static int PANEL_HEIGHT = 74;
	public final static int PANEL_WIDTH = 250;
	public final static int LAST_ROW = 8;

	public final static Locale LOCALE = new Locale("bg");

	public final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	public final static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
	public final static DateTimeFormatter fileNameTimeFormat = DateTimeFormatter.ofPattern("HH-mm-ss");

	// default fonts
	public final static Font DEFAULT_FONT = new Font("Century Gothic", Font.BOLD, 16);
	public final static Font RADIO_BUTTON_FONT = new Font("Century Gothic", Font.BOLD, 14);

	// default colors
	public final static Color TEXT_FIELD_COLOR = new Color(51, 51, 204);

	public static Preferences settings;
	public static Ini emailsettings;
	public static String backgroundPic;
	public static String logoWhite;
	public static String icon;

	public static Boolean showAllWarehouses;
	private static String userWarehouseAccess;
	
	public static Boolean tableExists = false;

	// Files
	public static String mainDbFile;
	public static String mainReportAddress;
	private static String dbFileA;
	public static String reportAddressA;
	private static String dbFileB;
	public static String reportAddressB;
	private static String dbFileC;
	public static String reportAddressC;
	
	//Warehouses names
	private static String mainWarehouseName;
	public static String warehouseAName;
	public static String warehouseBName;
	public static String warehouseCName;

	// passwords
	public static String maintenancePassword;
	public static String adminPassword;
	public static String statisticsPassword;

	// email settings
	public static String smtpuser;
	public static String smtppassword;
	public static String smtphostname;
	public static String smtpport;
	public static String smtpusetls;
	public static List<String> recipientsList = new ArrayList<String>();
	public static Boolean isEmailSent = true;

	public static Integer refreshTime;


	public static void LoadBasics() throws BackingStoreException {
		LoadPaths();
		AssignVariables();
		AssignMainDbFile(userWarehouseAccess);
		fullFrameCaption = FRAME_CAPTION + " / " + mainWarehouseName;
	}

	private static void LoadPaths() {
		try {
			settings = ReadIni.ParseIni(MAIN_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void AssignMainDbFile(String db) {
		switch (db) {
		case "A":
			mainDbFile = dbFileA;
			mainReportAddress = reportAddressA;
			mainWarehouseName = warehouseAName;
			break;
		case "B":
			mainDbFile = dbFileB;
			mainReportAddress = reportAddressB;
			mainWarehouseName = warehouseBName;
			break;
		case "C":
			mainDbFile = dbFileC;
			mainReportAddress = reportAddressC;
			mainWarehouseName = warehouseCName;
			break;
		default:
			int result = JOptionPane.showConfirmDialog(null, "Не може да бъде зареден файл dbFile" + userWarehouseAccess,
					"Грешка", JOptionPane.DEFAULT_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				//System.exit(0);
			}
		}
	}

//	private static void AssignEmailSettings() {
//		smtpuser = emailsettings.get("sender", "smtpuser");
//		smtppassword = emailsettings.get("sender", "smtppassword");
//		smtphostname = emailsettings.get("sender", "smtphostname");
//		smtpport = emailsettings.get("sender", "smtpport");
//		smtpusetls = emailsettings.get("sender", "smtpusetls");
//	}
//
//	private static void AssignEmailRecipients() throws BackingStoreException {
//		Section section = emailsettings.get("recipient");
//		for (String item : section.keySet()) {
//			//System.out.println("\t"+item+"="+section.get(item));
//			recipientsList.add(section.get(item));
//		}
//	}

	private static void LoadComboboxData() {
//		try {
//			//workshopData = ReadFromCsv.ParseGenericCsv(workshopFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public static void LoadDowntimeDb() {
//		File file = new File(downtimeDbFile);
//		if (file.canRead()) {
//			try {
//				downtimeDb = ReadFromCsv.ReadDowntimeDb(downtimeDbFile);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			isDowntimeDb = false;
//		}
	}

	public static void LoadActionDb() {
//		File file = new File(downtimeDbFile);
//		if (file.canRead()) {
//			try {
//				actionDb = ReadFromCsv.ReadActionDb(actionDbFile);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			isActionDb = false;
//		}
	}

	private static void AssignPasswords() {
		maintenancePassword = settings.node("maintenance").get("password", null);
		adminPassword = settings.node("admin").get("password", null);
		statisticsPassword = settings.node("statistics").get("password", null);
	}

	private static void AssignVariables() {
		StringBuilder sb = new StringBuilder();
		sb.append(settings.node("system").get("mainaddress", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("background").get("address", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("background").get("name", null));
		sb.append(DOT);
		sb.append(settings.node("background").get("extension", null));

		backgroundPic = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("system").get("mainaddress", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("logowhite").get("address", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("logowhite").get("name", null));
		sb.append(DOT);
		sb.append(settings.node("logowhite").get("extension", null));

		logoWhite = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("system").get("mainaddress", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("logoframe").get("address", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("logoframe").get("name", null));
		sb.append(DOT);
		sb.append(settings.node("logoframe").get("extension", null));

		icon = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileA").get("address", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("dbfileA").get("name", null));
		sb.append(DOT);
		sb.append(settings.node("dbfileA").get("extension", null));

		dbFileA = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileB").get("address", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("dbfileB").get("name", null));
		sb.append(DOT);
		sb.append(settings.node("dbfileB").get("extension", null));

		dbFileB = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileC").get("address", null));
		sb.append(BACKSLASH);
		sb.append(settings.node("dbfileC").get("name", null));
		sb.append(DOT);
		sb.append(settings.node("dbfileC").get("extension", null));

		dbFileC = sb.toString();
		
		sb = new StringBuilder();
		sb.append(settings.node("dbfileA").get("report", null));
		sb.append(BACKSLASH);

		reportAddressA = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileB").get("report", null));
		sb.append(BACKSLASH);

		reportAddressB = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileC").get("report", null));
		sb.append(BACKSLASH);

		reportAddressC = sb.toString();
		
		sb = new StringBuilder();
		sb.append(settings.node("dbfileA").get("warehousename", null));

		warehouseAName = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileB").get("warehousename", null));

		warehouseBName = sb.toString();

		sb = new StringBuilder();
		sb.append(settings.node("dbfileC").get("warehousename", null));

		warehouseCName = sb.toString();
		
		userWarehouseAccess = settings.node("userwarehouseaccess").get("warehouse", null);

		showAllWarehouses = Boolean.parseBoolean(settings.node("showallwarehouses").get("param", null));
	}
}
