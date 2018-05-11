package facility;

public enum StationLevel {

    CLOSED, SIGNAL, UNMAN, SINGLE_C, SINGLE_B, SINGLE_A, SINGLE, C, B, A, S;

    public static StationLevel getInstance(String level) {
        for (StationLevel sl : values()) {
            if (sl.toString().equals(level)) return sl;
        }
        return null;
    }

    @Override
    public String toString() {
        switch (this) {
        case CLOSED: return "廢站";
        case SIGNAL: return "號誌";
        case UNMAN: return "招呼";
        case SINGLE_C: return "丙簡";
        case SINGLE_B: return "乙簡";
        case SINGLE_A: return "甲簡";
        case SINGLE: return "簡易";
        case C: return "三等";
        case B: return "二等";
        case A: return "一等";
        case S: return "特等";
        }
        throw new InternalError(); //Never happens
    }
}
