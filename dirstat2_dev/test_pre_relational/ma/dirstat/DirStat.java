package ma.dirstat;

import java.io.IOException;
import java.nio.file.Path;

import java.sql.*;

import ma.dirstat.scan.DirStatScanner;

class DirStat {

	private static final String DEFAULT_DIR_STAT_DB = "masysma_dirstat";

	private final Connection dbconn;

	DirStat(HostnamePort db) throws Exception {
		super();
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbconn = DriverManager.getConnection("jdbc:mysql://" + db.host +
				":" + db.port + "/" + DEFAULT_DIR_STAT_DB +
						"?user=root&password=testwort");
	}

	void eval(String name) {
	}

	void scan(String name, Iterable<Path> path) throws IOException,
								SQLException {
		// TODO PRONE TO SQL INJECTION
		dbconn.createStatement().execute(
			"CREATE TABLE IF NOT EXISTS `" + name + "` (\n" +
				"path VARCHAR(512) PRIMARY KEY NOT NULL,\n" +
				"root     VARCHAR(16),\n" +
				"name     VARCHAR(256),\n" +
				"ext      VARCHAR(64),\n" +
				"file_key VARCHAR(128),\n" +
				"creation DATETIME,\n" +
				"dir      TINYINT(1),\n" +
				"reg      TINYINT(1),\n" +
				"symlink  TINYINT(1),\n" +
				"access   DATETIME,\n" +
				"`mod`    DATETIME,\n" +
				"size     INT(64),\n" +
				"`exists` TINYINT(1),\n" +
				"owner    VARCHAR(32),\n" +
				"x        TINYINT(1),\n" +
				"hidden   TINYINT(1),\n" +
				"r        TINYINT(1),\n" +
				"w        TINYINT(1),\n" +
				"error    TEXT\n" +
			");"
		);
		new DirStatScanner(dbconn, name, path).run();
	}

	void ping() {
		// TODO N_IMPL
	}

	void close() throws SQLException {
		dbconn.close();
	}

}
