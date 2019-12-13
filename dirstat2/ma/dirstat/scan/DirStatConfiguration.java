package ma.dirstat.scan;

import ma.tools2.args.*;
import ma.tools2.args.conv.BooleanConverter;

public class DirStatConfiguration extends ArgumentList {

	final Parameter<Boolean> exists;
	final Parameter<Boolean> owner;
	final Parameter<Boolean> hidden;
	final Parameter<Boolean> r, w, x;

	public DirStatConfiguration() {
		super("Additional configuration parameters to add scans at " +
					"the expense of scanning performace.",
						ArgumentComposition.OPTIONAL);
		
		add(exists = boolparam("check-exists", 'a',
			"Scans files for existence (takes ages on Windows)."));
		add(owner = boolparam("scan-owner", 'o', 
			"Scans the file owner (takes long on Windows)."));
		add(hidden = boolparam("check-hidden", 'h',
			"Checks if scanned files are considered hidden."));
		add(r = boolparam("check-r", 'r',
				"Checks if a scanned file is readable."));
		add(w = boolparam("check-w", 'w',
				"Checks if a scanned file is writable."));
		add(x = boolparam("check-x", 'x',
				"Checks if a scanned files is executable."));
	}

	private static final Parameter<Boolean> boolparam(String l, char s,
								String descr) {
		return new Parameter<Boolean>(l, s, false,
						new BooleanConverter(), descr);
	}

}
