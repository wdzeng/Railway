package app.utility;

import facility.Station;
import facility.Train;
import java.io.IOException;

public class CustomWeekCalculator extends CustomCalculator {

	CustomWeekCalculator(Station station) {
		super(station);
	}

	@Override
	protected int increment(Searcher es, Train train) throws IOException {
		return es.nDrivingDay(train, date);
	}

}
