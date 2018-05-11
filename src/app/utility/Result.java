package app.utility;

import facility.Station;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result extends HashMap<Station, ResultItem> {

	public Result() {
		super();
	}

	public void increaseCount(int count, Station station, boolean stopped) {
		ResultItem rItem;

		rItem = get(station);
		if (rItem == null) {
			synchronized (this) {
				rItem = get(station);
				if (rItem == null) {
					rItem = new ResultItem();
					put(station, rItem);
				}
			}
		}

		if (stopped) {
			rItem.nStop.getAndAdd(count);
		}
		else {
			rItem.nPass.getAndAdd(count);
		}
	}

	@Override
	public String toString() {
		return toString(ResultItem.SORT_BY_STOPPING_RATE, false);
	}

	public String toString(ResultItem.Comparator comp, boolean showAll) {
		List<Map.Entry<Station, ResultItem>> list = new ArrayList<>(this.entrySet());
		StringBuilder sb = new StringBuilder(25 * size());
		list.sort(comp);
		for (Map.Entry<Station, ResultItem> e : list) {
			ResultItem ib = e.getValue();
			if (!showAll && ib.getStoppingCount() == 0) {
				continue;
			}
			sb.append(e.getKey().toString())
			  .append("\t")
			  .append(String.format("%4d", ib.getStoppingCount()))
			  .append("\t")
			  .append(String.format("%4d", ib.getSum()))
			  .append("\t")
			  .append(String.format("%7s", String.format("%.2f", ib.getStoppingRate() * 100) + "%"))
			  .append("\n");
		}
		return sb.toString();
	}
}

