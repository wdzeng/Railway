package facility;


/**
 * This class provides all cities and counties in Taiwan island. Note that New Taipei City and Keelung
 * City are regarded as parts of Taipei City. This class provides no constructors.
 */
public enum County {

	/**
	 * <p>Taipei City (台北市).</p><p>Note that New Taipei City and Keelung City are regarded as parts of
	 * Taipei City.</p>
	 */
	TAIPEI,
	/**
	 * <p>Taoyuan City (桃園市).</pp>
	 */
	TAOYUAN,
	/**
	 * <p>Hsinchi City (新竹市) and Hsinchu County (新竹縣).</p>
	 */
	HSINCHU,
	/**
	 * <p>Miaoli County (苗栗縣).</p>
	 */
	MIAOLI,
	/**
	 * <p>Taichung City (台中市).</p>
	 */
	TAICHUNG,
	/**
	 * <p>Changhua County (彰化縣).</p>
	 */
	CHANGHUA,
	/**
	 * <p>Yunlin County (雲林縣).</p>
	 */
	YUNLIN,
	/**
	 * <p>Jiayi City (嘉義市) and Jiayi County (嘉義縣).</p>
	 */
	JIAYI,
	/**
	 * <p>Tainan City (台南市).</p>
	 */
	TAINAN,
	/**
	 * <p>Kaoshung City (高雄市).</p>
	 */
	KAOSHIUNG,
	/**
	 * <p>Pingtung County (屏東縣).</p>
	 */
	PINGTUNG,
	/**
	 * <p>Taitung County (台東縣).</p>
	 */
	TAITUNG,
	/**
	 * <p>Hualien County (花蓮縣).</p>
	 */
	HUALIEN,
	/**
	 * <p>Yilan County (宜蘭縣).</p>
	 */
	YILAN,
	/**
	 * <p>Nantao County (南投縣).</p>
	 */
	NANTAO;

	public static County getInstance(String county) {
		county = county.replace("臺", "台");
		for (County c : values()) {
			if (c.toString().equals(county)) {
				return c;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		switch (this) {
		case TAIPEI:
			return "台北";
		case TAOYUAN:
			return "桃園";
		case HSINCHU:
			return "新竹";
		case MIAOLI:
			return "苗栗";
		case TAICHUNG:
			return "台中";
		case CHANGHUA:
			return "彰化";
		case YUNLIN:
			return "雲林";
		case JIAYI:
			return "嘉義";
		case TAINAN:
			return "台南";
		case KAOSHIUNG:
			return "高雄";
		case PINGTUNG:
			return "屏東";
		case TAITUNG:
			return "台東";
		case HUALIEN:
			return "花蓮";
		case YILAN:
			return "宜蘭";
		case NANTAO:
			return "南投";
		}
		throw new InternalError(); //Never happens.
	}
}
