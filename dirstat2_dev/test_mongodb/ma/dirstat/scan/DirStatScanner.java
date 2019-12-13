package ma.dirstat.scan;

import java.io.IOException;

import java.nio.file.*;
import java.nio.file.attribute.*;

import ma.tools2.util.ErrorInfo;

import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteConcern;

// Normal errors  VISITING OWNER (SCANNING)
// Fatal errors   everything else.
public class DirStatScanner implements FileVisitor<Path> {

	private static final String META_ID = "DIRSTAT_META";

	private static final String[] PROPERTIES = {
		"java.version", "java.vm.version", "java.vm.name",
		"os.name", "os.version", "user.name", "user.home"
	};

	private final DBCollection db;
	private final Iterable<Path> paths;
	private final DirStatConfiguration conf;
	private final StatusDisplay status;

	public DirStatScanner(DBCollection db, Iterable<Path> paths,
						DirStatConfiguration conf) {
		super();
		this.db    = db;
		this.paths = paths;
		this.conf  = conf;
		status     = new StatusDisplay();
		db.setWriteConcern(WriteConcern.UNACKNOWLEDGED);
	}

	public void run() throws IOException {
		putMeta();
		status.start();
		try {
			for(Path i: paths) {
				try {
				
					Files.walkFileTree(i, this);
				} catch(IOException ex) {
					incerr("SCANNING", i);
					ex.printStackTrace();
				}
			}
		} finally {
			status.interrupt();
			try {
				status.join();
			} catch(InterruptedException ex) {
			}
		}
	}

	private void putMeta() {
		BasicDBObject o = new BasicDBObject("_id", META_ID);
		o.put("creation", System.currentTimeMillis());
		o.put("paths",    toStringArray(paths));
		o.put("sep",      FileSystems.getDefault().getSeparator());
		for(String i: PROPERTIES)
			o.put(i.replace('.', '_'), System.getProperty(i));
		db.insert(o);
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

	@Override
	public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes a)
							throws IOException {
		procSafe(d, a);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes a)
							throws IOException {
		procSafe(file, a);
		return FileVisitResult.CONTINUE;
	}

	private void procSafe(Path p, BasicFileAttributes a) {
		try {
			proc(p, a);
		} catch(IOException ex) {
			visitFailed(p, ex);
		}	
	}

	private void proc(Path path, BasicFileAttributes a) throws IOException {
		Path pA = path.toAbsolutePath();
		BasicDBObject o = new BasicDBObject("_id", pA.toString());
		putGenerallyAvailableInformation(pA, o);
		putIfNotNull(o, "file_key", a.fileKey());
		o.put("creation", a.creationTime().toMillis());
		o.put("dir",      a.isDirectory());
		o.put("reg",      a.isRegularFile());
		o.put("symlink",  a.isSymbolicLink());
		o.put("access",   a.lastAccessTime().toMillis());
		o.put("mod",      a.lastModifiedTime().toMillis());
		final long size = a.size();
		o.put("size", size);
		status.logFile(size);
		if(conf.owner.getValue()) {
			try {
				o.put("owner", Files.getOwner(pA).getName());
			} catch(IOException ex) {
				incerr("OWNER", pA);
				o.put("error",
					ErrorInfo.getStackTrace(ex).toString());
				o.put("etype", "owner");
			}
		}
		db.insert(o);
	}

	private void incerr(String msg, Path path) {
		System.err.println("ERROR " + msg + " " + path.toString());
		status.logError();
	}

	private void putGenerallyAvailableInformation(Path path,
							BasicDBObject o) {
		putIfNotNull(o, "root", path.getRoot());
		o.put("visited", System.currentTimeMillis());
		Path fn = path.getFileName();
		if(fn != null) {
			String fns = fn.toString();
			o.put("name", fns);
			int dot = fns.lastIndexOf('.');
			if(dot != -1)
				o.put("ext", fns.substring(dot + 1));
		}
		if(conf.exists.getValue())
			o.put("exists", Files.exists(path));
		try {
			if(conf.hidden.getValue())
				o.put("hidden", Files.isHidden(path));
			if(conf.r.getValue())
				o.put("r", Files.isReadable(path));
			if(conf.w.getValue())
				o.put("w", Files.isWritable(path));
			if(conf.x.getValue())
				o.put("x", Files.isExecutable(path));
		} catch(IOException ex) {
			incerr("GENERAL", path);
			o.put("error", ErrorInfo.getStackTrace(ex).toString());
			o.put("etype", "general");
		}
	}

	private void putIfNotNull(BasicDBObject o, String key, Object value) {
		if(value != null)
			o.put(key, value.toString());
	}

	private void visitFailed(Path p, IOException ex) {
		incerr("VISITING", p);
		try {
			visitFailedCritical(p, ex);
		} catch(Exception t) {
			incerr("LOGGING", p);
			t.printStackTrace();
		} 
	}

	private void visitFailedCritical(Path p, IOException ex) {
		Path use;
		try {
			use = p.toAbsolutePath();
		} catch(Exception exx) {
			use = p;
			incerr("CONVERTING", p);
		}
		BasicDBObject o = new BasicDBObject("_id", use.toString());
		o.put("error", ErrorInfo.getStackTrace(ex).toString());
		o.put("etype", "visiting");
		putGenerallyAvailableInformation(p, o);
		db.insert(o);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path d, IOException es)
							throws IOException {
		status.logDir();
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException es)
							throws IOException {
		visitFailed(file, es);
		return FileVisitResult.CONTINUE;
	}

}
