package utility;

import java.util.Calendar;

/**
 * This is a class used to represent a time. When searching facility.timetable or booking
 * tickets, this class performs good functions. This class contains an only
 * <code>Calendar</code> field.
 * @author Parabola\
 * @see TRADate
 */
public class TRATime implements Comparable<TRATime> {

	private static boolean isValid(int hour, int minute) {
		return !(hour < 0 || hour > 23 || minute < 0 || minute > 59);
	}

	/**
	 * Test if a description is able to create a new instance.
	 * @param time           a description to be tested. The description should be
	 *                       formatted as <b>HMM</b> or <b>HHMM</b>. Such as "1230" means
	 *                       twelve thirty. The description is allowed to contain a symbol
	 *                       to separate argument <code>hour</code> and <code> month</code>
	 *                       , such as "14:40" or "2:15".
	 * @param separateSymbol symbol to separate <code>hour</code> and <code>minute</code>.
	 *                       If no separate symbol, put null or an empty string.
	 * @return true if valid. Put the range of arguments into consideration,
	 * like "25:01" is invalid.
	 */
	public static boolean isValid(String time, String separateSymbol) {
		if (time == null) {
			return false;
		}
		if (separateSymbol == null || separateSymbol.length() == 0) {
			return isValid(time);
		}
		String[] sps = time.split(separateSymbol);
		if (sps.length == 2) {
			return isValid(sps[0] + sps[1]);
		}
		return false;
	}

	/**
	 * Test if a description is able to create a new instance.
	 * @param time a description to be tested. The description should be
	 *             formatted as <b>HMM</b> or <b>HHMM</b>. Such as "1230" means
	 *             twelve thirty.
	 * @return true if valid. Put the range of arguments into consideration,
	 * like "25:01" or "12:60" is invalid.
	 */
	public static boolean isValid(String time) {
		if (time == null) {
			return false;
		}
		final String h, m;
		if (time.length() == 3) { // hmm
			h = time.substring(0, 1);
			if (!h.matches("[0-9]")) {
				return false;
			}
			m = time.substring(1);
			if (!m.matches("[0-9]{2}")) {
				return false;
			}
		}
		else if (time.length() == 4) { // hhmm
			h = time.substring(0, 5);
			if (!h.matches("[0-9]{2}")) {
				return false;
			}
			m = time.substring(2);
			if (!m.matches("[0-9]{2}")) {
				return false;
			}
		}
		else {
			return false;
		}
		return isValid(Integer.parseInt(h), Integer.parseInt(m));
	}

	/**
	 * Get a new instance with the given description. If the description is
	 * invalid, a <code>IllegalArgumentException</code> is thrown. Calling
	 * {@link #isValid(String)} or {@link #isValid(String, String)} to test if
	 * the description is valid.
	 * @param time           a description. The description should be formatted as
	 *                       <b>HMM</b> or <b>HHMM</b>. Such as "1230" means twelve thirty.
	 *                       The description is allowed to contain a symbol to separate
	 *                       argument <code>hour</code> and <code> month</code> , such as
	 *                       "14:40" or "2:15".
	 * @param separateSymbol symbol to separate <code>hour</code> and <code>minute</code>.
	 *                       If no separate symbol, put null or an empty string.
	 * @return a new instance.
	 * if <code>time</code> is null or the description is invalid.
	 */
	public static TRATime format(String time, String separateSymbol) {
		if (separateSymbol == null || separateSymbol.length() == 0) {
			return format(time);
		}
		String[] sps = time.split(separateSymbol);
		if (sps.length == 2) {
			return format(sps[0] + sps[1]);
		}
		throw new IllegalArgumentException("Invalid time: " + time);
	}

	/**
	 * Get a new instance with the given description. If the description is
	 * invalid, a <code>IllegalArgumentException</code> is thrown. Calling
	 * {@link #isValid(String)} or {@link #isValid(String, String)} to test if
	 * the description is valid.
	 * @param time a description. The description should be formatted as
	 *             <b>HMM</b> or <b>HHMM</b>. Such as "1230" means twelve thirty.
	 * @return a new instance.
	 * if <code>time</code> is null or the description is invalid.
	 */
	public static TRATime format(String time) {
		final String h, m;
		if (time.length() == 3) { // hmm
			h = time.substring(0, 1);
			if (!h.matches("[0-9]")) {
				throw new IllegalArgumentException("Invalid time: " + time);
			}
			m = time.substring(1);
			if (!m.matches("[0-9]{2}")) {
				throw new IllegalArgumentException("Invalid time: " + time);
			}
		}
		else if (time.length() == 4) { // hhmm
			h = time.substring(0, 2);
			if (!h.matches("[0-9]{2}")) {
				throw new IllegalArgumentException("Invalid time: " + time);
			}
			m = time.substring(2);
			if (!m.matches("[0-9]{2}")) {
				throw new IllegalArgumentException("Invalid time: " + time);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid time: " + time);
		}
		return new TRATime(Integer.parseInt(h), Integer.parseInt(m));
	}

	/**
	 * @return a new instance which represents now.
	 */
	public static TRATime now() {
		Calendar cal = Calendar.getInstance();
		return new TRATime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}

	private final Calendar cal;

	/**
	 * @param hour   argument hour.
	 * @param minute argument minute.
	 *               if hour or minute is out of range.
	 */
	public TRATime(int hour, int minute) {
		if (!isValid(hour, minute)) {
			throw new IllegalArgumentException("Invalid arguments: hour=" + hour + ", minute=" + minute + ".");
		}
		cal = Calendar.getInstance();
		cal.set(0, Calendar.JANUARY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
	}

	/**
	 * Get a instance startTime a <code>Calendar</code> object.
	 * @param cal a <code>Calendar</code> object.
	 */
	public TRATime(Calendar cal) {
		cal = (Calendar) cal.clone();
		cal.set(0, Calendar.JANUARY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.cal = cal;
	}

	/**
	 * @param com an object to be compared.
	 * @return true if the object representing a time later than another object.
	 */
	public boolean after(TRATime com) {
		return cal.after(com.cal);
	}

	/**
	 * @param com an object to be compared.
	 * @return true if the object representing a time earlier than another
	 * object.
	 */
	public boolean before(TRATime com) {
		return cal.before(com.cal);
	}

	@Override
	public int compareTo(TRATime o) {
		return cal.compareTo(o.cal);
	}

	/**
	 * @return hour.
	 */
	public int getHour() {
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * @return minute.
	 */
	public int getMinute() {
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * @return a <code>Calendar</code> object representing the time.
	 */
	public Calendar toCalendar() {
		return (Calendar) cal.clone();
	}

	/**
	 * Get a string describing the time with given separate symbol. If
	 * <code>separteSymbol</code> is null, no symbol is contained.
	 */
	public String toString(String separateSymbol) {
		if (separateSymbol == null) {
			separateSymbol = "";
		}
		return String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + separateSymbol + String.format("%02d", cal.get(Calendar.MINUTE));
	}

	/**
	 * Get a string describing the time. The default format is "HH:MM".
	 */
	@Override
	public String toString() {
		return toString(":");
	}
}
