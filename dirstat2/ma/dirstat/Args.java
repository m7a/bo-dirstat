package ma.dirstat;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import java.sql.Connection;

import ma.tools2.util.NotImplementedException;
import ma.tools2.args.*;
import ma.tools2.args.conv.*;

import ma.dirstat.scan.DirStatConfiguration;

public class Args extends AbstractArgs {

	public static final String NAME            = "Ma_Sys.ma DirStat";
	public static final String VERSION         = "2.0.0.5";
	public static final String DB_NAME         = "masysma_dirstat";
	public static final String COPYRIGHT_YEARS = "2013, 2014";

	private final Parameter<Boolean> eval;
	private final Parameter<Boolean> scan;
	private final Parameter<Boolean> ping;
	private final Parameter<Boolean> delete;
	final Parameter<Connection>      db;
	final Parameter<String>          name;
	final Parameter<Iterable<Path>>  path;
	final DirStatConfiguration       conf;

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
		dbList.add(db = new Parameter<Connection>(
			"db", 'd', null, new DBConverter(),
			"Specifies the database (PostgreSQL) to use."
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
		scanList.add(conf = new DirStatConfiguration());
		ArgumentList pathList = new ArgumentList(null,
						ArgumentComposition.OPTIONAL);
		pathList.add(path = new Parameter<Iterable<Path>>(
			"src", 'i',
			FileSystems.getDefault().getRootDirectories(),
			new MultipleSourceConverter(),
			"Files and directories to scan."
		));
		scanList.add(pathList);
		add(scanList);

		ArgumentList pingList = new ArgumentList("Ping database",
						ArgumentComposition.AND);
		pingList.add(ping = new Parameter<Boolean>(
			"ping", 'p', false, new BooleanConverter(), null
		));
		pingList.add(dbList);
		add(pingList);

		ArgumentList delList = new ArgumentList("Delete Scan",
						ArgumentComposition.AND);
		delList.add(delete = new Parameter<Boolean>(
			"delete", 'd', false, new BooleanConverter(), null
		));
		delList.add(dbList);
		delList.add(nameList);
		add(delList);
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
		if(delete.getValue())
			return Mode.DELETE;
		if(scan.getValue())
			return Mode.SCAN;
		throw new NotImplementedException();
	}

}
