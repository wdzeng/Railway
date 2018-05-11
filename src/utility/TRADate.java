package utility;

import java.util.Calendar;

/**
 * This class is used to deal with the date about TRA. Once an instance is created, its fields cannot be
 * changed, like <code>String</code>. This class contains only one field which class is
 * <code>Calendar</code> but invisible.
 * @author Parabola
 * @see TRATime
 */
public class TRADate implements Cloneable {

	private static final int[] DAYS_OF_MONTH = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

	private static boolean isValid(int year, int month, int dayOfMonth) {
		if (month < 1 || month > 12 || year < 0) {
			return false;
		}
		if (year == 0) {
			return dayOfMonth <= DAYS_OF_MONTH[month - 1];
		}
		if (month == 2) {
			return dayOfMonth <= (year % 4 == 0 ? 29 : 28);
		}
		return dayOfMonth <= DAYS_OF_MONTH[month - 1];
	}

	private static Calendar reset(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/**
	 * <p> Try if a string description can be used to verify an <code>TRADate</code> instance. A good
	 * description should only be formatted as <b>MDD</b>, <b>MMDD</b> or <b>YYYYMMDD</b>. For
	 * example, a
	 * description "312" indicates March 12<sup>th</sup>, "1225" indicates December 25 <sup>th</sup>,
	 * and
	 * "20071014" indicates October 14<sup>th</sup>, 2007. </p> <p> Notice that improper date such as
	 * "0231" or "1232" returns false either. If parameter <code>year</code> exists, it should be
	 * positive. </p>
	 * @param date a date description.
	 * @return true if the description is valid.
	 */
	public static boolean isValid(String date) {
		if (date == null) {
			return false;
		}
		if (date.length() == 8) {
			String y = date.substring(0, 4);
			String m = date.substring(4, 6);
			String d = date.substring(6);
			if (y.matches("[0-9]{4}") && m.matches("[0-9]{2}") && d.matches("[0-9]{2}")) {
				return isValid(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d));
			}
		}
		else if (date.length() == 4) {
			String m = date.substring(0, 2);
			String d = date.substring(2);
			if (m.matches("[0-9]{2}") && d.matches("[0-9]{2}")) {
				return isValid(0, Integer.parseInt(m), Integer.parseInt(d));
			}
		}
		else if (date.length() == 3) {
			String m = date.substring(0, 1);
			String d = date.substring(1);
			if (m.matches("[0-9]") && d.matches("[0-9]{2}")) {
				return isValid(0, Integer.parseInt(m), Integer.parseInt(d));
			}
		}
		return false;
	}

	/**
	 * <p> Try if a string description can be used to verify an <code>TRADate</code> instance. If
	 * invalid, a <code>IllegalArgumentException</code> is thrown. </p> <p> Calling <code>{@link
	 * #isValid(String)}</code> to test the validity of the description is recommended. </p>
	 * @param date a date description.
	 * @return a new instance if description is valid.
	 * @throws IllegalArgumentException if the description is invalid.
	 */
	public static TRADate getInstance(String date) {
		if (date == null) {
			throw new IllegalArgumentException("Null");
		}
		if (date.length() == 8) {
			String y = date.substring(0, 4);
			String m = date.substring(4, 6);
			String d = date.substring(6);
			if (y.matches("[0-9]{4}") && m.matches("[0-9]{2}") && d.matches("[0-9]{2}")) {
				return new TRADate(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d));
			}
			throw new IllegalArgumentException("Invalid date: " + date);
		}
		if (date.length() == 4) {
			String m = date.substring(0, 2);
			String d = date.substring(2);
			if (m.matches("[0-9]{2}") && d.matches("[0-9]{2}")) {
				return new TRADate(Calendar.getInstance().get(Calendar.YEAR), Integer.parseInt(m), Integer.parseInt(d));
			}
			throw new IllegalArgumentException("Invalid date: " + date);
		}
		if (date.length() == 3) {
			String m = date.substring(0, 1);
			String d = date.substring(1);
			if (m.matches("[0-9]") && d.matches("[0-9]{2}")) {
				return new TRADate(Calendar.getInstance().get(Calendar.YEAR), Integer.parseInt(m), Integer.parseInt(d));
			}
			throw new IllegalArgumentException("Invalid date: " + date);
		}
		throw new IllegalArgumentException("Invalid date: " + date);
	}

	/**
	 * @return a new instance which time is same as now.
	 */
	public static TRADate now() {
		return new TRADate();
	}

	private Calendar cal;

	private TRADate() {
		cal = reset(Calendar.getInstance());
	}

	/**
	 * Get a new instance by month and date.
	 * @param month      argument month.
	 * @param dayOfMonth argument date.
	 * @throws IllegalArgumentException if this date is invalid;
	 */
	public TRADate(int month, int dayOfMonth) {
		this(Calendar.getInstance().get(Calendar.YEAR), month, dayOfMonth);
	}

	/**
	 * Get a new instance by month and date.
	 * @param year       argument year.
	 * @param month      argument month.
	 * @param dayOfMonth argument date.
	 * @throws IllegalArgumentException if this date is invalid
	 */
	@SuppressWarnings("MagicConstant")
	public TRADate(int year, int month, int dayOfMonth) {
		if (!isValid(year, month, dayOfMonth)) {
			throw new IllegalArgumentException("Invalid date: " + year + ", " + month + ", " + dayOfMonth);
		}
		cal = reset(Calendar.getInstance());
		cal.set(year, month - 1, dayOfMonth);
	}

	/**
	 * Test if this object represents a date after another object.
	 * @param com an object to be compared.
	 * @return true if this object represents an later date.
	 */
	public boolean after(TRADate com) {
		return cal.after(com.cal);
	}

	/**
	 * Test if this object represents a date earlier than another object.
	 * @param com an object to be compared.
	 * @return true if that object represents an earlier date.
	 */
	public boolean before(TRADate com) {
		return cal.before(com.cal);
	}

	@Override
	public TRADate clone() {
		TRADate newDate;
		try {
			newDate = (TRADate) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e); // Never happen.
		}
		newDate.cal = (Calendar) this.cal.clone();
		return newDate;
	}

	/**
	 * Test if this object is equaled to another object. Only if that object is an instance of
	 * <code>TRADate</code>, it returns true when their <code>Calendar</code> field is equaled.
	 * @param obj an object to be tested.
	 * @return true if equaled.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TRADate) {
			return this.cal.equals(((TRADate) obj).cal);
		}
		return false;
	}

	/**
	 * @return the day of month.
	 */
	public int getDate() {
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @return the day of Week. The result is same as <code>Calendar</code>.
	 */
	public int getDay() {
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * @return the month.
	 */
	public int getMonth() {
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * @return the year.
	 */
	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	/**
	 * @return a string representing the date. The default format is MM/DD.
	 */
	public String monthAndDate() {
		return monthAndDate("/");
	}

	/**
	 * @param separatedSymbol a symbol used to separate arguments. If null, it has the same effect as an
	 *                        empty string.
	 * @return a string representing the date. The format is MM/DD.
	 */
	public String monthAndDate(String separatedSymbol) {
		if (separatedSymbol == null) {
			separatedSymbol = "";
		}
		return String.format("%02d", getMonth()) + separatedSymbol + String.format("%02d", getDate());
	}

	/**
	 * @return a new instance representing seven days after.
	 */
	public TRADate nextWeek() {
		TRADate date = clone();
		date.cal.add(Calendar.DAY_OF_MONTH, 7);
		return date;
	}

	/**
	 * Calculate the count of days startTime this object to another object.
	 * @param com an object to be compared.
	 * @return a number represents the count of days startTime this object to another object. Positive if this
	 * object is later.
	 */
	public int pass(TRADate com) {
		long m = cal.getTimeInMillis() - com.cal.getTimeInMillis();
		m /= (24 * 60 * 60 * 1000);
		return (int) m;
	}

	/**
	 * @return a <code>Calendar</code> object.
	 */
	public Calendar toCalendar() {
		return (Calendar) cal.clone();
	}

	/**
	 * @param dayOfWeek argument day of week.
	 * @return a new instance the day of week is modified.
	 */
	public TRADate toDay(int dayOfWeek) {
		TRADate date = clone();
		int weekOfYear = date.cal.get(Calendar.WEEK_OF_YEAR);
		date.cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		date.cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		return date;
	}

	/**
	 * @return a string representing the date. The default format is YYYY/MM/DD.
	 */
	@Override
	public String toString() {
		return toString("/");
	}

	/**
	 * @param separatedSymbol a symbol used to separate arguments. If null, it has the same effect as an
	 *                        empty string.
	 * @return a string representing the date. The format is YYYY/MM/DD.
	 */
	public String toString(String separatedSymbol) {
		if (separatedSymbol == null) {
			separatedSymbol = "";
		}
		return String.format("%04d", getYear()) + separatedSymbol + String.format("%02d", getMonth()) + separatedSymbol + String.format("%02d", getDate());
	}

	/**
	 * @return a new instance which represents the next day.
	 */
	public TRADate tomorrow() {
		TRADate d = clone();
		d.cal.add(Calendar.DAY_OF_MONTH, 1);
		return d;
	}

	/**
	 * @return a new instance which represents the last day.
	 */
	public TRADate yesterday() {
		TRADate d = clone();
		d.cal.add(Calendar.DAY_OF_MONTH, -1);
		return d;
	}
}
