package ma.dirstat.eval;

import java.util.ArrayList;

import java.io.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JFileChooser;

import java.sql.*;

import org.netbeans.swing.outline.Outline;

class UserController implements ActionListener {

	// TODO Room for improvement
	// It might be interesting to get some additional (and quickly
	// collectable) information.
	private static final String DETAIL_TABLE_SELECT =
		"SELECT " +
			"COUNT(*) AS objects, " +
			"COUNT(CASE WHEN files.dir = TRUE THEN 1 ELSE NULL " +
							"END) AS dirs, " +
			"COUNT(CASE WHEN files.reg = TRUE THEN 1 ELSE NULL " +
							"END) AS regulars, " +
			"COUNT(CASE WHEN files.symlink = TRUE THEN 1 ELSE " +
						"NULL END) AS symlinks, " +
			"MAX(LENGTH(path)) AS longest_path " +
		"FROM files " +
		"WHERE ?";

	private static final String DETAIL_EMPTY_SELECT =
		"SELECT path FROM files WHERE file_size = 0 AND reg = TRUE";

	private static final String DETAIL_DIAGRAM_SELECT =
		"SELECT " +
			"ROUND(LN(file_size + 1)::numeric, 2) AS lnsz, " +
			"COUNT(*) AS n " +
		"FROM files " +
		"WHERE (file_size IS NOT NULL AND dir = FALSE) " +
		"GROUP BY lnsz;";

	private final DirStatTree tree;
	private final ViewFactory fact;
	private final Connection db;
	private final DirStatDetailPanel details;

	private Outline table;

	private int previouslySelectedElement;

	UserController(ViewFactory fact, DirStatTree tree, Connection db,
						DirStatDetailPanel details) {
		super();
		this.tree                 = tree;
		this.fact                 = fact;
		this.db                   = db;
		this.details              = details;
		previouslySelectedElement = -1;
	}

	void setTable(Outline table) {
		this.table = table;
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		ViewNode sel = getSelected();

		if(cmd.equals("jarcnt")) {
			attachJarCnt(sel, event);
		} else if(cmd.equals("File...")) {
			attachFile(sel);
		} else if(cmd.equals("Text")) {
			exportText(sel == null? tree.getRoot(): sel);
		} else if(!(sel == null || sel.root instanceof RootView)) {
			View selV = sel.root;
			if(cmd.equals("Delete"))
				tree.removeView(selV);
		}
	}

	private ViewNode getSelected() {
		int sel = table.getSelectedRow();
		if(sel == -1)
			return null;	
		else
			return (ViewNode)table.getModel().getValueAt(
					table.convertRowIndexToModel(sel), 0);
		
	}

	private void attachJarCnt(ViewNode below, ActionEvent ev) {
		View view;
		try {
			view = fact.createJAR(((JMenuItem)ev.getSource()).
								getText());
		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
		attachView(below, view);
	}

	private void attachView(final ViewNode below, final View view) {
		tree.submitBG(new Runnable() {
			public void run() {
				tree.addView(below, view);
			}
		});
	}

	private void attachFile(ViewNode sel) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(chooser.showDialog(table, "Open") ==
						JFileChooser.APPROVE_OPTION)
			attachFile(sel, chooser.getSelectedFile());
	}

	private void attachFile(ViewNode sel, File file) {
		View view;
		try {
			view = fact.create(new BufferedReader(new FileReader(
									file)));
		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
		attachView(sel, view);
	}

	void updateSelection() {
		int sel = table.getSelectedRow();
		if(sel != previouslySelectedElement)
			updateSelection(sel);
	}

	private void updateSelection(int sel) {
		previouslySelectedElement = sel;
		if(sel == -1)
			details.clearAll();
		else
			updateDetails();
	}

	private void updateDetails() {
		final ViewNode sel = getSelected();
		if(sel == null || sel instanceof LoadingNode)
			return;
		tree.submitBG(new Runnable() {
			public void run() {
				try {
					updateDetails(sel);
				} catch(Exception ex) {
					ex.printStackTrace(); // TODO GUI ERR
				}
			}
		});
	}

	private void updateDetails(ViewNode node) throws SQLException {
		if(!node.equals(getSelected()))
			return; // Abort

		details.clearAll();
		PreparedFilter filter = node.root.createFilter(node).merge(
						node.root.getPrevFilter());
		setTableValues(filter);

		if(!node.equals(getSelected()))
			return; // Abort

		setEmptyFiles(filter);

		if(!node.equals(getSelected()))
			return; // Abort

		setDiagramValues(filter);
	}

	private void setTableValues(PreparedFilter filter) throws SQLException {
		query(filter, DETAIL_TABLE_SELECT, new QueryProcessor() {
			public void process(ResultSet r) throws SQLException {
				setTableValues(r);
			}
		});
	}

	private void query(PreparedFilter filter, String query,
				QueryProcessor proc) throws SQLException {
		try(PreparedStatement st = filter.prepare(db, query)) {
			filter.append(st, 1);
			ResultSet results = st.executeQuery();
			while(results.next())
				proc.process(results);
		}
		details.repaintLater();
	}

	private void setTableValues(ResultSet r) throws SQLException {
		details.tbl.put("Objects",          r.getLong("objects"));
		details.tbl.put("Directories",      r.getLong("dirs"));
		details.tbl.put("Regular files",    r.getLong("regulars"));
		details.tbl.put("Symbolic Links",   r.getLong("symlinks"));
		details.tbl.put("Longest pathname", r.getLong("longest_path"));
		details.tbl.fireTableStructureChanged();
	}

	private void setEmptyFiles(PreparedFilter filter) throws SQLException {
		final ArrayList<String> empty = new ArrayList<String>();
		query(filter, DETAIL_EMPTY_SELECT, new QueryProcessor() {
			public void process(ResultSet r) throws SQLException {
				empty.add(r.getString("path"));
			}
		});
		details.empty.setListData(empty.toArray(new String[empty.size()]
									));
	}

	private void setDiagramValues(PreparedFilter filter)
							throws SQLException {
		query(filter, DETAIL_DIAGRAM_SELECT, new QueryProcessor() {
			public void process(ResultSet r) throws SQLException {
				details.diagram.put(r.getDouble("lnsz"),
								r.getLong("n"));
			}
		});
	}

	private void exportText(ViewNode root) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(chooser.showDialog(table, "Export") ==
						JFileChooser.APPROVE_OPTION)
			exportText(root, chooser.getSelectedFile());
	}

	private void exportText(ViewNode root, File file) {
		TextExporter exporter = new TextExporter(root,
				((RootView)tree.getRoot().root).getMeta());
		try {
			exporter.export(file);
		} catch(IOException ex) {
			ex.printStackTrace(); // TODO GUI ERROR
		}
	}

}
