package facility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utility.TRADate;
import utility.TRATime;
import utility.WebSocket;

/**
 * This class is a time table which contains all the <code>Train</code> objects on the date in a time range. This class is composed of <code>TimeTableItem</code> objects.<p>
 * This class does not provide constructor; hence one must call <tt>search</tt> method to get an instance. On doing so, a socket connects to TRA's website and downloads the
 * time table. {@link IOException} may occurs.
 * @author Parabola
 * @see TimeTableItem
 */
public class TimeTable {

	private static final Pattern CAR = Pattern.compile("<td class=\"SearchResult_TrainType\"><div " + "align=\"center\"><span id=\"classname\">" + ".*?</span></div></td>");
	private static final Pattern NO = Pattern.compile("language=\">.*?</a></td>");
	private static final Pattern ROU = Pattern.compile("<td align=\"center\" style=\"width:25px;\">" + ".*?</td>");
	private static final Pattern TIME = Pattern.compile("<td class=\"SearchResult_Time\" " + "align=\"center\" style=\"width:65px;\">.*?</td>");
	private static final Pattern PCE = Pattern.compile("<span id=\"Label1\">.*?</span>");
	private static final TRATime T0 = new TRATime(0, 0), T1 = new TRATime(23, 59);

	private static TimeTable getInstance(Station from, Station to, TRADate date, TRATime fromTime, TRATime toTime, String html) throws IOException {

		try {
			if (html.contains("本次查詢無資料")) {
				return null;
			}

			Matcher matcher = Pattern.compile("<tbody>.*?</tbody>").matcher(html);
			matcher.find();

			final String tbodyHtml = matcher.group();

			List<String> trs = new ArrayList<>();
			matcher = Pattern.compile("<tr.*?>.*?</tr>").matcher(tbodyHtml);
			while (matcher.find()) {
				trs.add(matcher.group());
			}
			List<TimeTableItem> items = new ArrayList<>();
			String trainNo;
			int price;
			String route;
			TRATime arr, dep;
			boolean bookable;
			for (String tr : trs) {
				matcher = CAR.matcher(tr);
				matcher.find();
				TrainClass type = TrainClass.getInstance(matcher.group()
				                                                .replace("<td " + "class=\"SearchResult_TrainType\"><div align=\"center\"><span id=\"class" + "name\">", "")
				                                                .replace("</span></div></td>", ""));


				matcher = NO.matcher(tr);
				matcher.find();
				trainNo = matcher.group().replace("language=\">", "").replace("</a></td>", "");

				matcher = ROU.matcher(tr);
				matcher.find();
				route = matcher.group().replace("<td align=\"center\" style=\"width:25px;" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "\">", "").replace("</td>", "");

				matcher = TIME.matcher(tr);
				matcher.find();

				dep = TRATime.format(matcher.group().replace("<td class=\"SearchResult_Time\" " + "align=\"center\" style=\"width:65px;\">", "").replace("</td>", ""), ":");

				matcher.find();
				arr = TRATime.format(matcher.group().replace("<td class=\"SearchResult_Time\" " + "align=\"center\" style=\"width:65px;\">", "").replace("</td>", ""), ":");

				matcher = PCE.matcher(tr);
				matcher.find();

				price = Integer.parseInt(matcher.group().replace("<span id=\"Label1\">$ ", "").replace("</span>", ""));

				bookable = tr.contains("images/orderticket.jpg");

				items.add(new TimeTableItem(trainNo, type, dep, arr, route, price, bookable));
			}

			return new TimeTable(from, to, date, fromTime, toTime, items);
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Searches trains by given info and gets a time table.
	 * @param origin      origin station
	 * @param destination destination station
	 * @param date        date of the travel
	 * @param fromTime    the earliest departure time of the time table
	 * @param toTime      the latest departure time of the time table
	 * @return a time table
	 * @throws IOException if an I/O error occurs;
	 */
	public static TimeTable search(Station origin, Station destination, TRADate date, TRATime fromTime, TRATime toTime) throws IOException {

		if (origin == null || destination == null) {
			throw new NullPointerException();
		}
		if (date == null) {
			date = TRADate.now();
		}
		else if (date.before(TRADate.now())) {
			throw new IllegalArgumentException();
		}
		if (fromTime == null) {
			fromTime = T0;
		}
		if (toTime == null) {
			toTime = T1;
		}

		URL url = new URL(
				"http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx?searchtype=0&trainclass=2&fromtime=" + fromTime.toString("") + "&totime=" + toTime.toString("")
						+ "&searchdate=" + date.toString("/") + "&fromstation=" + origin.getIdSearch() + "&tostation=" + destination.getIdSearch());

		try {
			return getInstance(origin, destination, date, fromTime, toTime, WebSocket.get(url).collapse());
		}
		catch (IOException e) {
			System.err.println("URL=" + url);
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Searches trains by given info and gets a time table.
	 * @param origin      origin station
	 * @param destination destination station
	 * @param date        date of the travel
	 * @return a time table
	 * @throws IOException if an I/O error occurs;
	 */
	public static TimeTable search(Station origin, Station destination, TRADate date) throws IOException {
		return search(origin, destination, date, T0, T1);
	}

	/**
	 * Searches trains today by given info and gets a time table.
	 * @param origin      origin station
	 * @param destination destination station
	 * @return a time table
	 * @throws IOException if an I/O error occurs;
	 */
	public static TimeTable search(Station origin, Station destination) throws IOException {
		return search(origin, destination, TRADate.now());
	}

	private final Station origin;
	private final Station dest;
	private final TRADate date;
	private final TRATime fromTime;
	private final TRATime toTime;
	private final List<TimeTableItem> items;

	TimeTable(Station origin, Station dest, TRADate date, TRATime fromTime, TRATime arv, List<TimeTableItem> items) {
		if (origin == null || dest == null || date == null || fromTime == null || arv == null) {
			throw new NullPointerException();
		}
		if (items.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.origin = origin;
		this.dest = dest;
		this.date = date;
		this.fromTime = fromTime;
		this.toTime = arv;
		this.items = items;
	}

	/**
	 * Gets a description of this time table.
	 */
	public String description() {
		StringBuilder sb = new StringBuilder(50 + items.size() * 25);
		sb.append(date.toString("-"))
		  .append(" ")
		  .append(fromTime.toString(":"))
		  .append("~")
		  .append(toTime.toString(":"))
		  .append(" 【")
		  .append(origin.getName())
		  .append(" -> ")
		  .append(dest.getName())
		  .append("】\n");

		for (TimeTableItem sttr : items) {
			sb.append(sttr.description()).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Queries if this time table if equaled to another. If both time tables contains same results (in the same orders), they are thought of to be same. Neither date nor time
	 * range is put into consideration.
	 * @param o another object (time table)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof TimeTable) {
			return items.equals(((TimeTable) o).items);
		}
		return false;
	}

	/**
	 * Gets the date of this time table.
	 */
	public TRADate getDate() {
		return date;
	}

	/**
	 * Gets the beginning of time searching scope of this time table.
	 */
	public TRATime getDeparture() {
		return fromTime;
	}

	/**
	 * Gets the destination station of this timetable.
	 */
	public Station getDestination() {
		return dest;
	}

	/**
	 * Gets the item that matches to a given train. If such train does not appear in this table, null is returned.
	 * @param trainNo no of train
	 * @return the item that matches to a given train; if such train does not appear in this table, null is returned.
	 */
	public TimeTableItem getItem(String trainNo) {
		return items.stream().filter(item -> item.trainNo.equals(trainNo)).findFirst().orElse(null);
	}

	/**
	 * Gets all the items in this time table.
	 */
	public List<TimeTableItem> getItems() {
		return new ArrayList<>(items);
	}

	/**
	 * Gets the end of time searching scope of this time table.
	 */
	public TRATime getLatestDeparture() {
		return toTime;
	}

	/**
	 * Gets the origin station of this time table.
	 */
	public Station getOrigin() {
		return origin;
	}

	@Override
	public int hashCode() {
		return items.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + "\n --------------------------------------------------- \n" + description() + " --------------------------------------------------- \n";
	}
}
