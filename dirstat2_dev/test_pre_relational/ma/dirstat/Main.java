package ma.dirstat;

public class Main {

	private final Args args;

	private Main(Args a) {
		super();
		args = a;
	}

	private void act() throws Exception {
		final DirStat d = new DirStat(args.db.getValue());
		try {
			switch(args.getMode()) {
			case EVAL: d.eval(args.name.getValue()); break;
			case SCAN: d.scan(args.name.getValue(),
						args.path.getValue()); break;
			case PING: d.ping(); break;
			}
		} finally {
			d.close();
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
