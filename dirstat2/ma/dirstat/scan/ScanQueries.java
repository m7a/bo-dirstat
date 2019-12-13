package ma.dirstat.scan;

final class ScanQueries {

	static final String[] PROPERTIES = {
		"java.version", "java.vm.version", "java.vm.name",
		"os.name", "os.version", "user.name", "user.home"
	};

	static final String INSERT_META =
		"INSERT INTO scans (" +
			"name, creation, paths, sep, conf_exists, " +
			"conf_owner, conf_hidden, conf_r, conf_w, conf_x, " +
			"java_version, java_vm_version, java_vm_name, " +
			"os_name, os_version, user_name, user_home" +
		") VALUES (" +
			"?, ?, ?, ?, ?," +
			"?, ?, ?, ?, ?," +
			"?, ?, ?," +
			"?, ?, ?, ?" +
		") RETURNING id;";

	static final int REGULAR  = 0;
	static final int OPTIONAL = 1;
	static final int ERROR    = 2;

	static final String[][] QUERIES = {
		{ "INSERT INTO files (" +
			"scan, path, root, visited, name, ext, file_key, " +
			"time_creation, dir, reg, symlink, time_access, " +
			"time_mod, file_size" +
		") VALUES ", "(" +
			"?,    ?,    ?,    ?,       ?,    ?,   ?, " +
			"?,             ?,   ?,   ?,       ?, " +
			"?,        ?" +
		")", }, 
		{ "INSERT INTO optional_attrs (" +
			"scan, path, owner, fexists, hidden, r, w, x" +
		") VALUES ",
			"(?, ?, ?, ?, ?, ?, ?, ?)",
		},
		{ "INSERT INTO errors (scan, path, etype, message) VALUES ",
					"(?, ?, ?::error_type, ?)", }
	};

}
