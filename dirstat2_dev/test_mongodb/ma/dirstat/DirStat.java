package ma.dirstat;

import java.io.IOException;
import java.nio.file.Path;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import ma.dirstat.scan.*;
import ma.dirstat.eval.DirStatEval;

public class DirStat {

	private static final String DEFAULT_DIR_STAT_DB = "masysma_dirstat";

	private final MongoClient dbconn;
	private final DB db;

	DirStat(HostnamePort db) throws MongoException, UnknownHostException {
		super();
		dbconn = new MongoClient(db.host, db.port);
		this.db = dbconn.getDB(DEFAULT_DIR_STAT_DB);
	}

	void eval(String name) throws Exception {
		DBCollection c = db.getCollection(name);
		new DirStatEval(c, this).run();
	}

	void scan(String name, Iterable<Path> path, DirStatConfiguration conf)
							throws IOException {
		DBCollection c = db.getCollection(name);
		new DirStatScanner(c, path, conf).run();
	}

	void ping() {
		for(String i: db.getCollectionNames())
			System.out.println(i);
	}

	public void close() {
		dbconn.close();
	}

}
