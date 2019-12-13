package ma.dirstat.eval;

import com.mongodb.DBObject;
import com.mongodb.DBCollection;

interface View {

	void setReferenceData(ViewReferenceData data);

	void create();

	ViewNode getRoot();
	void populateChildren(ViewNode parent);

	DBObject createMapReduceFilter(ViewNode below);
	DBObject createAggregationFilter(ViewNode below);

	DBObject[] getPrevMapReduceFilters();
	DBObject[] getPrevAggreagtionFilters();

}
