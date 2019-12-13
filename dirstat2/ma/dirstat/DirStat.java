package ma.dirstat;

import java.io.*;
import java.nio.file.Path;

import java.sql.*;

import ma.dirstat.scan.*;
import ma.dirstat.eval.DirStatEval;

public class DirStat {

	private final String INITSQL = "init.sql";

	private final Connection db;
	private final String name;

	DirStat(Connection db, String name) {
		super();
		this.db   = db;
		this.name = name;
	}

	// Ensures collections exist etc.
	void init() throws Exception {
		if(checkTableExistence())
			return;

		StringBuilder buf = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(
					new InputStreamReader(getClass().
					getResource(INITSQL).openStream()))) {
			String line;
			while((line = reader.readLine()) != null) {
				buf.append(line);
				buf.append('\n');
			}
		}
		db.createStatement().execute(buf.toString());
	}

	private boolean checkTableExistence() throws SQLException {
		try {
			db.createStatement().execute("SELECT NULL FROM scans " +
								"LIMIT 1");
			return true;
		} catch(Exception ex) {
			return false;
		}
	}

	void eval() throws Exception {
		new DirStatEval(db, name).run();
	}

	void scan(Iterable<Path> path, DirStatConfiguration conf)
							throws Exception {
		new DirStatScanner(db, name, path, conf).run();
	}

	void ping() throws SQLException {
		if(checkTableExistence()) {
			printScans();
		} else {
			System.err.println("Missing correct DB tables.");
		}
	}

	private void printScans() throws SQLException {
		try(ResultSet rslt = db.createStatement().executeQuery(
				"SELECT name FROM SCANS ORDER BY name ASC")) {
			while(rslt.next())
				System.out.println(rslt.getString("name"));
		}
	}

	void delete() throws SQLException {
		new DirStatDeleter(db, name).run();
	}

}
