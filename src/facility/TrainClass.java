package facility;

/**
 * Train type
 */
public enum TrainClass {

	TEMU2000("普悠瑪"),
	TEMU1000("太魯閣"),
	TC("自強"),
	CK("莒光"),
	FH("復興"),
	LOCAL("區間車"),
	LOCAL_FAST("區間快"),
	ORDINAL("普快");

	/**
	 * Gets the train type by a given name
	 * @param name train type name
	 * @throws IllegalArgumentException if name is invalid
	 */
	public static TrainClass getInstance(String name) {
		for (TrainClass tt : values()) {
			if (tt.name.contains(name)) {
				return tt;
			}
		}
		throw null;
	}



	private String name;

	TrainClass(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + name + "]";
	}
}
