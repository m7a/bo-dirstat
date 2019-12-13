package ma.dirstat.eval;

public class ViewNodeFactory {

	private final View root;

	ViewNodeFactory(View root) {
		super();
		this.root = root;
	}

	public ViewNode create(Object userdata) {
		return new ViewNode(root, userdata);
	}

}
