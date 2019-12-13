package ma.dirstat.eval;

import java.io.*;

class FileBoundView extends BoundView {

	private final File file;

	FileBoundView(ViewFactory fact, File file) throws Exception {
		super(fact);
		this.file = file;
		init();
	}

	public Reader createReader() throws Exception {
		return new FileReader(file);
	}

}
