package ma.dirstat.eval;

import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.event.*;

import ma.tools2.util.NotImplementedException;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.netbeans.swing.outline.RowModel;

class DirStatTree extends AbstractEventAwareDirStatTree implements RowModel {

	private static final String N_A = "N/A";

	private static final String[] COLUMNS = {
		"Pattern", "Files", "Size", "Errors", "Empty", "Biggest",
		"Biggest Path"
	};

	static final String[] DEFAULT_VIEWS = {
		"extension.js",
		"tree.js"
	};

	static final DBObject[] NO_FILTERS = new DBObject[0];

	private final DBCollection db;
	private final ArrayList<BoundView> views;

	private final ExecutorService bgTasks;

	DirStatTree(final DBCollection db) {
		super();
		this.db = db;
		views   = new ArrayList<BoundView>();
		bgTasks = Executors.newSingleThreadExecutor();
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
		for(String i: DEFAULT_VIEWS)
			addRootView(new JarBoundView(fact, i));
	}

	private void addRootView(BoundView view) {
		view.setReferenceData(new ViewReferenceData(new ViewNodeFactory(
					view), db, NO_FILTERS, NO_FILTERS));
		addNewView(view);
	}

	void addNewView(BoundView view) {
		view.create();
		views.add(view);
		viewAdded(view, views.size() - 1);
	}

	void removeView(BoundView view) {
		TreeModelEvent ev = createViewToBeRemovedEvent(view,
							views.indexOf(view));
		boolean ok = views.remove(view);
		assert ok;
		viewHasBeenRemoved(ev);
	}

	void submitBG(Runnable r) {
		bgTasks.submit(r);
	}

	//------------------------------------------------------[ Tree Model ]--

	@Override
	public Object getChild(Object parent, int index) {
		if(parent.equals(db.getName()))
			return views.get(index).getRoot();

		ViewNode node = (ViewNode)parent;
		assert node.getState() != State.NOT_INITIALIZED;

		if(node.getState() == State.READY) {
			assert node.children != null;
			if(index < node.children.length) {
				assert node.children[index] != null;
				return node.children[index];
			} else {
				// Against concurrent modification
				changesBelow(node);
			}
		}

		return ((BoundView)node.root).loading;
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent.equals(db.getName()))
			return views.size();

		ViewNode vn = (ViewNode)parent;
		if(vn.getState() == State.READY) {
			assert vn.children != null;
			return vn.children.length;
		} else {
			return 1;
		}
	}

	@Override
	public Object getRoot() {
		return db.getName();
	}

	@Override
	public boolean isLeaf(Object node) {
		if(node.equals(db.getName()))
			return views.size() == 0;

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
			return vn.children.length == 0;
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
				if(vn.children.length == 0)
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
		else if(parent.equals(getRoot()))
			return views.indexOf(((ViewNode)child).root);
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
		throw new NotImplementedException(child + " is not a child of "
								+ parent);
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
		if(node.equals(db.getName()))
			return "";
		else
			return getViewValue((ViewNode)node, column).toString();
	}

	private Object getViewValue(ViewNode node, int column) {
		switch(column) {
		case 0:  return node.pattern;
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
