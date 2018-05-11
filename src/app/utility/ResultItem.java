package app.utility;

import facility.Station;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultItem implements Comparable<ResultItem> {

	public static final Comparator SORT_BY_STOPPING_COUNT = (o1, o2) -> {
		int n1 = o1.getStoppingCount();
		int n2 = o2.getStoppingCount();
		if (n1 == n2) {
			return o2.getSum() - o1.getSum();
		}
		return n2 - n1;
	};
	public static final Comparator SORT_BY_SUM = (o1, o2) -> {
		int sum1 = o1.getSum();
		int sum2 = o2.getSum();
		if (sum1 == sum2) {
			return o2.nStop.get() - o1.nStop.get();
		}
		return sum2 - sum1;
	};
	public static final Comparator SORT_BY_STOPPING_RATE = (o1, o2) -> {
		double r1 = o1.getStoppingRate();
		double r2 = o2.getStoppingRate();
		if (r1 == r2) {
			return o2.getSum() - o1.getSum();
		}
		return r1 > r2 ? -1 : 1;
	};

	@FunctionalInterface
	interface Comparator extends java.util.Comparator<Map.Entry<Station, ResultItem>> {

		@Override
		default int compare(Map.Entry<Station, ResultItem> o1, Map.Entry<Station, ResultItem> o2) {
			return compare(o1.getValue(), o2.getValue());
		}

		int compare(ResultItem o1, ResultItem o2);
	}

	AtomicInteger nPass = new AtomicInteger(0);
	AtomicInteger nStop = new AtomicInteger(0);

	public ResultItem() {
	}

	@Override
	public int compareTo(ResultItem o) {
		return SORT_BY_STOPPING_RATE.compare(this, o);
	}

	public int getPassingCount() {
		return nPass.get();
	}

	public int getStoppingCount() {
		return nStop.get();
	}

	public double getStoppingRate() {
		int sum = nPass.get() + nStop.get();
		if (sum == 0) {
			return 0.0;
		}
		return (double) nStop.get() / sum;
	}

	public int getSum() {
		return nPass.get() + nStop.get();
	}

	@Override
	public String toString() {
		return "stop=" + nStop + ", sum=" + getSum() + ", rate=" + String.format("%.2f", getStoppingRate() * 100) + "%\n";
	}
}