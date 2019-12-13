package ma.dirstat.eval;

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

	public ViewNode[] children;

	private State state;

	// TODO add detail object and populate method for that
	// DetailObject
	//	User-defined KV
	//	User-defined Textarea or list? (optional)
	//	Could the first two somehow be merged?
	//	Diagram w/ x=ln(size) y=number

	ViewNode(View root, Object userdata) {
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
