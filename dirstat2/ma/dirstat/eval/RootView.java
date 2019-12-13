package ma.dirstat.eval;

import java.util.Arrays;
import java.util.ArrayList;

import ma.tools2.util.NotImplementedException;

class RootView implements View {

	private final DirStatTree tree;
	private final ViewNode rn;
	private final ArrayList<View> views;

	private ViewReferenceData ref;

	RootView(DirStatTree tree) {
		super();
		this.tree = tree;
		rn = new ViewNode(this, null);
		views = new ArrayList<View>();
	}

	public void setReferenceData(ViewReferenceData data) {
		ref = data;
	}

	public void create() {
		rn.id = ref.meta.name;
		rn.pattern = "";
		rn.createChildren();
		rn.setReady();
	}

	public LoadingNode getLoading() {
		 throw new NotImplementedException();
	}

	public ViewNode getRoot() {
		return rn;
	}
	
	public void populateChildren(ViewNode parent) {
		throw new NotImplementedException();
	}

	public PreparedFilter createFilter(ViewNode below) {
		PreparedFilter ret = new PreparedFilter();
		ret.set("files.scan = ?", Arrays.asList(new Object[] {
								ref.meta.id }));
		return ret;
	}

	public PreparedFilter getPrevFilter() {
		return PreparedFilter.EMPTY;
	}

	void addView(ViewNode below, View view) {
		assert below != null;
		final PreparedFilter filter = below.root.getPrevFilter().merge(
						below.root.createFilter(below));
		view.setReferenceData(new ViewReferenceData(view, ref.db,
							filter, ref.meta));
		view.create();
		views.add(view);
		rn.children.add(view.getRoot());
	}

	void removeView(View view) {
		boolean ok = rn.children.remove(view.getRoot());
		assert ok;
		ok = views.remove(view);
		assert ok;
	}

	int indexOf(View view) {
		return views.indexOf(view);
	}

	DirStatMeta getMeta() {
		return ref.meta;
	}

}
