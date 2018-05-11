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
 * A train.
 * @author Parabola
 */
public class Train {

	private static class Builder {

		private static List<TrainStop> analyze(String tableHtml) {
			Pattern pattern = Pattern.compile("TRSearchResult\\.push\\('.*?'\\);");
			Matcher matcher = pattern.matcher(tableHtml);
			List<TrainStop> list = new ArrayList<>();

			int count = 0;
			String value;
			TrainStop.Builder builder = new TrainStop.Builder();
			while (matcher.find()) {
				if (count != 3) {
					value = matcher.group().replace("TRSearchResult.push('", "").replace("');", "");
					switch (count) {
					case 0:
						builder.stt = Station.getInstance(value);
						break;
					case 1:
						builder.arr = new TRATime(Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(2)));
						break;
					case 2:
						builder.dep = new TRATime(Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(2)));
						break;
					}
				}

				if (++count == 4) {
					count = 0;
					list.add(builder.build());
				}
			}
			return list;
		}

		private static int getDelay(String entireHtml) {
			Pattern pattern = Pattern.compile("<script>traindelaytime=.*?;</script>");
			Matcher matcher = pattern.matcher(entireHtml);

			if (matcher.find()) {
				String ds = matcher.group().replace("<script>traindelaytime=", "").replace(";</script>", "");
				if (ds.matches("[0-9]+")) {
					return Integer.parseInt(ds);
				}
			}

			// If info can be found
			return -1;
		}

		private static Train getInstance(final String trainNo, final TRADate date) throws IOException {

			URL urlToGetTable = new URL(
					"http://twtraffic.tra.gov.tw/twrail/mobile/TrainDetail.aspx?searchdate=" + (date == null ? TRADate.now().toString("/") : date.toString("/"))
							+ "&traincode=" + trainNo);
			String html = WebSocket.post(urlToGetTable).collapse();
			String tableHtml = getTableHtml(html);
			if (tableHtml == null) {
				return null;
			}

			List<TrainStop> stops = analyze(tableHtml);
			int sSize = stops.size();
			if (sSize < 2) {
				return null;
			}

			int fromIndex = 0;
			TrainStop arr, des;
			while (true) {
				arr = stops.get(fromIndex);
				des = stops.get(fromIndex + 1);
				if (arr.dep.before(des.arv)) {
					break;
				}
				fromIndex++;
			}

			TimeTable ttb = TimeTable.search(arr.stt, des.stt, date, arr.dep, des.arv);
			if (ttb == null) {
				return null;
			}
			TimeTableItem item = ttb.getItem(trainNo);
			if (item == null) {
				return null;
			}

			return new Train(trainNo, item.getTrainClass(), stops, getDelay(html));
		}

		private static String getTableHtml(String entireHtml) {

			// Check if train exists
			if (entireHtml.matches(".*?<script>.*?</script>.*?")) {
				Matcher matcher = Pattern.compile("<script>.*?</script>").matcher(entireHtml);
				matcher.find();
				return matcher.group().replace("<script>", "").replace("</script>", "");
			}

			return null;
		}
	}

	private static boolean[] commentToBoos(String cmt) {
		if (!cmt.matches(".*?逢週.*?駛.*")) {
			return new boolean[]{true, true, true, true, true, true, true};
		}

		Matcher matcher = Pattern.compile("逢週.*?駛").matcher(cmt);
		matcher.find();
		cmt = matcher.group();
		boolean[] boos = cmt.charAt(cmt.length() - 2) == '行' ? new boolean[7] : new boolean[]{true, true, true, true, true, true, true};
		cmt = cmt.substring(2, cmt.length() - 2);
		int from = -1, i, w;
		boolean flag = true;
		for (final char c : cmt.toCharArray()) {
			if (c == '、') {
				if (from != -1) {
					boos[from] = !boos[from];
					from = -1;
				}
				continue;
			}

			i = weekDayToInt(c);
			if (i == -1) {
				continue;
			}

			if (from == -1) {
				from = i;
				flag = true;
				continue;
			}

			w = from;
			for ( ; ; ) {
				boos[w] = !boos[w];
				if (w == i) {
					flag = false;
					break;
				}
				if (w == 6) {
					w = 0;
				}
				else {
					w++;
				}
			}
		}
		if (from != -1 && flag) {
			boos[from] = !boos[from];
		}
		return boos;
	}

	private static int weekDayToInt(char c) {
		switch (c) {
		case '一':
			return 1;
		case '二':
			return 2;
		case '三':
			return 3;
		case '四':
			return 4;
		case '五':
			return 5;
		case '六':
			return 6;
		case '日':
			return 0;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Gets today's train by given train no.
	 * @param trainNo train no
	 * @return today's train
	 */
	public static Train search(String trainNo) throws IOException {
		return Builder.getInstance(trainNo, TRADate.now());
	}

	/**
	 * Gets a train on some day by given train no.
	 * @param trainNo train no
	 * @param date    travel date
	 * @return a train
	 */
	public static Train search(String trainNo, TRADate date) throws IOException {
		return Builder.getInstance(trainNo, date);
	}



	private int delay;
	private String no;
	private TrainClass type;
	private List<TrainStop> stops;

	Train(String no, TrainClass type, List<TrainStop> stops, int delay) {
		this.no = no;
		this.type = type;
		this.stops = stops;
		this.delay = delay;
	}

	/**
	 * Gets a description of this train.
	 * @return a description
	 */
	public String description() {
		return description(TRATime.now());
	}

	/**
	 * Gets a description of this train assuming that the time is at argument <tt>time</tt>.
	 * @return a description
	 */
	public String description(TRATime time) {
		StringBuilder sb = new StringBuilder(stops.size() * 40).append("車次 [").append(no).append("]\n");
		if (type != null) {
			sb.append("車種: ").append(type.getName()).append("\n");
		}

		if (stops != null) {
			for (TrainStop arr : stops) {
				sb.append(String.format("%-4s\t%8s%8s\n", arr.stt.getName(), arr.arv.toString(":"), arr.dep.toString(":")));
			}
		}

		if (delay != -1) {
			if (delay == 0) {
				sb.append("準點\n");
			}
			else {
				sb.append("誤點: ").append(delay).append("分\n");
			}
		}

		if (stops != null && !(delay == -1 && time.after(stops.get(0).arv) && time.before(stops.get(stops.size() - 1).dep))) {
			sb.append(getLocation(time).description()).append("\n");
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Train) {
			return no.equals(((Train) o).no);
		}
		return false;
	}

	/**
	 * Gets the arrival time to a given station
	 * @param station station
	 * @return time
	 */
	public TrainStop getArrivalTime(Station station) {
		return stops.stream().filter(ts -> ts.stt == station).findFirst().orElse(null);
	}

	public String getComment() throws IOException {
		return getComment(TRADate.now());
	}

	/**
	 * Gets the comment ("在新左營跨日 etc") of this train.
	 * @param date the date when this train travels
	 * @return comment
	 */
	public String getComment(TRADate date) throws IOException {

		if (date.before(TRADate.now())) {
			throw new IllegalArgumentException();
		}

		String dt = stops.get(0).dep.toString("");
		URL url = new URL(
				"http://twtraffic.tra.gov.tw/twrail/SearchResult.aspx?searchtype=0&searchd" + "ate=" + date.toString("/") + "&fromstation=" + stops.get(0).stt.getIdSearch()
						+ "&tostation=" + stops.get(stops.size() - 1).stt.getIdSearch() + "&trainclass=2&fromtime=" + dt + "&totime=" + dt + "&redir=1");
		String html = WebSocket.get(url).collapse();
		Pattern pattern = Pattern.compile("<a id=\"TrainCodeHyperLink\".*?>" + no + "</a>" + "" + ".*?<span" + " id=\"Comment\">.*?</span>");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			return matcher.group().replaceAll("<a id=\"TrainCodeHyperLink\".*?>" + no + "</a>" + ".*?<span id=\"Comment\">", "").replace("</span>", "");
		}

		//If this train is not available on that day, search the next day.
		return getComment(date.tomorrow());
	}

	/**
	 * Gets the minute of delay of this train. If this train is not delayed, 0 is returned. -1 is returned on any other situation.
	 * @return delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Queries the days this train travels on this week. Index 0 represents Sunday, 1 Monday, and so on.
	 * @return an array info
	 */
	public boolean[] getDrivingDays() throws IOException {
		return getDrivingDaysOnSuchWeek(TRADate.now());
	}

	/**
	 * Queries the days this train travels on a given week. Index 0 represents Sunday, 1 Monday, and so on.
	 * @param dateOnWeek the week which contains this date
	 * @return an array info
	 */
	public boolean[] getDrivingDaysOnSuchWeek(TRADate dateOnWeek) throws IOException {
		return commentToBoos(getComment(dateOnWeek));
	}

	/**
	 * Gets the location of this train on a given time.
	 * @return location
	 */
	public TrainLocation getLocation(TRATime time) {

		int size = stops.size();
		if (delay == -1 && time.after(stops.get(0).arv) && time.before(stops.get(size - 1).dep)) {
			return TrainLocation.UNKNOWN;
		}

		if (time.before(stops.get(0).arv)) {
			return TrainLocation.NOT_YET_STARTED;
		}
		if (time.before(stops.get(0).dep)) {
			return new TrainLocation(stops.get(0).stt, stops.get(0).stt);
		}

		for (int i = 1 ; i < size ; i++) {
			if (time.before(stops.get(i).arv)) {
				return new TrainLocation(stops.get(i - 1).stt, stops.get(i).stt);
			}
			if (time.before(stops.get(i).dep)) {
				return new TrainLocation(stops.get(i).stt, stops.get(i).stt);
			}
		}
		return TrainLocation.AT_TERMINAL;
	}

	/**
	 * Gets the location of this train right now.
	 * @return location
	 */
	public TrainLocation getLocation() {
		return getLocation(TRATime.now());
	}

	/**
	 * Gets no of this train.
	 * @return no
	 */
	public String getNo() {
		return no;
	}

	/**
	 * Gets all the stops of this train (by order).
	 * @return stops
	 */
	public List<TrainStop> getStops() {
		return new ArrayList<>(stops);
	}

	/**
	 * Gets the type of this train.
	 * @return train type
	 */
	public TrainClass getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return no.hashCode();
	}

	/**
	 * Queries if this train is available on a given day.
	 * @return true if available
	 */
	public boolean isAvailable(TRADate date) throws IOException {

		if (date.before(TRADate.now())) {
			throw new IllegalArgumentException();
		}

		TrainStop ori = stops.get(0);
		if (ori == null) {
			return false;
		}
		TrainStop des = stops.get(stops.size() - 1);
		if (des == null) {
			return false;
		}

		TimeTable ttb = TimeTable.search(ori.stt, des.stt, date, ori.dep, des.arv);
		if (ttb == null) {
			return false;
		}

		//Check if contains this train
		return ttb.getItem(no) != null;
	}

	/**
	 * Queries if this train stops at a station.
	 * @param station station to be checked
	 * @return true if this train stops at such station
	 */
	public boolean stopsAt(Station station) {
		return stops != null && stops.stream().anyMatch(s -> s.stt == station);
	}

	@Override
	public String toString() {
		return getClass().getName() + "\n -------------------------------- \n" + description() + " -------------------------------- \n";
	}
}
