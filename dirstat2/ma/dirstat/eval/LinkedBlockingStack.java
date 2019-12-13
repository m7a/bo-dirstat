package ma.dirstat.eval;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

class LinkedBlockingStack<T> extends LinkedBlockingDeque<T> {

	LinkedBlockingStack() {
		super();
	}

	@Override
	public boolean add(T e) {
		super.addFirst(e);
		return true;
	}

	@Override
	public boolean offer(T e) {
		return super.offerFirst(e);
	}

	@Override
	public void put(T e) throws InterruptedException {
		super.putFirst(e);
	}

	@Override
	public boolean offer(T e, long timeout, TimeUnit unit)
						throws InterruptedException {
		return super.offerFirst(e, timeout, unit);
	}

}
