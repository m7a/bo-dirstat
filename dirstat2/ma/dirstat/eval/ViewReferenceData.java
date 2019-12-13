package ma.dirstat.eval;

import java.sql.Connection;

public class ViewReferenceData {

	public final View view;
	public final LoadingNode loading;
	public final Connection db;
	public final PreparedFilter filter;
	public final DirStatMeta meta;
	public final Object obj;

	ViewReferenceData(View view, Connection db, PreparedFilter filter,
							DirStatMeta meta) {
		super();
		this.view    = view;
		this.loading = new LoadingNode(view);
		this.db      = db;
		this.filter  = filter;
		this.meta    = meta;
		this.obj     = new Object();
	}

}
