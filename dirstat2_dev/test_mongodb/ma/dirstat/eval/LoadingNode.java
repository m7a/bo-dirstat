package ma.dirstat.eval;

class LoadingNode extends ViewNode {

	private static final Object CHARACTERISTIC_OBJECT = new Object();

	LoadingNode(View root) {
		super(root, CHARACTERISTIC_OBJECT);
		id           = "Loading...";
		pattern      = "...";
		biggestPath  = "TBD";
		biggestBytes = 0;
		children     = new ViewNode[0];
		setState(State.READY);
	}

}
