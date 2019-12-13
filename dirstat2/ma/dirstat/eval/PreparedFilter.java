package ma.dirstat.eval;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedFilter {

	static final PreparedFilter EMPTY = new PreparedFilter();

	private String statement;
	private final ArrayList<Object> values;

	public PreparedFilter() {
		super();
		statement = null;
		values = new ArrayList<Object>();
	}

	public void set(String query, Iterable<Object> values) {
		statement = "(" + query + ")";
		for(Object i: values)
			this.values.add(i);
	}

	public PreparedFilter merge(PreparedFilter nf) {
		PreparedFilter ret = new PreparedFilter();
		ret.statement = statement;
		ret.values.addAll(values);
		if(ret.statement == null) {
			ret.set(nf.statement, nf.values);
		} else {
			ret.statement += " AND (" + nf.statement + ")";
			ret.values.addAll(nf.values);
		}
		return ret;
	}

	public PreparedStatement prepare(Connection db, String sql)
							throws SQLException {
		final String queryR;
		StringBuilder query = new StringBuilder();
		int pos;
		while((pos = sql.indexOf("WHERE ")) != -1) {
			query.append(sql.substring(0, pos));
			sql = sql.substring(pos + 6);
			query.append("WHERE ");
			query.append(statement);
			if(sql.charAt(0) == '?')
				sql = sql.substring(1);
			else
				query.append(" AND ");
		}
		query.append(sql);
		return db.prepareStatement(query.toString());
	}

	public int append(PreparedStatement st, int pos) throws SQLException {
		for(Object i: values)
			st.setObject(pos++, i);
		return pos;
	}

}
