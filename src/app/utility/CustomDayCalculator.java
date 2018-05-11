package app.utility;

import facility.Station;
import facility.Train;

public class CustomDayCalculator extends CustomCalculator {

	CustomDayCalculator(Station station) {
		super(station);
	}

	@Override
	protected int increment(Searcher es, Train train) {
		return 1;
	}
}
