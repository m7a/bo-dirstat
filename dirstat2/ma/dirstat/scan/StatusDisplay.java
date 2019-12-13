package ma.dirstat.scan;

import ma.dirstat.FileSize;

class StatusDisplay extends Thread {

	private static final int STATUS_INTERVAL = 1000;

	private long files,  filesS;
	private long dirs,   dirsS;
	private long errors, errorsS;
	private long size,   sizeS;
	private long queued, queuedS;
	private int  commit, commitS;

	private boolean on;

	StatusDisplay() {
		super();
		files   = 0;
		filesS  = 0;
		dirs    = 0;
		dirsS   = 0;
		errors  = 0;
		errorsS = 0;
		size    = 0;
		sizeS   = 0;
		commit  = 0;
		commitS = 0;
		on      = true;
	}

	public void interrupt() {
		super.interrupt();
		on = false;
	}

	public void run() {
		while(on) {
			printStatus();
			try {
				sleep(STATUS_INTERVAL);
			} catch(InterruptedException ex) {
			}
		}
	}

	private void printStatus() {
		System.out.println(
			"f=" + files + " +" + filesS + " d=" + dirs + " +" +
			dirsS + " e=" + errors + " +" + errorsS + " s=" +
			FileSize.format(size) + " +" + FileSize.format(sizeS) +
			" c/q=" + commit + "/" + formatQueued(queued) + " +" +
			commitS + "/+" + formatQueued(queuedS)
		);
		filesS  = 0;
		dirsS   = 0;
		errorsS = 0;
		sizeS   = 0;
		queuedS = 0;
		commitS = 0;
	}

	private static String formatQueued(long queued) {
		return String.valueOf(queued /
					PreparedStatementBuffer.OUTER_LIM);
	}

	void logError() {
		errors++;
		errorsS++;
	}

	void logFile(long size) {
		files++;
		filesS++;
		this.size += size;
		sizeS += size;
	}

	void logDir() {
		dirs++;
		dirsS++;
	}

	void logCommit() {
		commit++;
		commitS++;
	}

	void logQueue() {
		queued++;
		queuedS++;
	}

}
