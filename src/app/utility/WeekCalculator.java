package app.utility;

import facility.Train;
import java.io.IOException;

public class WeekCalculator extends GeneralCalculator {

	WeekCalculator() {
	}

	@Override
	protected int increment(Searcher es, Train train) throws IOException {
		return es.nDrivingDay(train, date);
	}
}
