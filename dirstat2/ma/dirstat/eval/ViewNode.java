package ma.dirstat.eval;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ViewNode {

	public final View root;
	public final Object userdata;

	public String id;
	public String pattern;
	public long files;
	public long size;
	public long errors;
	public long empty;
	public String biggestPath;
	public long biggestBytes;

	public ArrayList<ViewNode> children;

	private State state;

	public ViewNode(View root, Object userdata) {
		super();
		this.root     = root;
		this.userdata = userdata;
		id            = "ERROR";
		pattern       = null;
		files         = 0;
		size          = 0;
		errors        = 0;
		empty         = 0;
		biggestPath   = "N/A";
		biggestBytes  = -1;
		children      = null;
		state         = State.NOT_INITIALIZED;
	}

	public String toString() {
		return id;
	}

	public void createChildren() {
		children = new ArrayList<ViewNode>();
	}

	public void setReady() {
		this.state = State.READY;
	}

	void setState(State s) {
		this.state = s;
	}

	State getState() {
		return this.state;
	}

}
