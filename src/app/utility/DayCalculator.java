package app.utility;

import facility.Train;

public class DayCalculator extends GeneralCalculator {

	DayCalculator() {
	}

	@Override
	protected int increment(Searcher es, Train train) {
		return 1;
	}
}
