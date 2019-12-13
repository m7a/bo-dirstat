package ma.dirstat;

import java.sql.Connection;

public class Main {

	private final Args args;

	private Main(Args a) {
		super();
		args = a;
	}

	private void act() throws Exception {
		Connection conn = args.db.getValue();
		if(conn == null)
			conn = DBConverter.createDefaultConnection();

		DirStat d = new DirStat(conn, args.name.getValue());
		try {
			d.init();
			act(d);
		} finally {
			if(args.getMode() != Mode.EVAL)
				conn.close();
		}
	}

	private void act(DirStat d) throws Exception {
		switch(args.getMode()) {
		case EVAL:   d.eval();                                break;
		case SCAN:   d.scan(args.path.getValue(), args.conf); break;
		case PING:   d.ping();                                break;
		case DELETE: d.delete();                              break;
		}
	}

	public static void main(String[] args) {
		Args a = new Args();
		if(a.parseAndReact(args))
			System.exit(1); // Exits with 1 even with $0 --help
		else
			act(a);
	}

	private static void act(Args a) {
		Main main = new Main(a);
		try {
			main.act();
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

}
