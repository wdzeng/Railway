import facility.RailwayMap;
import facility.Station;

public class Test {

	public static void main(String[] args) {
		System.out.println(RailwayMap.path(Station.getInstance("南澳"), Station.getInstance("松山")));
	}
}
