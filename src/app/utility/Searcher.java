package app.utility;

import facility.Train;
import java.io.IOException;
import utility.TRADate;

/**
 * When searching a facility.timetable or a train, an <code>IOException</code> is thrown
 * occasionally. This class will conduct the searching again and again
 * automatically if an <code>IOException</code> occurs in a few times. If an
 * <code>IOException</code> happens over a number of times, it throws an
 * <code>IOException</code> and stop searching.
 * @author Parabola
 */
public class Searcher {

	volatile boolean interrupted = false;

	public Searcher() {
	}

	public boolean availableOn(Train train, TRADate date) throws IOException {
		return train.isAvailable(date);
	}

	public int nDrivingDay(Train train, TRADate date) throws IOException {
		boolean[] bools = train.getDrivingDaysOnSuchWeek(date);
		int t = 0;
		for (boolean boo : bools) {
			if (boo) {
				t++;
			}
		}
		return t;
	}

	public Train search(int trainNo) throws IOException {
		return search(trainNo, null);
	}

	public Train search(int trainNo, TRADate date) throws IOException {
		return Train.search(Integer.toString(trainNo), date);
	}
}
