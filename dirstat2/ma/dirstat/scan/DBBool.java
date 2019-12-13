package ma.dirstat.scan;

import java.sql.*;

enum DBBool {

	TRUE, FALSE, UNDEFINED;

	void rebool(PreparedStatement st, int pos) throws SQLException {
		switch(this) {
		case UNDEFINED: st.setNull(pos, Types.BOOLEAN); break;
		case TRUE:      st.setBoolean(pos, true);       break;
		case FALSE:     st.setBoolean(pos, false);      break;
		}
	}

}

