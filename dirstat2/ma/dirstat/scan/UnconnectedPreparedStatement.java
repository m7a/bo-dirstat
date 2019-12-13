package ma.dirstat.scan;

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface UnconnectedPreparedStatement {

	int addTo(int scan, int pos1, PreparedStatement st) throws SQLException;

}
