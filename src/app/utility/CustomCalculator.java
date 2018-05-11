package app.utility;

import facility.RailwayMap;
import facility.Station;
import facility.Train;
import facility.TrainStop;
import java.io.IOException;
import java.util.List;

public abstract class CustomCalculator extends Calculator {

	protected final Station station;
	protected final ResultItem rItem = new ResultItem();

	public CustomCalculator(Station station) {
		this.station = station;
	}

	@Override
	protected void run() {
		Train train;

		noloop:
		while ((no = counter.next()) != -1 && !searcher.interrupted) {
			//Get train
			try {
				train = searcher.search(no, date);
			}
			catch (IOException e) {
				searcher.interrupted = true;
				return;
			}

			//Train not found, skip
			if (train == null) {
				continue;
			}

			//Get increment
			int count;
			try {
				count = increment(searcher, train);
			}
			catch (IOException e1) {
				searcher.interrupted = true;
				return;
			}

			//Check if this train stops at this station
			List<TrainStop> trainStops = train.getStops();
			if (trainStops.stream().anyMatch(stop -> stop.getStation() == station)) {
				rItem.nStop.getAndAdd(count);
				continue;
			}

			//Check if this train passes this station
			count = trainStops.size();
			int countDec = count - 1;
			for (int i = 0 ; i < countDec && !searcher.interrupted ; i++) {
				List<Station> stations = RailwayMap.path(trainStops.get(i).getStation(), trainStops.get(i + 1).getStation());
				if (stations.contains(station)) {
					rItem.nPass.getAndAdd(count);
					continue noloop;
				}
			}
		}

	}

	@Override
	public final ResultItem calculate() {
		//Start tasks
		threads.forEach(r -> r.start());

		//Wait for tasks
		threads.forEach(r -> {
			try {
				r.join();
			}
			catch (InterruptedException e) {
			}
		});

		//Check if tasks were interrupted
		return searcher.interrupted ? null : rItem;
	}
}
