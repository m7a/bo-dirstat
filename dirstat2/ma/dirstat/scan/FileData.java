package ma.dirstat.scan;

import java.nio.file.attribute.FileTime;

import java.sql.*;

class FileData implements UnconnectedPreparedStatement {

	//-----------------------------------------------------[ Definitions ]--

	private final String path;
	private final Timestamp visited;

	private String root             = null;
	private String name             = null;
	private String ext              = null;
	private String file_key         = null;
	private Timestamp time_creation = null; // underscore like in DB
	private DBBool dir              = DBBool.UNDEFINED;
	private DBBool reg              = DBBool.UNDEFINED;
	private DBBool symlink          = DBBool.UNDEFINED;
	private Timestamp time_access   = null;
	private Timestamp time_mod      = null;
	private long file_size          = -1;

	private OptionalData optional   = null;

	FileData(String path) {
		super();
		this.path = path;
		visited = DataProcessor.now();
	}

	//---------------------------------------------------[ DB Connection ]--

	@Override
	public int addTo(int scan, int pos, PreparedStatement st)
							throws SQLException {
		st.setInt(pos++, scan);
		st.setString(pos++, path);

		// notice the following 2 entries are the only ones presented in
		// a different order in the field list than in the database
		st.setString(pos++, root);
		st.setTimestamp(pos++, visited);

		st.setString(pos++, name);
		st.setString(pos++, ext);
		st.setString(pos++, file_key);
		st.setTimestamp(pos++, time_creation);
		dir.rebool(st, pos++);
		reg.rebool(st, pos++);
		symlink.rebool(st, pos++);
		st.setTimestamp(pos++, time_access);
		st.setTimestamp(pos++, time_mod);
		if(file_size == -1)
			st.setNull(pos++, Types.BIGINT);
		else
			st.setLong(pos++, file_size);
		return pos;
	}

	OptionalData detachOptional() {
		final OptionalData ret = optional;
		optional = null; // A hint to the GC
		return ret;
	}

	//---------------------------------------------- [ Normal attributes ]--

	void setRoot(Object root) {
		if(root != null)
			this.root = root.toString();
	}

	void setName(String name) {
		this.name = name;
	}

	void setExt(String ext) {
		this.ext = ext;
	}

	void setFileKey(Object file_key) {
		if(file_key != null)
			this.file_key = file_key.toString();
	}

	void setCreation(FileTime time_creation) {
		this.time_creation = conv(time_creation);
	}

	private static Timestamp conv(FileTime time) {
		return new Timestamp(time.toMillis());
	}

	void setDir(boolean dir) {
		this.dir = bool(dir);
	}

	void setReg(boolean reg) {
		this.reg = bool(reg);
	}

	void setSymlink(boolean symlink) {
		this.symlink = bool(symlink);
	}

	void setAccess(FileTime time_access) {
		this.time_access = conv(time_access);
	}

	void setMod(FileTime time_mod) {
		this.time_mod = conv(time_mod);
	}

	void setSize(long file_size) {
		this.file_size = file_size;
	}

	//-------------------------------------------- [ Optional attributes ]--

	void setExists(boolean exists) {
		optionalReq();
		optional.fexists = bool(exists);
	}

	private void optionalReq() {
		if(optional == null)
			optional = new OptionalData(path);
	}

	private static DBBool bool(boolean bool) {
		if(bool)
			return DBBool.TRUE;
		else
			return DBBool.FALSE;
	}

	void setHidden(boolean hidden) {
		optionalReq();
		optional.hidden = bool(hidden);
	}

	void setR(boolean r) {
		optionalReq();
		optional.r = bool(r);
	}

	void setW(boolean w) {
		optionalReq();
		optional.w = bool(w);
	}

	void setX(boolean x) {
		optionalReq();
		optional.x = bool(x);
	}

	void setOwner(String owner) {
		optionalReq();
		optional.owner = owner;
	}

}
