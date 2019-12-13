package ma.dirstat.eval;

import java.awt.Component;
import java.awt.Color;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import static ma.dirstat.FileSize.*;

class TreeTableRenderer implements TableCellRenderer {

	private final TableCellRenderer before;

	TreeTableRenderer(TableCellRenderer before) {
		super();
		this.before = before;
	}

	// TODO DOES NOT SUPPORT EXCHANGING COLUMNS
	public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
		JLabel ret = (JLabel)this.before.getTableCellRendererComponent(
			table, value, isSelected, hasFocus, row, column);

		assert value != null;

		String val = value.toString();
		if((column == 3 || column == 6) && val.length() != 0 &&
							!val.equals("N/A")) {
			long num = Long.parseLong(val);
			colorize(num, ret);
			if(num == -1)
				ret.setText("N/A");
			else
				ret.setText(format(num));
		} else {
			ret.setForeground(Color.BLACK);
		}

		return ret;
	}

	private static void colorize(long num, JLabel lbl) {
		Color c;
		if(num > ONE_EIB)
			c = Color.RED;
		else if(num > ONE_TIB)
			c = Color.MAGENTA;
		else if(num > ONE_GIB)
			c = Color.BLUE;
		else if(num > ONE_MIB)
			c = Color.BLACK;
		else if(num > ONE_KIB)
			c = Color.GRAY;
		else
			c = Color.LIGHT_GRAY;
		lbl.setForeground(c);
	}

}
