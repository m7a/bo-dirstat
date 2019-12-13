package ma.dirstat.scan;

import java.util.*;

class MultiInsert<T extends UnconnectedPreparedStatement>
							implements Iterable<T> {

	static final int N = 100;

	private final ArrayList<T> sub;

	MultiInsert() {
		this(false);
	}

	MultiInsert(boolean eof) {
		super();
		if(eof)
			sub = null;
		else
			sub = new ArrayList<T>(N);
	}

	void add(T st) {
		sub.add(st);
	}

	boolean isFull() {
		return sub.size() >= N;
	}

	boolean isEmpty() {
		return sub.size() == 0;
	}

	int getSize() {
		return sub.size();
	}

	public Iterator<T> iterator() {
		return sub.iterator();
	}

	boolean isEOF() {
		return sub == null;
	}

}
