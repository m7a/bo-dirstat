package ma.dirstat.scan;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class OptionalData implements UnconnectedPreparedStatement {

	private final String path;

	// Filled in @FileData
	String owner    = null;
	DBBool fexists  = DBBool.UNDEFINED;
	DBBool hidden   = DBBool.UNDEFINED;
	DBBool r        = DBBool.UNDEFINED;
	DBBool w        = DBBool.UNDEFINED;
	DBBool x        = DBBool.UNDEFINED;

	OptionalData(String path) {
		super();
		this.path = path;
	}

	public int addTo(int scan, int pos, PreparedStatement st)
							throws SQLException {
		st.setInt(pos++, scan);
		st.setString(pos++, path);
		st.setString(pos++, owner);
		fexists.rebool(st, pos++);
		hidden.rebool(st, pos++);
		r.rebool(st, pos++);
		w.rebool(st, pos++);
		x.rebool(st, pos++);
		return pos;
	}
}
