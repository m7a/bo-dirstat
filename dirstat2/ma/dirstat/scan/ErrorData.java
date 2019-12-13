package ma.dirstat.scan;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class ErrorData implements UnconnectedPreparedStatement {

	private final String path;
	private final ErrorType etype;
	private final String msg;

	ErrorData(String path, ErrorType etype, String msg) {
		super();
		this.path  = path;
		this.etype = etype;
		this.msg   = msg;
	}

	@Override
	public int addTo(int scan, int pos, PreparedStatement st)
							throws SQLException {
		st.setLong(pos++, scan);
		st.setString(pos++, path);
		st.setString(pos++, etype.toString());
		st.setString(pos++, msg);
		return pos;
	}

}
