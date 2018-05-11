package app.utility;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

	private AtomicInteger count = new AtomicInteger(1);
	private int maxCount;

	public Counter(int maxCount) {
		if (maxCount <= 0) {
			throw new IllegalArgumentException();
		}
		this.maxCount = maxCount;
	}

	public int getProgress() {
		return count.get();
	}

	public int next() {
		int now = count.getAndIncrement();
		if (now <= 0 || now > maxCount) {
			return -1;
		}

		System.out.println(now);
		return now;
	}
}
