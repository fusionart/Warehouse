package ColorRenderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class IncomeReservedColorRenderer implements TableCellRenderer {
	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		Object dtType = table.getModel().getValueAt(row, 7);
		Boolean isReserved = Boolean.parseBoolean(dtType.toString());

		if (isReserved) {

			c.setBackground(Color.lightGray);
			c.setEnabled(table.isCellEditable(row, column));

		} else if (isSelected) {
			c.setBackground(new Color(100, 150, 237, 150));
			c.setForeground(Color.black);
		} else {
			c.setBackground(Color.white);
			c.setEnabled(true);
		}

		return c;
	}
}
