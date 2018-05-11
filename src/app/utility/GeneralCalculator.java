package app.utility;

import facility.RailwayMap;
import facility.Station;
import facility.Train;
import facility.TrainStop;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class GeneralCalculator extends Calculator {

	protected final Result result = new Result();

	@Override
	protected void run() {
		Train train;

		while ((no = counter.next()) != -1 && /*Check interrupted*/ !searcher.interrupted) {
			//Get train
			try {
				train = searcher.search(no);
			}
			catch (IOException e) {
				searcher.interrupted = true;
				return;
			}

			//Train not found
			if (train == null) {
				continue;
			}

			//Get increment
			int increment;
			try {
				increment = increment(searcher, train);
			}
			catch (IOException e1) {
				searcher.interrupted = true;
				return;
			}

			//Calculate
			List<TrainStop> stops = train.getStops();
			List<Station> path;
			int nStop = stops.size();
			int nStopDec = nStop - 1;
			int nStation;
			for (int i = 0 ; i < nStopDec && /*Check interrupted*/ !searcher.interrupted ; i++) {
				path = new LinkedList<>(RailwayMap.path(stops.get(i).getStation(), stops.get(i + 1).getStation()));
				nStation = path.size();

				result.increaseCount(increment, path.get(0), true);
				for (int j = 1 ; j < nStation ; j++) {
					if (path.get(j) == Station.getInstance("台北")) {
						System.err.println(stops);
					}
					result.increaseCount(increment, path.get(j), false);
				}
			}

			result.increaseCount(increment, stops.get(nStopDec).getStation(), true);
		}
	}

	@Override
	public final Result calculate() {
		//Start tasks
		threads.forEach(r -> r.start());

		//Wait for classes
		threads.forEach(r -> {
			try {
				r.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		//Check if tasks were interrupted
		return searcher.interrupted ? null : result;
	}
}
