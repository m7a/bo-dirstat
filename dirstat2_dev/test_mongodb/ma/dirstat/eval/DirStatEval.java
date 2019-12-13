package ma.dirstat.eval;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import com.mongodb.DBCollection;

import ma.dirstat.DirStat;

public class DirStatEval {

	private final DBCollection db;
	private final DirStat stat;

	public DirStatEval(DBCollection db, DirStat stat) {
		super();
		this.db   = db;
		this.stat = stat;
	}

	public void run() throws Exception {
		final DirStatTree tree = new DirStatTree(db);
		ViewFactory fact = new ViewFactory();
		tree.createDefaultViews(fact);
		final UserController ctrl = new UserController(fact, tree, db);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI gui = new GUI(tree, ctrl);
				ensureDBClose(gui);
				gui.setVisible(true);
			}
		});
	}

	private void ensureDBClose(GUI gui) {
		gui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				stat.close();
			}
		});
	}

}
