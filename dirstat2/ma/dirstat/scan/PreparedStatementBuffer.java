package ma.dirstat.scan;

import java.sql.*;

// TODO Use of connection pooling instead of shared DB connection might be good.
class PreparedStatementBuffer {

	static final int OUTER_LIM = 100;

	private final Connection db;
	private final int query;
	private final PreparedStatement st;
	private final StatusDisplay status;

	private int scan;
	private int queued;

	PreparedStatementBuffer(Connection db, int queryNum,
				StatusDisplay status) throws SQLException {
		super();
		this.db = db;
		query = queryNum;
		this.status = status;
		st = prepare(db, queryNum, MultiInsert.N);
		scan = -1;
		queued = 0;
	}

	private static PreparedStatement prepare(Connection db, int query,
						int n) throws SQLException {
		StringBuilder sql = new StringBuilder(ScanQueries.
							QUERIES[query][0]);
		for(int i = 0; i < n; i++) {
			sql.append(ScanQueries.QUERIES[query][1]);
			if(i != n - 1)
				sql.append(',');
		}
		sql.append(';');

		synchronized(db) {
			return db.prepareStatement(sql.toString());
		}
	}

	void setScan(int scan) {
		this.scan = scan;
	}

	void add(MultiInsert<? extends UnconnectedPreparedStatement> mult)
							throws SQLException {
		if(mult.isFull()) {
			add(mult, st);
			synchronized(db) {
				st.addBatch();
			}
			if(++queued >= OUTER_LIM)
				execute();
		} else {
			execute();
			PreparedStatement nw = prepare(db, query,
								mult.getSize());
			synchronized(db) {
				add(mult, nw);
				nw.execute();
				nw.close();
			}
		}
	}

	private void add(MultiInsert<? extends UnconnectedPreparedStatement>
			mult, PreparedStatement st) throws SQLException {
		int pos = 1;
		for(UnconnectedPreparedStatement i: mult) {
			synchronized(db) {
				pos = i.addTo(scan, pos, st);
			}
		}
	}

	private void execute() throws SQLException {
		if(queued != 0) {
			synchronized(db) {
				st.executeBatch();
			}
			status.logCommit();
			queued = 0;
		}
	}

	void close() throws SQLException {
		execute();
		synchronized(db) {
			st.close();
		}
	}

	public String toString() { // Debug only
		return super.toString() + " " + query;
	}

}
