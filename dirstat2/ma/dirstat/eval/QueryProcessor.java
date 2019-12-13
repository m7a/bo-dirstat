package ma.dirstat.eval;

import java.sql.ResultSet;
import java.sql.SQLException;

interface QueryProcessor {

	public void process(ResultSet results) throws SQLException;

}
