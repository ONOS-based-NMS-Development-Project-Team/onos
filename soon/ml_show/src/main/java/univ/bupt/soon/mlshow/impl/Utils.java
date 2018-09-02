package univ.bupt.soon.mlshow.impl;

import java.text.SimpleDateFormat;

/**
 * 工具类，包含了handler通用的变量
 */
public class Utils {

    private Utils() {
    }

    // 时间数据格式
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 排序变量
    public static final String FIRST_COL = "firstCol";
    public static final String FIRST_DIR = "firstDir";
    public static final String SECOND_COL = "secondCol";
    public static final String SECOND_DIR = "secondDir";
    public static final String ASC = "asc";

    // annotation变量
    public static final String ANNOTS = "annots";
    // 找不到应用
    public static final String NO_ROWS_MESSAGE = "No applications found";

    public static final String ID = "id";
    public static final String INPUT = "input";
    public static final String ALARM_HAPPEN = "alarm_happen";
    public static final String TRAIN = "train";
    public static final String DATAID = "dataid";


    public static final String ALARM_SOURCE = "alarmSource";
    public static final String TIME_OCCUR = "timeOccur";
    public static final String TIME_FIRST_OCCUR = "timeFirstOccur";


    public static final String RESULT = "result";

    // 历史数据返回条目
    public static final String LEVEL = "level";
    public static final String NAME = "name";
    public static final String ALARM_SRC = "alarm_src";
    public static final String TP = "tp";
    public static final String LOCATION = "location";
    public static final String HAPPEN_TIME = "happen_time";
    public static final String CLEAN_TIME = "clean_time";
    public static final String CONFIRM_TIME = "confirm_time";
    public static final String PATH_LEVEL = "path_level";

}
