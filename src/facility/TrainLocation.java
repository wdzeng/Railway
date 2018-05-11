package facility;

import java.util.Objects;

/**
 * This class represents a location of a train. A train may be between two stations or be at a
 * station's platform. In addition, a train may have not started or have arrived to the terminal.<p>
 * However, be aware that station pair[A, B] is not equaled to pair[B, A]. And, Station A and B might not be adjacent.
 * @author Parabola
 */
public class TrainLocation {

	/**
	 * The location of a train is unknown.
	 */
	public static final TrainLocation UNKNOWN = new TrainLocation();
	/**
	 * A train has leaved the terminal station and finished its job.
	 */
	public static final TrainLocation AT_TERMINAL = new TrainLocation();
	/**
	 * A train has not started yet.
	 */
	public static final TrainLocation NOT_YET_STARTED = new TrainLocation();

	/**
	 * A station.
	 */
	public final Station a, b;

	private TrainLocation() {
		this.a = null;
		this.b = null;
	}

	/**
	 * Constructs a location info representing that a train is between station A and B.
	 */
	public TrainLocation(Station a, Station b) {
		this.a = Objects.requireNonNull(a);
		this.b = Objects.requireNonNull(b);
	}

	public String description() {
		if (this == NOT_YET_STARTED) {
			return "尚未發車";
		}
		if (this == AT_TERMINAL) {
			return "已抵終點";
		}
		if (this == UNKNOWN) {
			return "未知";
		}
		if (a == b) {
			return "本車目前位在【" + a + "】";
		}
		return "本車目前位在【" + a + "】至【" + b + "】區間";
	}

	@Override
	public boolean equals(Object obj) {

		//Check const
		if (this == obj) {
			return true;
		}

		if (obj instanceof TrainLocation) {
			TrainLocation tl = (TrainLocation) obj;
			return a == tl.a && b == tl.b || a == tl.b && b == tl.a;
		}
		return false;
	}

	/**
	 * If this station is between station A and B, gets A or else null.
	 * @return station A
	 */
	public Station getStationA() {
		return a;
	}

	/**
	 * If this station is between station A and B, gets B or else null.
	 * @return station B
	 */
	public Station getStationB() {
		return b;
	}

	@Override
	public int hashCode() {
		if (this == UNKNOWN || this == NOT_YET_STARTED || this == AT_TERMINAL) {
			return System.identityHashCode(this);
		}
		return a.getName().hashCode() + 17 * b.getName().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + "\n ------------------------ \n" + description() + "\n ------------------------ \n\n";
	}
}
