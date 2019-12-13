package ma.dirstat.eval;

import com.mongodb.DBObject;
import com.mongodb.DBCollection;

public class ViewReferenceData {

	public final ViewNodeFactory fact;
	public final DBCollection db;

	public final DBObject[] aggregationFilters;
	public final DBObject[] mapReduceFilters;

	public final Object referenceObject;

	ViewReferenceData(ViewNodeFactory fact, DBCollection db,
						DBObject[] aggregationFilters,
						DBObject[] mapReduceFilters) {
		super();
		this.fact               = fact;
		this.db                 = db;
		this.aggregationFilters = aggregationFilters;
		this.mapReduceFilters   = mapReduceFilters;
		this.referenceObject    = new Object();
	}

}
