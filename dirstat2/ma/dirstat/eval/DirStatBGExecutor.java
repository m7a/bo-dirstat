package ma.dirstat.eval;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

class DirStatBGExecutor extends ThreadPoolExecutor {

	private final DirStatStatusPanel status;

	DirStatBGExecutor(DirStatStatusPanel status) {
		super(1, 1, 1, TimeUnit.DAYS,
					new LinkedBlockingStack<Runnable>());
		this.status = status;
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		status.updateTo(this);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		status.dec();
	}

}
