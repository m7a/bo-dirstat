package ma.dirstat.eval;

import java.io.File;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JFileChooser;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.netbeans.swing.outline.Outline;

class UserController implements ActionListener {

	private final DirStatTree tree;
	private final ViewFactory fact;
	private final DBCollection db;

	private Outline table;

	UserController(ViewFactory fact, DirStatTree tree, DBCollection db) {
		super();
		this.tree = tree;
		this.fact = fact;
		this.db   = db;
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
		} else if(sel != null) {
			BoundView selV = (BoundView)sel.root;
			if(cmd.equals("Delete"))
				tree.removeView(selV);
			else
				System.out.println("TODO N_IMPL " +
						event.getActionCommand());
		}
	}

	private ViewNode getSelected() {
		int sel = table.getSelectedRow();
		if(sel == -1 || sel == 0)
			return null;	
		else
			return (ViewNode)table.getValueAt(sel, 0);
		
	}

	private void attachJarCnt(ViewNode below, ActionEvent ev) {
		BoundView view;
		try {
			view = new JarBoundView(fact,
					((JMenuItem)ev.getSource()).getText());
		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
		attachView(below, view);
	}

	private void attachView(final ViewNode below, final BoundView view) {
		tree.submitBG(new Runnable() {
			public void run() {
				attachViewSub(below, view);
			}
		});
	}

	private void attachViewSub(ViewNode below, BoundView view) {
		final DBObject[] agf;
		final DBObject[] mrf;
		if(below == null) {
			agf = DirStatTree.NO_FILTERS;
			mrf = DirStatTree.NO_FILTERS;
		} else {
			View r = below.root;
			DBObject[] pagf = r.getPrevAggreagtionFilters();
			DBObject[] pmrf = r.getPrevMapReduceFilters();
			agf = new DBObject[pagf.length + 1];
			mrf = new DBObject[pmrf.length + 1];
			System.arraycopy(pagf, 0, agf, 0, pagf.length);
			System.arraycopy(pmrf, 0, mrf, 0, pmrf.length);
			agf[pagf.length] = r.createAggregationFilter(below);
			mrf[pmrf.length] = r.createMapReduceFilter(below);
		}

		view.setReferenceData(new ViewReferenceData(new ViewNodeFactory(
							view), db, agf, mrf));
		tree.addNewView(view);
	}

	private void attachFile(ViewNode sel) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(chooser.showDialog(table, "Öffnen") ==
						JFileChooser.APPROVE_OPTION)
			attachFile(sel, chooser.getSelectedFile());
	}

	private void attachFile(ViewNode sel, File file) {
		BoundView view;
		try {
			view = new FileBoundView(fact, file);
		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
		attachView(sel, view);
	}

}
