package ma.dirstat.eval;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import ma.tools2.MTC;

class DirStatDetailPanel extends JPanel {

	final DirStatStatusPanel status;
	final KVTableModel tbl;
	final JList<String> empty;
	final FileSizeDiagram diagram;

	DirStatDetailPanel() {
		super(new GridLayout(1, 3));

		status  = new DirStatStatusPanel();
		tbl     = createInfoTable();
		empty   = createListOfEmptyFiles();
		diagram = createDiagram();
	}

	private KVTableModel createInfoTable() {
		KVTableModel ret = new KVTableModel();
		JTable table = new JTable(ret);
		table.setFont(MTC.TERMINAL_FONT);
		JPanel pan = wrap(table, "General Information");
		pan.add(BorderLayout.SOUTH, status);
		add(pan);
		return ret;
	}

	private static JPanel wrap(JComponent c, String descr) {
		JPanel ret = new JPanel(new BorderLayout());
		ret.setBorder(new TitledBorder(descr));
		JComponent toAdd;
		if(c instanceof Scrollable)
			toAdd = new JScrollPane(c);
		else
			toAdd = c;
		ret.add(BorderLayout.CENTER, toAdd);
		return ret;
	}

	private JList<String> createListOfEmptyFiles() {
		JList<String> ret = new JList<String>();
		ret.setFont(MTC.TERMINAL_FONT);
		add(wrap(ret, "Empty files"));
		return ret;
	}

	private FileSizeDiagram createDiagram() {
		FileSizeDiagram ret = new FileSizeDiagram();
		add(wrap(ret, "File Size Distribution"));
		return ret;
	}

	void clearAll() {
		tbl.clear();
		empty.setListData(new String[0]);
		diagram.clear();
	}

	void repaintLater() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();
			}
		});
	}

}
