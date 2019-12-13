package ma.dirstat.eval;

import java.io.Reader;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

abstract class BoundView implements View {

	private View parent;

	private final ViewFactory fact;

	final LoadingNode loading;

	BoundView(ViewFactory fact) throws Exception {
		super();
		this.fact = fact;
		loading = new LoadingNode(this);
	}

	void init() throws Exception {
		parent = fact.create(createReader());
	}

	abstract Reader createReader() throws Exception;

	public void setReferenceData(ViewReferenceData data) {
		parent.setReferenceData(data);
	}

	public void create() {
		parent.create();
	}

	public ViewNode getRoot() {
		return parent.getRoot();
	}

	public void populateChildren(ViewNode parentS) {
		parent.populateChildren(parentS);
	}

	public DBObject createMapReduceFilter(ViewNode below) {
		return parent.createMapReduceFilter(below);
	}

	public DBObject createAggregationFilter(ViewNode below) {
		return parent.createAggregationFilter(below);
	}

	public DBObject[] getPrevMapReduceFilters() {
		return parent.getPrevMapReduceFilters();
	}

	public DBObject[] getPrevAggreagtionFilters() {
		return parent.getPrevAggreagtionFilters();
	}

}
