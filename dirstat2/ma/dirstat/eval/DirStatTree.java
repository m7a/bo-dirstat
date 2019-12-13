package ma.dirstat.eval;

import java.util.*;

import java.util.concurrent.ThreadPoolExecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.event.*;

import ma.tools2.util.NotImplementedException;

import java.sql.Connection;

import org.netbeans.swing.outline.RowModel;

class DirStatTree extends AbstractEventAwareDirStatTree implements RowModel {

	private static final String N_A = "N/A";

	static final String[] COLUMNS = {
		"Pattern", "Files", "Size", "Errors", "Empty", "Biggest",
		"Biggest Path"
	};

	static final String[] DEFAULT_VIEWS = { "extension.js", "tree.js",
								"type.js" };
	private static final String[] CREATE_VIEWS = { "type.js", "tree.js" };

	private final RootView root;
	private final ThreadPoolExecutor bgTasks;

	DirStatTree(Connection db, DirStatMeta meta, DirStatStatusPanel stat) {
		super();
		root    = new RootView(this);
		bgTasks = new DirStatBGExecutor(stat);
		root.setReferenceData(new ViewReferenceData(root, db,
						PreparedFilter.EMPTY, meta));
		root.create();
	}

	void createDefaultViews(final ViewFactory fact) {
		bgTasks.submit(new Runnable() {
			public void run() {
				try {
					createDefaultViewsLoop(fact);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private void createDefaultViewsLoop(ViewFactory fact) throws Exception {
		for(String i: CREATE_VIEWS)
			addView(root.getRoot(), fact.createJAR(i));
	}

	void addView(ViewNode below, View view) {
		root.addView(below, view);
		viewAdded(view, root.getRoot().children.size() - 1);
	}

	void removeView(View view) {
		TreeModelEvent ev = createViewToBeRemovedEvent(view,
							root.indexOf(view));
		root.removeView(view);
		viewHasBeenRemoved(ev);
	}

	void submitBG(Runnable r) {
		bgTasks.submit(r);
	}

	//------------------------------------------------------[ Tree Model ]--

	@Override
	public Object getChild(Object parent, int index) {
		ViewNode node = (ViewNode)parent;
		assert node.getState() != State.NOT_INITIALIZED;

		if(node.getState() == State.READY) {
			assert node.children != null;
			if(index < node.children.size()) {
				assert node.children.get(index) != null;
				return node.children.get(index);
			} else {
				// Against concurrent modification
				changesBelow(node);
			}
		}

		return node.root.getLoading();
	}

	@Override
	public int getChildCount(Object parent) {
		ViewNode vn = (ViewNode)parent;
		if(vn.getState() == State.READY) {
			assert vn.children != null;
			return vn.children.size();
		} else {
			return 1;
		}
	}

	@Override
	public ViewNode getRoot() {
		return root.getRoot();
	}

	@Override
	public boolean isLeaf(Object node) {
		final ViewNode vn = (ViewNode)node;
		switch(vn.getState()) {
		case NOT_INITIALIZED:
			vn.setState(State.WORKING);
			assert vn.children == null;
			bgPopulate(vn);
			/* Fall through */
		case WORKING:
			return false;
		case READY:
			return vn.children.isEmpty();
		default:
			throw new NotImplementedException();
		}
	}

	private void bgPopulate(final ViewNode vn) {
		bgTasks.submit(new Runnable() {
			public void run() {
				vn.root.populateChildren(vn);
				assert vn.children != null;
				assert vn.getState() == State.READY;
				if(vn.children.isEmpty())
					justRepaint();
				else
					changesBelow(vn);
			}
		});
	}

	/**
	 * Muss leider für problemlose Sortierung implementiert werden.
	 * Diese Methode arbeitet nicht besonders effektiv, was sich im Test
	 * aber nicht als Problem herausstellte.
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
			return -1;
		else if(child instanceof LoadingNode)
			return 0;
		else
			return searchChild((ViewNode)parent, (ViewNode)child);
	}

	private int searchChild(ViewNode parent, ViewNode child) {
		assert parent.getState() == State.READY;
		int i = 0;
		for(ViewNode current: parent.children) {
			if(current.equals(child))
				return i;
			i++;
		}
		return -1; // Has been removed
	}

	//-------------------------------------------------------[ Row Model ]--

	@Override
	public Class getColumnClass(int column) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueFor(Object node, int column) {
		return getViewValue((ViewNode)node, column).toString();
	}

	private Object getViewValue(ViewNode node, int column) {
		switch(column) {
		case 0:  assert(node.pattern != null); return node.pattern;
		case 1:  return node.files;
		case 2:  return node.size;
		case 3:  return node.errors;
		case 4:  return node.empty;
		case 5:  return node.biggestBytes;
		case 6:  return node.biggestPath == null? N_A: node.biggestPath;
		default: throw new NotImplementedException();
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}

	@Override
	public void setValueFor(Object node, int column, Object value) {
		// Not used
	}

}
