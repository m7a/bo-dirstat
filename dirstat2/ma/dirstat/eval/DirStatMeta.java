package ma.dirstat.eval;

import java.sql.*;

public class DirStatMeta {

	private static final String SELECT = 
		"SELECT id, name, creation, paths, sep, conf_exists, " +
			"conf_owner, conf_hidden, conf_r, conf_w, conf_x, " +
			"java_version, java_vm_version, java_vm_name, " +
			"os_name, os_version, user_name, user_home " +
		"FROM scans " +
		"WHERE name = ?;";

	public final int id;
	public final String name;
	public final long creation;
	public final String[] paths;
	public final char sep;

	public final Conf conf;

	public class Conf {

		public final boolean exists;
		public final boolean owner;
		public final boolean hidden;
		public final boolean r;
		public final boolean w;
		public final boolean x;

		Conf(boolean exists, boolean owner, boolean hidden, boolean r,
							boolean w, boolean x) {
			super();
			this.exists = exists;
			this.owner  = owner;
			this.hidden = hidden;
			this.r      = r;
			this.w      = w;
			this.x      = x;
		}
	}

	public final String os_name;
	public final String os_version;
	public final String user_name;
	public final String user_home;

	DirStatMeta(Connection db, String name) throws SQLException {
		super();
		this.name = name;


		PreparedStatement st = db.prepareStatement(SELECT);
		st.setString(1, name);
		ResultSet results = st.executeQuery();

		boolean hasN = results.next();
		assert hasN;

		id         = results.getInt("id");
		creation   = results.getTimestamp("creation").getTime();
		paths      = (String[])(results.getArray("paths").getArray());
		sep        = results.getString("sep").charAt(0);

		conf = new Conf(
			results.getBoolean("conf_exists"),
			results.getBoolean("conf_owner"),
			results.getBoolean("conf_hidden"),
			results.getBoolean("conf_r"),
			results.getBoolean("conf_w"),
			results.getBoolean("conf_x")
		);

		os_name    = results.getString("os_name");
		os_version = results.getString("os_version");
		user_name  = results.getString("user_name");
		user_home  = results.getString("user_home");

		assert !results.next();

		st.close();
	}

}
