package ma.dirstat.scan;

import java.util.concurrent.LinkedBlockingDeque;
import java.io.File;
import java.nio.file.Path;
import java.sql.*;

class DataProcessor extends Thread {

	private final Connection db;
	private final String name;
	private final StatusDisplay status;

	private final DataQueue<FileData>     fileQueue;
	private final DataQueue<OptionalData> optionalQueue;
	private final DataQueue<ErrorData>    errorQueue;

	private int scan;

	DataProcessor(Connection db, String name, StatusDisplay status)
							throws SQLException {
		super();
		this.db       = db;
		this.name     = name;
		this.status   = status;
		scan          = -1;
		fileQueue     = new DataQueue<FileData>(status, db,
							ScanQueries.REGULAR);
		optionalQueue = new DataQueue<OptionalData>(status, db,
							ScanQueries.OPTIONAL);
		errorQueue    = new DataQueue<ErrorData>(status, db,
							ScanQueries.ERROR);
	}

	void insertMeta(Iterable<Path> paths, DirStatConfiguration cfg)
							throws SQLException {
		try(PreparedStatement st = prepareMetaStatement()) {
			int pos = 0;
			st.setString(++pos, name);
			st.setTimestamp(++pos, now());
			st.setArray(++pos, db.createArrayOf("varchar",
							toStringArray(paths)));
			st.setString(++pos, File.separator);
			st.setBoolean(++pos, cfg.exists.getValue());
			st.setBoolean(++pos, cfg.owner.getValue());
			st.setBoolean(++pos, cfg.hidden.getValue());
			st.setBoolean(++pos, cfg.r.getValue());
			st.setBoolean(++pos, cfg.w.getValue());
			st.setBoolean(++pos, cfg.x.getValue());
			for(String p: ScanQueries.PROPERTIES)
				st.setString(++pos, System.getProperty(p));
			ResultSet rs = st.executeQuery();
			rs.next();
			scan = rs.getInt(1);
		}
		assert scan != 0;
		fileQueue.setScan(scan);
		optionalQueue.setScan(scan);
		errorQueue.setScan(scan);
	}

	private PreparedStatement prepareMetaStatement() throws SQLException {
		return db.prepareStatement(ScanQueries.INSERT_META);
	}

	private static String[] toStringArray(Iterable<Path> paths) {
		int i = 0;
		int n = 0;
		for(Path j: paths)
			n++;
		String[] ret = new String[n];
		for(Path j: paths)
			ret[i++] = j.toString();
		return ret;
	}

	static Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	void queue(UnconnectedPreparedStatement ust) {
		if(ust instanceof ErrorData) {
			errorQueue.queue((ErrorData)ust);
		} else {
			FileData fil = (FileData)ust;
			fileQueue.queue(fil);
			OptionalData opt = fil.detachOptional();
			if(opt != null)
				optionalQueue.queue(opt);
		}
	}

	public void start() {
		fileQueue.start();
		optionalQueue.start();
		errorQueue.start();
		super.start();
	}

	public void run() {
		assert scan != 0;
		try {
			fileQueue.join();
			optionalQueue.join();
			errorQueue.join();
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		fileQueue.close();
		optionalQueue.close();
		errorQueue.close();
	}

}
