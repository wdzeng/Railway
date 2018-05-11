package facility;

import java.util.Objects;
import utility.TRATime;

/**
 * Unit of <code>TimeTable</code>.
 * @author Parabola
 * @see TimeTable
 */
public class TimeTableItem implements Comparable<TimeTableItem> {

	static class Builder {

		public TRATime arrTime;
		public boolean bookable;
		public TRATime depTime;
		public int price = -1;
		public String route;
		public String trainNo;
		public TrainClass trainClass;

		public Builder() {
		}

		public Builder(String trainNo) {
			this.trainNo = Objects.requireNonNull(trainNo);
		}

		public TimeTableItem build() {
			return new TimeTableItem(trainNo, trainClass, depTime, arrTime, route, price, bookable);
		}
	}

	TRATime arv;
	boolean bookable;
	TRATime dep;
	int price;
	String route;
	String trainNo;
	TrainClass trainClass;

	TimeTableItem(String trainNo, TRATime dep, TRATime arv) {
		this(trainNo, null, dep, arv, null, -1, false);
	}

	TimeTableItem(String trainNo, TrainClass type, TRATime dep, TRATime arv, String route, int price) {
		this(trainNo, type, dep, arv, route, price, false);
	}

	TimeTableItem(String trainNo, TrainClass type, TRATime dep, TRATime arv, String route, int price, boolean bookable) {
		if (price < 0) {
			throw new IllegalArgumentException();
		}
		this.trainNo = Objects.requireNonNull(trainNo);
		this.trainClass = Objects.requireNonNull(type);
		this.route = route;
		this.arv = Objects.requireNonNull(arv);
		this.dep = Objects.requireNonNull(dep);
		this.price = price;
		this.bookable = bookable;
	}

	@Override
	public int compareTo(TimeTableItem o) {
		return dep.compareTo(o.dep);
	}

	/**
	 * Gets a description.
	 */
	public String description() {
		return String.format("%-3s\t%4s\t%1s\t%5s\t%5s\t%4d\t%s",
		                     trainClass.getName(),
		                     trainNo,
		                     route,
		                     dep.toString(":"),
		                     arv.toString(":"),
		                     price,
		                     bookable ? "可訂票" : "不可訂票");
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
	 * Gets the price of this travel.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Gets the route the train is via.
	 */
	public String getRoute() {
		return route;
	}

	/**
	 * Gets the type of the train.
	 */
	public TrainClass getTrainClass() {
		return trainClass;
	}

	/**
	 * Queries if the train ticket can be booked (at the time this object is constructed).
	 */
	public boolean isBookable() {
		return bookable;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + description() + "]";
	}

	/**
	 * Gets the no of the train.
	 */
	public String getTrainNo() {
		return trainNo;
	}
}
