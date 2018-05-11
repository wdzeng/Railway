package facility;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains all routes in detail, including the location of all stations.
 * @author Parabola
 * @see RailwayMap
 */
public final class Route {

    public static final Route CHENZHUI_LINE = new Route("成追線", Station.getInstance("成功"), null);
    public static final Route COAST_LINE = new Route("海線", Station.getInstance("大甲"), null);
    public static final Route JIJI_LINE = new Route("集集線", Station.getInstance("集集"), Station
            .getInstance("二水"));
    public static final Route LIUJIA_LINE = new Route("六家線", Station.getInstance("六家"), Station
            .getInstance("竹中"));
    public static final Route MOUNTAIN_LINE = new Route("山線", Station.getInstance("台中"), null);
    public static final Route NEIWAN_LINE = new Route("內灣線", Station.getInstance("內灣"), Station
            .getInstance("竹中"));
    public static final Route NEIWAN_LIUJIA_LINE = new Route("內灣線", Station.getInstance("北新竹"),
            Station.getInstance("北新竹"));
    public static final Route NORTHERN_LINE = new Route("北迴線", Station.getInstance("南澳"), null);
    public static final Route PINGTUNG_LINE = new Route("屏東線", Station.getInstance("屏東"), null);
    public static final Route PINGXI_LINE = new Route("平溪線", Station.getInstance("平溪"), Station
            .getInstance("三貂嶺"));
    public static final Route SHALUN_LINE = new Route("沙崙線", Station.getInstance("沙崙"), Station
            .getInstance("中洲"));
    public static final Route SHEN_AO_LINE = new Route("深澳線", Station.getInstance("海科館"), Station
            .getInstance("瑞芳"));
    public static final Route SOUTHERN_LINE = new Route("南迴線", Station.getInstance("知本"), null);
    public static final Route TAITUNG_LINE = new Route("台東線", Station.getInstance("玉里"), null);
    public static final Route WESTERN_LINE_NORTH = new Route("縱貫線", Station.getInstance("台北"), null);
    public static final Route WESTERN_LINE_SOUTH = new Route("縱貫線", Station.getInstance("台南"), null);
    public static final Route YILAN_LINE = new Route("宜蘭線", Station.getInstance("宜蘭"), null);
    private static final Route[] ROUTELIST = {WESTERN_LINE_NORTH, WESTERN_LINE_SOUTH, COAST_LINE,
            MOUNTAIN_LINE, TAITUNG_LINE, SOUTHERN_LINE, NORTHERN_LINE, PINGTUNG_LINE, YILAN_LINE,
            JIJI_LINE, NEIWAN_LIUJIA_LINE, NEIWAN_LINE, PINGXI_LINE, SHALUN_LINE, SHEN_AO_LINE,
            LIUJIA_LINE, CHENZHUI_LINE};

    static {
        WESTERN_LINE_NORTH.stations = tra("基隆", "三坑", "八堵", "七堵", "百福", "五堵", "汐止", "汐科", "南港", "松山",
                "台北", "萬華", "板橋", "浮洲", "樹林", "南樹林", "山佳", "鶯歌", "桃園", "內壢", "中壢", "埔心", "楊梅", "富岡",
                "北湖", "湖口", "新豐", "竹北", "北新竹", "新竹", "三姓橋", "香山", "崎頂", "竹南");
        COAST_LINE.stations = tra("竹南", "談文", "大山", "後龍", "龍港", "白沙屯", "新埔", "通霄", "苑裡", "日南", "大甲",
                "台中港", "清水", "沙鹿", "龍井", "大肚", "追分", "彰化");
        MOUNTAIN_LINE.stations = tra("竹南", "造橋", "豐富", "苗栗", "南勢", "銅鑼", "三義", "泰安", "后里", "豐原", "潭子",
                "太原", "台中", "大慶", "烏日", "新烏日", "成功", "彰化");
        WESTERN_LINE_SOUTH.stations = tra("彰化", "花壇", "大村", "員林", "永靖", "社頭", "田中", "二水", "林內", "石龜",
                "斗六", "斗南", "石榴", "大林", "民雄", "嘉北", "嘉義", "水上", "南靖", "後壁", "新營", "柳營", "林鳳營", "隆田",
                "拔林", "善化", "南科", "新市", "永康", "大橋", "台南", "保安", "仁德", "中洲", "大湖", "路竹", "岡山", "橋頭",
                "楠梓", "新左營", "左營", "高雄");
        PINGTUNG_LINE.stations = tra("高雄", "鳳山", "後庄", "九曲堂", "六塊厝", "屏東", "歸來", "麟洛", "西勢", "竹田",
                "潮州", "崁頂", "南州", "鎮安", "林邊", "佳冬", "東海", "枋寮");
        SOUTHERN_LINE.stations = tra("枋寮", "加祿", "內獅", "枋山", "枋野", "古莊", "大武", "瀧溪", "金崙", "太麻里",
                "知本", "康樂", "台東");
        TAITUNG_LINE.stations = tra("台東", "山里", "鹿野", "瑞源", "瑞和", "關山", "海端", "池上", "富里", "東竹", "東里",
                "玉里", "三民", "瑞穗", "富源", "大富", "光復", "萬榮", "鳳林", "南平", "豐田", "壽豐", "平和", "志學", "吉安",
                "花蓮");
        NORTHERN_LINE.stations = tra("花蓮", "北埔", "景美", "新城", "崇德", "和仁", "和平", "漢本", "武塔", "南澳", "東澳",
                "永樂", "蘇澳新");
        YILAN_LINE.stations = tra("八堵", "暖暖", "四腳亭", "瑞芳", "三貂嶺", "侯硐", "三貂嶺", "牡丹", "雙溪", "貢寮", "福隆",
                "石城", "大里", "大溪", "龜山", "外澳", "頭城", "頂埔", "礁溪", "四城", "宜蘭", "二結", "中里", "羅東", "冬山",
                "新馬", "蘇澳新", "蘇澳");
        SHALUN_LINE.stations = tra("中洲", "長榮大學", "沙崙");
        NEIWAN_LINE.stations = tra("竹中", "上員", "榮華", "竹東", "橫山", "九讚頭", "合興", "富貴", "內灣");
        JIJI_LINE.stations = tra("二水", "源泉", "濁水", "龍泉", "集集", "水里", "車埕");
        PINGXI_LINE.stations = tra("三貂嶺", "大華", "十分", "望古", "嶺腳", "平溪", "菁桐");
        SHEN_AO_LINE.stations = tra("瑞芳", "海科館", "八斗子");
        LIUJIA_LINE.stations = tra("竹中", "六家");
        NEIWAN_LIUJIA_LINE.stations = tra("北新竹", "千甲", "新莊", "竹中");
        CHENZHUI_LINE.stations = tra("成功", "追分");
    }

    private final Station mainSta;
    private final String name;
    private final Station stml;
    private Station[] stations;

    private Route(String routeName, Station mainStation, Station stationToMainLine) {
        this.name = routeName;
        this.stml = stationToMainLine;
        this.mainSta = mainStation;
    }

    public static List<Route> all() {
        return Arrays.asList(ROUTELIST);
    }

    private static Station[] tra(String... ss) {
        Station[] arr = new Station[ss.length];
        for (int i = 0; i < ss.length; i++) {
            if (ss[i] == null) throw new Error("Null at index: " + i);
            Station s = Station.getInstance(ss[i]);
            if (s == null) throw new Error("Station not found: " + ss[i]);
            arr[i] = s;
        }
        return arr;
    }

    /**
     * Know if a station is at this route.
     * @param station argument station.
     * @return true if a station is on this route.
     * @throws IllegalArgumentException if parameter <code>station</code> is null.
     */
    public boolean contains(Station station) {
        return Arrays.asList(stations).contains(station);
    }


    /**
     * Get the station by index.
     * @param index argument index.
     * @return the station by index.
     * @throws IllegalArgumentException if index is out of bound.
     */
    public Station get(int index) {
        if (index < 0 || index >= stations.length)
            throw new IllegalArgumentException("Index is out of bound [0-" + (stations.length - 1) + "] : "
                    + index);
        return stations[index];
    }

    /**
     * Get the linked station of this route and another route, or null if these routes are not adjacent.
     * @param anotherRoute argument route.
     * @return the linked station of this route and another route, or null if these routes are not
     * adjacent.
     */
    public Station getJuncStation(Route anotherRoute) {
        return Arrays.stream(stations).filter(anotherRoute::contains).findFirst().orElse(null);
    }

    /**
     * <p>Get the main station of this route. The main station is defined as the largest station at this
     * route, while jinctional stations excluded. Below are main stations of all routes:</p> <ul>
     * <li>WESTERN_LINE_NORTH : Taipei</li> <li>WESTURN_LINE_SOUTH : Tainan</li> <li>MOUNTAIN_LINE:
     * Taichung</li> <li>COAST_LINE : Dajia</li> <li>NORTHERN_LINE : Nanao</li> <li>SOUTHERN_LINE :
     * Jiben</li> <li>YILAN_LINE : Yilan</li> <li>PINGTUNG_LINE : Pingtung</li> <li>TAITUNG_LINE :
     * Yule</li> <li>NEIWAN_LINE : Neiwan</li> <li>LIUJIA_LINE : LIUJIA</li> <li>NEIWAN_LIUJIA_LINE :
     * North Hsinchu</li> <li>JIJI_LINE : Jiji</li> <li>PINGXI_LINE : Pingxi</li> <li>SHEN_AO_LINE :
     * Heikeguan</li> <li>SHALUN_LINE : Shalun</li> <li>CHENZHUI_LINE : Chenkung</li> </ul>
     * @return the main station of this route.
     */
    public Station getMainStation() {
        return mainSta;
    }

    /**
     * Get the station to main line.
     * @return the station to main line, or null is this route is part of main line.
     */
    public Station getStationToMainLine() {
        return stml;
    }

    /**
     * Get the index of given station in this route.
     * @param station argument station.
     * @return the index of given station in this route, or -1 if this station are not at this route.
     */
    public int indexOf(Station station) {
        return Arrays.asList(stations).indexOf(station);
    }

    /**
     * Know if this route is next to another route.
     * @param anotherRoute argument route.
     * @return true if this route is next to another route.
     */
    public boolean isJuncTo(Route anotherRoute) {
        return Arrays.stream(stations).anyMatch(anotherRoute::contains);
    }

    /**
     * TrainBuilder if this route is part of the main line.
     * @return true if this route is part of the main line.
     */
    public boolean isMainLine() {
        return stml == null;
    }

    /**
     * Get all the stations of this route.
     * @return all the stations of this route.
     */
    public List<Station> stations() {
        return Arrays.asList(stations);
    }

    @Override
    public String toString() {
        return name;
    }

}
