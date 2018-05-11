import facility.Train;
import facility.TrainClass;
import facility.TrainLocation;
import java.io.IOException;

public class TrainDemo {

	public static void main(String[] args) throws IOException {

		//查詢今日110次列車
		Train t110 = Train.search("110");

		//獲取此車車種
		TrainClass clazz = t110.getType();
		System.out.println("車種 = " + clazz.getName());

		//獲取此車目前的位置
		TrainLocation loc = t110.getLocation();
		System.out.println("位置 = " + loc.description());

		//獲取此車的官方說明
		String cmt = t110.getComment();
		System.out.println("說明 = " + cmt);

		//印出此車的描述
		System.out.println(t110);
	}
}
