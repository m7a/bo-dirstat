package ma.dirstat.scan;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.sql.*;

class DataQueue<T extends UnconnectedPreparedStatement> extends Thread {

	private static final int QUEUE_MAX = 600;

	private final StatusDisplay status;
	private final LinkedBlockingDeque<MultiInsert<T>> queue;
	private final PreparedStatementBuffer buf;

	private MultiInsert<T> current;
	private boolean on;

	DataQueue(StatusDisplay status, Connection db, int query)
							throws SQLException {
		super();
		this.status = status;
		queue       = new LinkedBlockingDeque<MultiInsert<T>>();
		buf         = new PreparedStatementBuffer(db, query, status);
		current     = null;
		on          = true;
	}

	void setScan(int scan) {
		buf.setScan(scan);
	}

	void queue(T elem) {
		if(current == null)
			current = new MultiInsert<T>();
		current.add(elem);
		if(current.isFull())
			queue();
	}

	void close() {
		if(current != null && !current.isEmpty())
			queue();
		on = false;
		current = new MultiInsert<T>(true);
		queueOnly();
	}
	
	private void queue() {
		queueOnly();
		status.logQueue();
	}

	private void queueOnly() {
		try {
			queue.put(current);
		} catch(InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		current = null;
	}

	public void run() {
		try {
			while(on || (queue.peek() != null)) {
				MultiInsert<? extends
						UnconnectedPreparedStatement> i;
				try {
					i = queue.take();
				} catch(InterruptedException ex) {
					// Unexpected:
					ex.printStackTrace();
					on = false;
					continue;
				}
				if(i.isEOF())
					continue;
				buf.add(i);
			}
			buf.close();
		} catch(SQLException ex) {
			System.out.println(buf.toString());
			System.err.println("FATAL DATABASE ERROR(s)");
			do {
				ex.printStackTrace();
			} while((ex = ex.getNextException()) != null);
			System.exit(2);
		} catch(Exception ex) {
			System.err.println("DATABASE ERROR");
			ex.printStackTrace();
		}
	}

}
