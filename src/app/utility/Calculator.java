package app.utility;

import facility.Station;
import facility.Train;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utility.TRADate;

public abstract class Calculator {

	public static Calculator getInstance(boolean local, boolean oneDay, TRADate firstDate) {
		return getInstance(null, local, oneDay, firstDate);
	}

	public static Calculator getInstance(Station station, boolean local, boolean oneDay, TRADate firstDate) {
		if (firstDate.before(TRADate.now())) {
			throw new IllegalArgumentException();
		}

		//Determine calculator
		Calculator cal;
		if (station == null) {
			cal = oneDay ? new DayCalculator() : new WeekCalculator();
		}
		else {
			cal = oneDay ? new CustomDayCalculator(station) : new CustomWeekCalculator(station);
		}

		//Set calculator
		int nThread = Runtime.getRuntime().availableProcessors();
		List<Thread> threadList = new ArrayList<>(nThread);
		for (int i = 0 ; i < nThread ; i++) {
			threadList.add(new Thread(cal.run));
		}
		cal.threads = threadList;
		cal.date = firstDate;
		cal.counter = new Counter(local ? 8999 /*TODO what is max??*/ : 899);

		return cal;
	}

	private Runnable run = () -> run();
	protected Searcher searcher = new Searcher();
	protected Counter counter;
	protected List<Thread> threads;
	protected volatile int no;
	protected TRADate date;

	protected abstract int increment(Searcher es, Train train) throws IOException;

	protected abstract void run();

	public abstract Object calculate();

	public void interrupt() {
		searcher.interrupted = true;
	}

	public int progress() {
		return counter.getProgress();
	}
}
