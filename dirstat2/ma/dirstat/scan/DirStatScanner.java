package ma.dirstat.scan;

import java.io.IOException;

import java.nio.file.*;
import java.nio.file.attribute.*;

import java.sql.*;

import ma.tools2.util.ErrorInfo;

public class DirStatScanner implements FileVisitor<Path> {

	private final Iterable<Path>       paths;
	private final DirStatConfiguration conf;
	private final StatusDisplay        status;
	private final DataProcessor        proc;

	public DirStatScanner(Connection db, String name, Iterable<Path> paths,
				DirStatConfiguration conf) throws SQLException {
		super();
		this.paths = paths;
		this.conf  = conf;
		status     = new StatusDisplay();
		proc       = new DataProcessor(db, name, status);
	}

	public void run() throws IOException, SQLException {
		status.start();
		try {
			proc.insertMeta(paths, conf);
			proc.start();
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
				proc.close();
				try {
					proc.join();
				} catch(InterruptedException ex) {
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

	private void incerr(String msg, Path path) {
		System.err.println("ERROR " + msg + " " + path.toString());
		status.logError();
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
		FileData o = new FileData(pA.toString());
		putGenerallyAvailableInformation(pA, o);

		o.setFileKey(a.fileKey());
		o.setCreation(a.creationTime());
		o.setDir(a.isDirectory());
		o.setReg(a.isRegularFile());
		o.setSymlink(a.isSymbolicLink());
		o.setAccess(a.lastAccessTime());
		o.setMod(a.lastModifiedTime());

		long size = a.size();
		o.setSize(size);
		status.logFile(size);

		if(conf.owner.getValue()) {
			try {
				o.setOwner(Files.getOwner(pA).getName());
			} catch(IOException ex) {
				putError(path, ErrorType.OWNER, ex);
			}
		}

		proc.queue(o);
	}

	private void putGenerallyAvailableInformation(Path path, FileData o) {
		o.setRoot(path.getRoot());
		Path fn = path.getFileName();
		if(fn != null) {
			String fns = fn.toString();
			o.setName(fns);
			int dot = fns.lastIndexOf('.');
			if(dot == -1)
				o.setExt("N_EXT");
			else
				o.setExt(fns.substring(dot + 1).toLowerCase());
		}
		if(conf.exists.getValue())
			o.setExists(Files.exists(path));
		try {
			if(conf.hidden.getValue())
				o.setHidden(Files.isHidden(path));
			if(conf.r.getValue())
				o.setR(Files.isReadable(path));
			if(conf.w.getValue())
				o.setW(Files.isWritable(path));
			if(conf.x.getValue())
				o.setX(Files.isExecutable(path));
		} catch(IOException ex) {
			putError(path, ErrorType.GENERAL, ex);
		}
	}

	private void putError(Path path, ErrorType etype, Exception ex) {
		incerr(etype.toString().toUpperCase(), path);
		proc.queue(new ErrorData(path.toString(), etype,
				ErrorInfo.getStackTrace(ex).toString()));
	}

	private void visitFailed(Path p, IOException ex) {
		try {
			visitFailedCritical(p, ex);
		} catch(Exception t) {
			incerr("LOGGING", p);
			t.printStackTrace();
		} 
	}

	private void visitFailedCritical(Path p, IOException ex)
							throws SQLException {
		Path use;
		try {
			use = p.toAbsolutePath();
		} catch(Exception exx) {
			use = p;
			incerr("CONVERTING", p);
		}
		putError(use, ErrorType.VISITING, ex);
		if(use != null) {
			FileData data = new FileData(use.toString());
			putGenerallyAvailableInformation(use, data);
			proc.queue(data);
		}
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
