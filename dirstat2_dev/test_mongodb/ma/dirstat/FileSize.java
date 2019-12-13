package ma.dirstat;

public class FileSize {

	private static final long UNITS = 100;

	public static final long ONE_KIB = 1024;
	public static final long ONE_MIB = 1024 * ONE_KIB;
	public static final long ONE_GIB = 1024 * ONE_MIB;
	public static final long ONE_TIB = 1024 * ONE_GIB;
	public static final long ONE_EIB = 1024 * ONE_TIB;

	public static String format(long size) {
		if(size > ONE_EIB)
			return divsize(size, ONE_TIB) + "E";
		else if(size > ONE_TIB)
			return divsize(size, ONE_TIB) + "T";
		else if(size > ONE_GIB)
			return divsize(size, ONE_GIB) + "G";
		else if(size > ONE_MIB)
			return divsize(size, ONE_MIB) + "M";
		else if(size > ONE_KIB)
			return divsize(size, ONE_KIB) + "K";
		else
			return size + "B";
	}

	private static String divsize(long size, long by) {
		long display = size * UNITS / by;
		return (display / UNITS) + "." + (display % UNITS);
	}


}
