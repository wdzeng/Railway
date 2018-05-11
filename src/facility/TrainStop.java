package facility;

import java.util.Objects;
import utility.TRATime;

/**
 * A <code>Train</code> object contains a list of <code>TrainStop</code> objects. This class contains a
 * train's stopping station and its arrival time and departure time.
 * @author Parabola
 */
public class TrainStop implements Comparable<TrainStop> {

	static class Builder {

		TRATime arr;
		TRATime dep;
		Station stt;

		TrainStop build() {
			return new TrainStop(stt, arr, dep);
		}
	}

	TRATime arv;
	TRATime dep;
	Station stt;

	/**
	 * COnstructs a train stop object.
	 * @param station   station
	 * @param arrival   arrival time
	 * @param departure departure time
	 */
	public TrainStop(Station station, TRATime arrival, TRATime departure) {
		this.stt = Objects.requireNonNull(station);
		this.arv = Objects.requireNonNull(arrival);
		this.dep = Objects.requireNonNull(departure);
	}

	@Override
	public int compareTo(TrainStop o) {
		return dep.compareTo(o.dep);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TrainStop) {
			TrainStop ots = (TrainStop) o;
			return arv.equals(ots.arv) && dep.equals(ots.dep) && stt.equals(ots.stt);
		}
		return false;
	}

	/**
	 * Gets the arrival time.
	 */
	public TRATime getArrival() {
		return arv;
	}

	/**
	 * Gets the departure time.
	 */
	public TRATime getDeparture() {
		return dep;
	}

	/**
	 * Gets the station.
	 */
	public Station getStation() {
		return stt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arv, dep, stt);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + stt.getName() + ", " + dep + " -> " + arv + "]";
	}
}
