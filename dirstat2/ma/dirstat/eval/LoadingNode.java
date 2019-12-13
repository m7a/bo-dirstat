package ma.dirstat.eval;

public class LoadingNode extends ViewNode {

	private static final Object CHARACTERISTIC_OBJECT = new Object();

	LoadingNode(View root) {
		super(root, CHARACTERISTIC_OBJECT);
		id           = "Loading...";
		pattern      = "...";
		biggestPath  = "TBD";
		biggestBytes = 0;
		createChildren();
		setState(State.READY);
	}

}
