package ma.dirstat;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import ma.tools2.util.NotImplementedException;
import ma.tools2.args.*;
import ma.tools2.args.conv.*;

class Args extends AbstractArgs {

	private static final String NAME            = "Ma_Sys.ma DirStat";
	private static final String VERSION         = "2.0.0.0";
	private static final String COPYRIGHT_YEARS = "2013";

	private final Parameter<Boolean> eval;
	private final Parameter<Boolean> scan;
	private final Parameter<Boolean> ping;
	final Parameter<HostnamePort>    db;
	final Parameter<String>          name;
	final Parameter<Iterable<Path>>  path;

	Args() {
		super();

		ArgumentList evaluate = new ArgumentList(
			"Evaluate database contents graphically.",
			ArgumentComposition.AND
		);
		evaluate.add(eval = new Parameter<Boolean>(
			"eval", 'e', false, new BooleanConverter(), null
		));
		ArgumentList dbList = new ArgumentList(null,
						ArgumentComposition.OPTIONAL);
		dbList.add(db = new Parameter<HostnamePort>(
			"db", 'd', new HostnamePort("127.0.0.1", 27017),
			new HostnamePortConverter(),
			"Specifies the database host and port (MongoDB)."
		));
		evaluate.add(dbList);
		ArgumentList nameList = new ArgumentList(null,
						ArgumentComposition.OPTIONAL);
		nameList.add(name = new Parameter<String>(
			"name", 'n', "default", new StringConverter(),
			"The user-defined name for the scan to open."
		));
		evaluate.add(nameList);
		add(evaluate);

		ArgumentList scanList = new ArgumentList("Scan filesystems",
						ArgumentComposition.AND);
		scanList.add(scan = new Parameter<Boolean>(
			"scan", 's', true, new BooleanConverter(), null
		));
		scanList.add(dbList);
		scanList.add(nameList);
		scanList.add(path = new Parameter<Iterable<Path>>(
			"src", 'i',
			FileSystems.getDefault().getRootDirectories(),
			new MultipleSourceConverter(),
			"Files and directories to scan."
		));
		add(scanList);

		ArgumentList pingList = new ArgumentList("Ping database",
						ArgumentComposition.AND);
		pingList.add(ping = new Parameter<Boolean>(
			"ping", 'p', false, new BooleanConverter(), null
		));
		pingList.add(dbList);
		add(pingList);
	}
	
	protected String getApplicationName() {
		return NAME;
	}

	protected String getApplicationVersion() {
		return VERSION;
	}

	protected String getCopyrightYears() {
		return COPYRIGHT_YEARS;
	}

	protected String getInvocationCommand() {
		return "java ma.dirstat.Main";
	}

	Mode getMode() {
		if(eval.getValue())
			return Mode.EVAL;
		if(ping.getValue())
			return Mode.PING;
		if(scan.getValue())
			return Mode.SCAN;
		throw new NotImplementedException();
	}

}
