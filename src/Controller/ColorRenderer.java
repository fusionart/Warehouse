package controller;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ColorRenderer implements TableCellRenderer {
	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
	 
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
 
        Object dtType = table.getModel().getValueAt(row, 0);
        String dtName = dtType.toString();
        
        if (dtName.equals("Авария")) {
            c.setBackground(new Color(255, 0, 0, 150));
        } else {
            c.setBackground(Color.white);
        }
 
        return c;
    }
}
