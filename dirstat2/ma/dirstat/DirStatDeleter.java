package ma.dirstat;

import java.sql.*;

class DirStatDeleter {

	private static final String SELECT_ID = "SELECT id FROM scans WHERE " +
								"name = ?";

	private final Connection db;
	private final String name;

	DirStatDeleter(Connection db, String name) {
		super();
		this.db = db;
		this.name = name;
	}

	void run() throws SQLException {
		int id;
		try(PreparedStatement scanId = db.prepareStatement(SELECT_ID)) {
			id = getId(scanId);
		}
		delete(id, "optional_attrs");
		delete(id, "files");
		delete(id, "errors");
		delete(id, "scans", "id");
	}

	private int getId(PreparedStatement scanId) throws SQLException {
		scanId.setString(1, name);
		ResultSet result = scanId.executeQuery();
		boolean ok = result.next(); assert ok;
		int scan = result.getInt("id");
		assert !result.next();
		return scan;
	}

	private void delete(int scan, String table) throws SQLException {
		delete(scan, table, "scan");
	}

	private void delete(int scan, String table, String column)
							throws SQLException {
		try(PreparedStatement st = createDeletionQuery(table, column)) {
			st.setInt(1, scan);
			st.execute();
		}
	}

	/**
	 * Only pass hard coded strings to this function, because it does not
	 * escape input by any means (table and column names are not part of
	 * the prepared statement but directly inserted into the query)
	 * 
	 * @param table Hard Coded String
	 * @param column Hard Coded String
	 */
	private PreparedStatement createDeletionQuery(String table,
					String column) throws SQLException {
		return db.prepareStatement("DELETE FROM " + table + " WHERE " +
							 column + " = ?");
	}

}
