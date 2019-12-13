package ma.dirstat.eval;

import java.io.*;

class JarBoundView extends BoundView {

	private final String name;

	JarBoundView(ViewFactory fact, String name) throws Exception {
		super(fact);
		this.name = name;
		init();
	}

	public Reader createReader() throws Exception {
		return new BufferedReader(new InputStreamReader(
			getClass().getResource("js/" + name).openStream()));
	}

	public String toString() {
		return name + " (" + super.toString() + ")";
	}

}
