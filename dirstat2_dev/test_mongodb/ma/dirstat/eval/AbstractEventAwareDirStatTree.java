package ma.dirstat.eval;

import java.util.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import javax.swing.event.*;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

abstract class AbstractEventAwareDirStatTree implements TreeModel {

	enum TreeChange {
		NODES_INSERTED, NODES_REMOVED, STRUCTURE_CHANGED, MINOR_CHANGE
	}

	private final LinkedList<TreeModelListener> listeners;

	private JTable gui;

	AbstractEventAwareDirStatTree() {
		super();
		listeners = new LinkedList<TreeModelListener>();
	}

	void setGUI(JTable gui) {
		this.gui = gui;
	}

	void changesBelow(ViewNode node) {
		TreeModelEvent ev = createEventTo(node);
		updateGUI(ev, TreeChange.STRUCTURE_CHANGED);
	}

	private TreeModelEvent createEventTo(ViewNode vn) {
		assert vn != null;
		Deque<Object> path = new ArrayDeque<Object>();
		ViewNode root = vn.root.getRoot();
		path.push(vn);
		if(!radd(path, root, vn))
			path.clear();
		path.push(getRoot());
		Object[] pathO = path.toArray();
		return new TreeModelEvent(this, pathO);
	}

	private static boolean radd(Deque<Object> path, ViewNode root,
							ViewNode child) {
		if(root.equals(child))
			return true;
		if(root.children == null)
			return false;
		for(ViewNode i: root.children) {
			if(i.equals(child) || radd(path, i, child)) {
				path.push(root);
				return true;
			}
		}
		return false;
	}

	void justRepaint() {
		updateGUI(null, TreeChange.MINOR_CHANGE);
	}

	void viewAdded(BoundView view, int index) {
		TreeModelEvent ev = new TreeModelEvent(this,
				new Object[] { getRoot() }, new int[] { index },
				new Object[] { view.getRoot() });

		updateGUI(ev, TreeChange.NODES_INSERTED);
	}

	TreeModelEvent createViewToBeRemovedEvent(BoundView view, int viewI) {

		return new TreeModelEvent(this, new Object[] { getRoot() },
					new int[] { viewI },
					new Object[] { view.getRoot() });
	}

	void viewHasBeenRemoved(TreeModelEvent ev) {
		updateGUI(ev, TreeChange.NODES_REMOVED);
	}

	private void updateGUI(final TreeModelEvent ev, final TreeChange chg) {
		if(gui != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(chg != TreeChange.MINOR_CHANGE)
						sendChange(ev, chg);
					gui.repaint();
				}
			});
		}
	}

	private void sendChange(TreeModelEvent ev, TreeChange chg) {
		assert(chg != TreeChange.MINOR_CHANGE);
		for(TreeModelListener l: listeners) {
			switch(chg) {
			case NODES_INSERTED:
				l.treeNodesInserted(ev);
				break;
			case NODES_REMOVED:    
				l.treeNodesRemoved(ev);
				break;
			case STRUCTURE_CHANGED:
				l.treeStructureChanged(ev);
				break;
			}
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Not used
	}

}
