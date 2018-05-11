import facility.Station;
import facility.TimeTable;
import java.io.IOException;

public class TimeTableDemo {

	public static void main(String[] args) throws IOException {

		//獲取今天台南到台北的時刻表
		TimeTable tt = TimeTable.search(Station.getInstance("台南"), Station.getInstance("台北"));
		System.out.println(tt);
	}
}
