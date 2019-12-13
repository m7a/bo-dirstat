import java.io.*;
import java.util.*;
import java.sql.*;

// _id,root,visited,name,ext,creation,dir,reg,symlink,access,mod,size,error
// ./mongoexport -d masysma_dirstat -c linux3 -f _id,root,visited,name,ext,creation,dir,reg,symlink,access,mod,size,error --csv -o linux3.csv
public class MySQLCsvImport {

	private static final String DB = "dirstat_test";
	private static final String TABLE = "files";
	private static final int BUFSIZ = 0x1000;
	private static final char COMMA = ',';

	public static void main(String[] args) throws Exception {
		CSVTokenizer csv = new CSVTokenizer(new BufferedReader(
				new InputStreamReader(System.in)), COMMA);
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection dbconn = DriverManager.getConnection(
			"jdbc:mysql://127.0.0.1:3306/" + DB +
					"?user=root&password=testwort");

		dbconn.createStatement().execute(
			"CREATE TABLE IF NOT EXISTS `" + TABLE + "` (\n" +
			"id       SERIAL,\n" +
			"path     VARCHAR(512) NOT NULL,\n" +
			"root     VARCHAR(16),\n" +
			"visited  DATETIME,\n" +
			"name     VARCHAR(256),\n" +
			"ext      VARCHAR(64),\n" +
			"creation DATETIME,\n" +
			"dir      TINYINT(1),\n" +
			"reg      TINYINT(1),\n" +
			"symlink  TINYINT(1),\n" +
			"access   DATETIME,\n" +
			"`mod`    DATETIME,\n" +
			"size     BIGINT(128),\n" +
			"error    TEXT\n" +
			") ENGINE=InnoDB;"
		);

		// Debug only of course
		dbconn.createStatement().execute("TRUNCATE TABLE `" +
								TABLE + "`");

		String[] valuebuf = new String[BUFSIZ];
		for(int i = 0; i < valuebuf.length; i++)
			valuebuf[i] = null;

		long start = System.currentTimeMillis();

		StringBuilder query = createStringBuilder();
		csv.next(); // Skip first line.
		int n = 0;
		while(csv.hasNext()) {
			proclin(query, csv.next());
			if(n++ == BUFSIZ) {
				try {
					execQuery(dbconn, query);
				} catch(Exception ex) {
					System.err.println("FAILURE");
					System.err.println("-- QUERY --");
					System.err.println(query.toString());
					System.err.println("-- END --");
					ex.printStackTrace();
					System.exit(64);
				}
				query = createStringBuilder();
				n = 0;
			}
		}

		if(n != 0)
			execQuery(dbconn, query);

		System.out.println(System.currentTimeMillis() - start);

		dbconn.close();
		csv.close();
	}

	private static StringBuilder createStringBuilder() {
		StringBuilder inlined = new StringBuilder("INSERT INTO `");
		inlined.append(TABLE);
		inlined.append("` (path, root, visited, name, ext, creation," +
			" dir, reg, symlink, access, `mod`, size, error)" +
								" VALUES ");
		return inlined;
	}

	private static void proclin(StringBuilder query, List<String> ln) {
		query.append("(");

		Iterator<String> iter = ln.iterator();
		final String[] values = new String[] {
			str(iter.next()),
			str(iter.next()),
			time(iter.next()),
			str(iter.next()),
			str(iter.next()),
			time(iter.next()),
			bool(iter.next()),
			bool(iter.next()),
			bool(iter.next()),
			time(iter.next()),
			time(iter.next()),
			ne(iter.next()),
			str(iter.next())
		};

		for(int i = 0; i < values.length; i++) {
			query.append(values[i]);
			if(i != values.length - 1)
				query.append(", ");
		}

		query.append("\n),");
	}

	private static String ne(String s) {
		if(s.length() == 0)
			return "NULL";
		else
			return s;
	}

	private static String str(String s) {
		if(s.length() == 0)
			return "NULL";
		else
			return "'" + esc(s) + "'";
	}

	// https://www.owasp.org/index.php/SQL_Injection_Prevention_Cheat_Sheet#Defense_Option_3%3A_Escaping_All_User_Supplied_Input
	private static String esc(String s) {
		char[] sr = "\\\"'".toCharArray();
		for(int i = 0; i < sr.length; i++)
			s = s.replace(String.valueOf(sr[i]), "\\" + sr[i]);
		return s;
	}

	private static String time(String t) {
		if(t.length() == 0)
			return "NULL";
		else
			return "FROM_UNIXTIME(" + (Long.parseLong(t) / 1000)
									+ ")";
	}

	private static String bool(String bool) {
		if(bool.equals("true"))
			return "1";
		else
			return "0";
	}

	private static void execQuery(Connection db, StringBuilder query)
							throws Exception {
		db.createStatement().execute(query.substring(0,
						query.length() - 1) + ";");
	}

}
