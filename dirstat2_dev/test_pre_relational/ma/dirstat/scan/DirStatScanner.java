package ma.dirstat.scan;

import java.io.IOException;

import java.nio.file.*;
import java.nio.file.attribute.*;

import java.sql.*;
import java.util.concurrent.TimeUnit;

import ma.tools2.util.ErrorInfo;

public class DirStatScanner implements FileVisitor<Path> {

	private static final int STATUS_INTERVAL = 1000;

	private final Connection db;
	private final String name;
	private final Iterable<Path> paths;

	private long files;
	private long filesS;
	private long dirs;
	private long dirsS;
	private long errors;
	private long errorsS;
	private boolean on;

	public DirStatScanner(Connection db, String name,
						Iterable<Path> paths) {
		super();
		this.db    = db;
		this.paths = paths;
		this.name  = name;
		files      = 0;
		filesS     = 0;
		dirs       = 0;
		dirsS      = 0;
		errors     = 0;
		errorsS    = 0;
		on         = true;
	}

	public void run() throws IOException {
		Thread status = new Thread() {
			public void run() {
				while(on) {
					printStatus();
					try {
						sleep(STATUS_INTERVAL);
					} catch(InterruptedException ex) {
					}
				}
			}
		};
		status.start();
		try {
			for(Path i: paths) {
				try {
				
					Files.walkFileTree(i, this);
				} catch(IOException ex) {
					System.err.println("ERROR SCANNING " +
									i);
					ex.printStackTrace();
					errors++;
				}
			}
		} finally {
			on = false;
			try {
				status.join();
			} catch(InterruptedException ex) {
			}
		}
	}

	private void printStatus() {
		System.out.println(String.format(
			"STATUS  %10d files (+%3d)  %7d dirs (+%3d)  %d " +
								"errors (+%d)",
			files, filesS, dirs, dirsS, errors, errorsS
		));
		filesS  = 0;
		dirsS   = 0;
		errorsS = 0;
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
		files++;
		filesS++;
		return FileVisitResult.CONTINUE;
	}

	private void procSafe(Path p, BasicFileAttributes a) {
		try {
			proc(p, a);
		} catch(IOException ex) {
			visitFailed(p, ex);
		} catch(SQLException ex) {
			ex.printStackTrace();
			System.exit(64);
		}
	}

	private void proc(Path path, BasicFileAttributes a) throws IOException,
								SQLException {
		// TODO REALLY UNSAFE JUST FOR TESTING PURPOSES
		String query = "INSERT INTO `" + name + "` VALUES (";

		Path pA = path.toAbsolutePath();
		String name = str(pA.getFileName());
		int dot = name.indexOf('.');
		String ext = dot == -1? "NULL": "'" + name.substring(dot + 1);

		final String[] values = new String[] {
			str(pA), str(pA.getRoot()), name, ext, str(a.fileKey()),
			time(a.creationTime()), bool(a.isDirectory()),
			bool(a.isRegularFile()), bool(a.isSymbolicLink()),
			time(a.lastAccessTime()), time(a.lastModifiedTime()),
			String.valueOf(a.size()), bool(Files.exists(pA)),
			str(Files.getOwner(pA).getName()),
			bool(Files.isExecutable(pA)), bool(Files.isHidden(pA)),
			bool(Files.isReadable(pA)), bool(Files.isWritable(pA)),
			"NULL"
		};

		for(int i = 0; i < values.length; i++) {
			query += values[i];
			if(i != values.length - 1)
				query += ", ";
		}

		query += ");";

		db.createStatement().execute(query);
	}

	private static String str(Object s) {
		if(s == null)
			return "NULL";
		else
			return "'" + s + "'";
	}

	private static String time(FileTime t) {
		return "FROM_UNIXTIME(" + t.to(TimeUnit.SECONDS) + ")";
	}

	private static String bool(boolean bool) {
		if(bool)
			return "1";
		else
			return "0";
	}

	private void visitFailed(Path p, IOException ex) {
		try {
			visitFailedCritical(p, ex);
		} catch(Exception t) {
			System.err.println("ERROR LOGGING " + p.toString());
			t.printStackTrace();
		} finally {
			errors++;
			errorsS++;
		}
	}

	private void visitFailedCritical(Path p, IOException ex)
							throws SQLException {
		System.err.println("ERROR VISITING " + p.toString());
		Path use;
		try {
			use = p.toAbsolutePath();
		} catch(Exception exx) {
			use = p;
			System.err.println("ERROR CONVERTING " + p.toString());
		}
		db.createStatement().execute("INSERT INTO `" + name +
			"`(path, error) VALUES('" + use.toString() + "', '" +
			ErrorInfo.getStackTrace(ex).toString() + "');");
	}

	@Override
	public FileVisitResult postVisitDirectory(Path d, IOException es)
							throws IOException {
		dirs++;
		dirsS++;
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException es)
							throws IOException {
		visitFailed(file, es);
		return FileVisitResult.CONTINUE;
	}
}
