package ma.dirstat.eval;

import java.io.*;
import javax.script.*;

class ViewFactory {

	private static final String PRELOAD_FILE = "default.js";

	private final String preload;

	ViewFactory() throws IOException {
		super();
		try(BufferedReader r = new BufferedReader(
				new InputStreamReader(ViewFactory.class.
					getResourceAsStream(PRELOAD_FILE)))) {
			StringBuilder b = new StringBuilder();
			String line;
			while((line = r.readLine()) != null) {
				b.append(line);
				b.append('\n');
			}
			preload = b.toString();
		}
	}

	View createJAR(String name) throws Exception {
		return create(new InputStreamReader(getClass().
					getResourceAsStream("js/" + name)));
	}

	View create(Reader in) throws Exception {
		try {
			return createSub(in);
		} finally {
			in.close();
		}
	}

	private View createSub(Reader in) throws Exception {
		ScriptEngine parser = new ScriptEngineManager().
						getEngineByName("JavaScript");

		parser.eval(preload);
		parser.eval(in);

		Invocable script = (Invocable)parser;
		Object view = script.invokeFunction("create_view");
		View ret = script.getInterface(view, View.class);
		if(ret == null)
			throw new Exception("Script does not properly " +
							"implement interface.");

		return ret;
	}

}
