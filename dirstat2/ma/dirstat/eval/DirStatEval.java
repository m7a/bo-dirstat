package ma.dirstat.eval;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import java.sql.Connection;

import ma.dirstat.DirStat;

public class DirStatEval {

	private final Connection db;
	private final String name;

	public DirStatEval(Connection db, String name) {
		super();
		this.db   = db;
		this.name = name;
	}

	public void run() throws Exception {
		final DirStatDetailPanel details = new DirStatDetailPanel();

		final DirStatMeta meta = new DirStatMeta(db, name);
		final DirStatTree tree = new DirStatTree(db, meta,
								details.status);
		ViewFactory fact = new ViewFactory();
		tree.createDefaultViews(fact);

		final UserController ctrl = new UserController(fact, tree, db,
								details);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI gui = new GUI(tree, ctrl, details);
				ensureDBClose(gui);
				gui.setVisible(true);
			}
		});
	}

	private void ensureDBClose(GUI gui) {
		gui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					db.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

}
