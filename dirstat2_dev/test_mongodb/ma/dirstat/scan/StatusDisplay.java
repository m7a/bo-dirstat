package ma.dirstat.scan;

import ma.dirstat.FileSize;

class StatusDisplay extends Thread {

	private static final int STATUS_INTERVAL = 1000;

	private long files,  filesS;
	private long dirs,   dirsS;
	private long errors, errorsS;
	private long size,   sizeS;

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
			"STATUS f=" + files + " (+" + filesS + "), d=" +
			dirs + " (+" + dirsS + "), e=" + errors + " (+" +
			errorsS + "), s=" + FileSize.format(size) + " (+" +
			FileSize.format(sizeS) + ")"
		);
		filesS  = 0;
		dirsS   = 0;
		errorsS = 0;
		sizeS   = 0;
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

}
