package facility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains the data of relative locations of all stations. Use this class to find the path a
 * train goes startTime a station to another station, and verify all the stations it passes by.
 * @author Parabola
 * @see Route
 */
public class RailwayMap {

	// 必停車站包含台北、板橋、台中、大甲、沙鹿、台南、高雄、知本、台東、花蓮；玉里已除名
	// 端點車站包含基隆、蘇澳、內灣、六家、沙崙、車埕、八斗子、菁桐
	// 以上車站為必停靠車站

	/**
	 * ChenZhui Line (成追線).
	 */
	public static final Via CHENZHUI = new Via("Chen Zhui Line");
	/**
	 * Coast Line (海線).
	 */
	public static final Via COAST = new Via("Coast Line");
	/**
	 * This indicates the best, or the shortest, route.
	 */
	public static final Via DEFAULT = new Via(null);
	/**
	 * Mountain Line (山線).
	 */
	public static final Via MOUNTAIN = new Via("Mountain Line");
	/**
	 * This can be the northern part of Western Line, Yilan Line or Northern Line.
	 * 此路線泛指一切經由台灣北部行駛於東、西部的路線，包含縱貫線北段、宜蘭線和北迴線。
	 */
	public static final Via NORTH = new Via("northern path");
	/**
	 * This can be Pingtung Line or Southern Line. 此路線泛指一切經由台灣南部行駛於東、西部的路線，包含屏東線和南迴線。
	 */
	public static final Via SOUTH = new Via("southern path");

	private static final Via IGNORE = new Via(null);
	private static final List<Route> ROUND_CS = Arrays.asList(Route.WESTERN_LINE_NORTH,
	                                                          Route.YILAN_LINE,
	                                                          Route.NORTHERN_LINE,
	                                                          Route.TAITUNG_LINE,
	                                                          Route.SOUTHERN_LINE,
	                                                          Route.PINGTUNG_LINE,
	                                                          Route.WESTERN_LINE_SOUTH,
	                                                          Route.COAST_LINE);
	private static final List<Route> ROUND_MT = Arrays.asList(Route.WESTERN_LINE_NORTH,
	                                                          Route.YILAN_LINE,
	                                                          Route.NORTHERN_LINE,
	                                                          Route.TAITUNG_LINE,
	                                                          Route.SOUTHERN_LINE,
	                                                          Route.PINGTUNG_LINE,
	                                                          Route.WESTERN_LINE_SOUTH,
	                                                          Route.MOUNTAIN_LINE);

	private static class RouteMatcher {

		private final Route r1, r2;

		public RouteMatcher(Route a, Route b) {
			r1 = a;
			r2 = b;
		}

		public boolean match(Route a, Route b) {
			return r1 == a && r2 == b || r1 == b && r2 == a;
		}
	}

	public static class Via {

		private final String name;

		private Via(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static List<Station> cal(Station from, Station to, Via via) {
		if (from == to) {
			throw new IllegalArgumentException(from.getName());
		}

		List<Station> list = new ArrayList<>();
		Route r = from.getRoute(to);

		// 判斷兩車站是否位於同線上
		if (r != null) {
			int d0 = r.indexOf(from), d1 = r.indexOf(to);
			if (d0 < d1) {
				for (int d = d0 ; d < d1 ; d++) {
					list.add(r.get(d));
				}
				return list;
			}
			for (int d = d0 ; d > d1 ; d--) {
				list.add(r.get(d));
			}
			return list;
		}

		// 不在同一條線上，判斷支線與否
		Route rf = from.getRoute();
		//起點站位在支線
		if (!rf.isMainLine()) {
			Station o = rf.getStationToMainLine();
			list.addAll(cal(from, o, IGNORE));
			list.addAll(cal(o, to, via));
			return list;
		}
		Route rt = to.getRoute();
		//終點站位在支線
		if (!rt.isMainLine()) {
			Station o = rt.getStationToMainLine();
			list.addAll(cal(from, o, via));
			list.addAll(cal(o, to, IGNORE));
			return list;
		}

		// 判斷兩車站是否位在相鄰的線上
		Station sj = rf.getJuncStation(rt);
		// 兩車站在相鄰線上
		if (sj != null) {
			list.addAll(cal(from, sj, IGNORE));
			list.addAll(cal(sj, to, IGNORE));
			return list;
		}

		// 兩車站不在相鄰線上，則尋找應經過的路線
		// 未指定路線
		if (via == DEFAULT) {
			/*  使用RouteMatcher尋找應經過的路線，判斷應走北迴或南迴。
			 *   縱貫線北段 -> 北迴線 : 北環
			 *   縱貫線北段 -> 台東線 : 北環
			 *   宜蘭線     -> 台東線 : 北環
			 *   縱貫線南段 -> 南迴線 : 南迴
			 *   縱貫線南段 -> 台東線 : 南迴
			 *   屏東線     -> 台東線 : 南迴
			 *   若無法判斷北迴或南迴，則表示此路線應與北迴、南迴無關，則用慣例法搜尋路線。
			 */
			RouteMatcher rm = new RouteMatcher(rf, rt);
			if (rm.match(Route.WESTERN_LINE_NORTH, Route.NORTHERN_LINE)) {
				via = NORTH;
			}
			else if (rm.match(Route.WESTERN_LINE_NORTH, Route.TAITUNG_LINE)) {
				via = NORTH;
			}
			else if (rm.match(Route.YILAN_LINE, Route.TAITUNG_LINE)) {
				via = NORTH;
			}
			else if (rm.match(Route.WESTERN_LINE_SOUTH, Route.SOUTHERN_LINE)) {
				via = SOUTH;
			}
			else if (rm.match(Route.WESTERN_LINE_SOUTH, Route.TAITUNG_LINE)) {
				via = SOUTH;
			}
			else if (rm.match(Route.PINGTUNG_LINE, Route.TAITUNG_LINE)) {
				via = SOUTH;
			}

			/*  無法找到適合的路線，則用慣例配對。
			 *   山、海線 -> 屏東線 : 經台南
			 *   山、海線 -> 宜蘭線 : 經台北
			 *   其餘則丟出PathNotFoundException，因為找不到路線。
			 */
			else {
				if ((rf == Route.MOUNTAIN_LINE || rf == Route.COAST_LINE) && rt == Route.PINGTUNG_LINE) {
					list.addAll(cal(from, Route.WESTERN_LINE_SOUTH.getMainStation(), IGNORE));
					list.addAll(cal(Route.WESTERN_LINE_SOUTH.getMainStation(), to, IGNORE));
					return list;
				}
				if ((rt == Route.MOUNTAIN_LINE || rt == Route.COAST_LINE) && rf == Route.PINGTUNG_LINE) {
					list.addAll(cal(from, Route.WESTERN_LINE_SOUTH.getMainStation(), IGNORE));
					list.addAll(cal(Route.WESTERN_LINE_SOUTH.getMainStation(), to, IGNORE));
					return list;
				}
				if ((rf == Route.MOUNTAIN_LINE || rf == Route.COAST_LINE) && rt == Route.YILAN_LINE) {
					list.addAll(cal(from, Route.WESTERN_LINE_NORTH.getMainStation(), IGNORE));
					list.addAll(cal(Route.WESTERN_LINE_NORTH.getMainStation(), to, IGNORE));
					return list;
				}
				if ((rt == Route.MOUNTAIN_LINE || rt == Route.COAST_LINE) && rf == Route.YILAN_LINE) {
					list.addAll(cal(from, Route.WESTERN_LINE_NORTH.getMainStation(), IGNORE));
					list.addAll(cal(Route.WESTERN_LINE_NORTH.getMainStation(), to, IGNORE));
					return list;
				}
				return null;
			}
		}

		final int l1, l2, l3;
		final List<Route> routes;
		if (via == MOUNTAIN) {
			routes = ROUND_MT;
			l1 = routes.indexOf(rf);
			l2 = routes.size() - 1;
			l3 = routes.indexOf(rt);
		}
		else if (via == COAST) {
			routes = ROUND_CS;
			l1 = routes.indexOf(rf);
			l2 = routes.size() - 1;
			l3 = routes.indexOf(rt);
		}
		else if (via == NORTH) {
			routes = ROUND_MT;
			l1 = routes.indexOf(rf);
			l2 = routes.indexOf(Route.NORTHERN_LINE);
			l3 = routes.indexOf(rt);
		}
		else if (via == SOUTH) {
			routes = ROUND_MT;
			l1 = routes.indexOf(rf);
			l2 = routes.indexOf(Route.SOUTHERN_LINE);
			l3 = routes.indexOf(rt);
		}
		else {
			return null;
		}

		//順行或逆行
		final boolean way;
		if (l1 <= l2 && l2 <= l3) {
			way = true;
		}
		else if (l3 <= l2 && l2 <= l1) {
			way = false;
		}
		else if (l2 <= l1 && l1 <= l3) {
			way = false;
		}
		else if (l3 <= l1 && l1 <= l2) {
			way = true;
		}
		else if (l1 <= l3 && l3 <= l2) {
			way = false;
		}
		else {
			way = true;
		}

		int i;
		if (way) {
			list.addAll(cal(from, routes.get(next(l1, routes)).getMainStation(), IGNORE));
			for (i = next(l1, routes); i != last(l3, routes) ; i = next(i, routes)) {
				list.addAll(cal(routes.get(i).getMainStation(), routes.get(next(i, routes)).getMainStation(), IGNORE));
			}
			list.addAll(cal(routes.get(i).getMainStation(), to, IGNORE));
			return list;
		}

		list.addAll(cal(from, routes.get(last(l1, routes)).getMainStation(), IGNORE));
		for (i = last(l1, routes); i != next(l3, routes) ; i = last(i, routes)) {
			list.addAll(cal(routes.get(i).getMainStation(), routes.get(last(i, routes)).getMainStation(), IGNORE));
		}
		list.addAll(cal(routes.get(i).getMainStation(), to, IGNORE));
		return list;
	}

	private static int last(int i, List<Route> mt) {
		if (mt == ROUND_MT) {
			return i == 0 ? ROUND_MT.size() - 1 : i - 1;
		}
		return i == 0 ? ROUND_CS.size() - 1 : i - 1;
	}

	private static int next(int i, List<Route> mt) {
		if (mt == ROUND_MT) {
			return i == ROUND_MT.size() - 1 ? 0 : i + 1;
		}
		return i == ROUND_CS.size() - 1 ? 0 : i + 1;
	}

	/**
	 * Get a list that contains all the stations by given stations in order via given route.
	 * @param stations a list of station a train passes in order.
	 * @param via      a route
	 * @return a list that contains all the stations by given stations in order, or null if a proper way
	 * is not found.
	 */
	public static List<Station> path(Station[] stations, Via via) {
		List<Station> list = new ArrayList<>();
		for (int i = 0 ; i < stations.length - 1 ; i++) {
			List<Station> added = cal(stations[i], stations[i + 1], via);
			if (added == null) {
				return null;
			}
			list.addAll(added);
		}
		list.add(stations[stations.length - 1]);
		return list;
	}

	/**
	 * Get a list which contains all stations startTime a station to another station in order. The system will
	 * try to find the shortest route.
	 * @param from a station
	 * @param to   another station
	 * @return a list containing all stations in order, or null if a proper way is not found.
	 */
	public static List<Station> path(Station from, Station to) {
		return path(from, to, DEFAULT);
	}

	/**
	 * Get a list which contains all stations startTime a station to another station via given route in
	 * order.
	 * @param from a station
	 * @param to   another station
	 * @param via  a route
	 * @return a list which contains all stations startTime a station to another station via given route in
	 * order, or null if a proper way is not found.
	 */
	public static List<Station> path(Station from, Station to, Via via) {
		return cal(from, to, via);
	}

	/**
	 * Get a list that contains all the stations by given stations in order. The system will try to find
	 * the shortest route.
	 * @param stations a list of station a train passes in order.
	 * @return a list that contains all the stations by given stations in order, or null if a proper way
	 * is not found.
	 */
	public static List<Station> path(Station[] stations) {
		return path(stations, DEFAULT);
	}
}