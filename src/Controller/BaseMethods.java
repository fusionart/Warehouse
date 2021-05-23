package controller;

import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class BaseMethods {
	public static Boolean CheckIsNumber(String s) {
		if ((s == null)) {
			return false;
		}

		try {
			int d = Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Моля, въведете число!", "Грешка",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	public static Boolean CheckIfDowntimeNumberExists(String s) {
//		if (Base.downtimeDb.containsKey(Integer.parseInt(s))) {
//			return true;
//		}
//		
//		JOptionPane.showMessageDialog(null, "Номер " + s + " не е намерен", "Грешка", JOptionPane.INFORMATION_MESSAGE);
		return false;
	}

	public static Boolean CheckIfActionNumberExists(String s) {
//		if (Base.actionDb.containsKey(Integer.parseInt(s))) {
//			JOptionPane.showMessageDialog(null, "За номер " + s + " има въведено Действие", "Грешка", JOptionPane.INFORMATION_MESSAGE);
//			return true;
//		}
//		
		return false;
	}

	public static Boolean CheckIfNegative(String s) {
		if (Integer.parseInt(s) <= 0) {
			JOptionPane.showMessageDialog(null, "Моля, въведете число по-голямо от 0!", "Грешка",
					JOptionPane.INFORMATION_MESSAGE);
			return true;
		}

		return false;
	}
	
	public static String FormatDate(LocalDate date) {
		String formattedDate = null;
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		
		if (date != null) {
			formattedDate = date.format(dateFormat);
		}
		
		return formattedDate;
	}

	public static String FormatTime(LocalTime time) {
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

		String formattedTime = time.format(timeFormat);

		return formattedTime;
	}

//	public static DowntimeModel LoadDowntimeModel(String s) {
//		DowntimeModel dtm = Base.downtimeDb.get(Integer.parseInt(s));
//		
//		return dtm;
//	}
//	
//	public static ActionModel LoadActionModel(String s) {
//		ActionModel actm = Base.actionDb.get(Integer.parseInt(s));
//		
//		return actm;
//	}
//	
//	public static String ActionName(DowntimeModel dtm) {
//		String actionName = null;
//		if (dtm.isBreakdown()) {
//			actionName = "Авария";
//		}
//
//		if (dtm.isSignal()) {
//			actionName = "Сигнал";
//		}
//
//		if (dtm.isMaterial()) {
//			actionName = "Материал";
//		}
//
//		if (dtm.isCleaning()) {
//			actionName = "Почистване";
//		}
//
//		if (dtm.isRepair()) {
//			actionName = "Поправка";
//		}
//
//		if (dtm.isNoElectricity()) {
//			actionName = "Липса на ток";
//		}
//		
//		if (dtm.isShortReadjustment()) {
//			actionName = "Кратка настройка";
//		}
//		
//		if (dtm.isLongReadjustment()) {
//			actionName = "Пълна настройка";
//		}
//
//		if (dtm.isOther()) {
//			actionName = dtm.getOtherText();
//		}
//		
//		return actionName;
//	}

	public static void ResizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 15; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + (comp.getPreferredSize().width / 4), width);
			}

			TableColumn col = columnModel.getColumn(column);
			TableCellRenderer headerRenderer = col.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			Object headerValue = col.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0,
					column);
			width = Math.max(width, headerComp.getPreferredSize().width + (headerComp.getPreferredSize().width / 5));

			if (width > 300)
				width = 300;
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}
}
