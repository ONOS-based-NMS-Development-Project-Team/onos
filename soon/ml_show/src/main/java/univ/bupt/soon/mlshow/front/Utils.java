package univ.bupt.soon.mlshow.front;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类，包含了handler通用的变量
 */
public class Utils {

    private Utils() {
    }

    // 时间数据格式
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 解析Date
    public static final String parseDate(Date date) {
        if (date == null) {
            return "null";
        } else {
            return formatter.format(date);
        }
    }
    public static String checkNull(double[] obj) {
        if (obj == null) {
            return "null";
        } else {
            StringBuilder builder = new StringBuilder("[");
            for (double d : obj) {
                builder.append(d).append(", ");
            }
            builder.append("]");
            return builder.toString();
        }
    }

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
    public static final String ALARM_HAPPEN = "alarmHappen";
    public static final String TRAIN = "train";
    public static final String DATA_ID = "dataId";

    public static final String DATASET_ID = "dataSetId";
    public static final String DATASET_TYPE =  "dataSetType";


    public static final String ALARM_SOURCE = "alarmSource";
    public static final String TIME_OCCUR = "timeOccur";
    public static final String TIME_FIRST_OCCUR = "timeFirstOccur";


    public static final String RESULT = "result";

    // 历史告警数据返回条目
    public static final String LEVEL = "level";
    public static final String NAME = "name";
    public static final String ALARM_SRC = "alarmSource";
    public static final String TYPE = "type";
    public static final String LOCATION = "location";
    public static final String HAPPEN_TIME = "happenTime";
    public static final String CLEAN_TIME = "cleanTime";
    public static final String CONFIRM_TIME = "confirmTime";
    public static final String PATH_LEVEL = "pathLevel";

    // 当前告警数据返回条目
    public static final String FREQUENCY = "frequency";
    public static final String FIRST_HAPPEN = "firstHappen";
    public static final String RECENT_HAPPEN = "recentHappen";
    public static final String CLEAN = "clean";
    public static final String CONFIRM = "confirm";

    // 性能数据返回条目
    public static final String NODE = "node";
    public static final String BOARD = "board";
    public static final String PORT = "port";
    public static final String COMPONENT = "component";
    public static final String EVENT = "event";
    public static final String END_TIME = "endTime";
    public static final String MAX_VAL = "maxVal";
    public static final String CUR_VAL = "curVal";
    public static final String MIN_VAL = "minVal";

    // 告警预测模型应用结果
    public static final String TIME = "time";
    public static final String MODEL_ID = "modelId";
    public static final String TRAIN_DATASET_ID = "trainDataSetId";
    public static final String TEST_DATASET_ID = "testDataSetId";
    public static final String TEST_ACCURACY = "testAccuracy";
    public static final String INPUT_TYPE = "inputType";


    // 故障分类
    public static final String FAULT_TYPE = "faultType";

    // 区域预测
    public static final String AREA_ID = "areaId";
    public static final String TIDE = "tide";
    public static final String TIMEPOINT = "timepoint";
    public static final String ONE_HOUR_AFTER = "oneHoursAfter";
    public static final String TWO_HOURS_BEFORE = "twoHoursBefore";

    // 边预测
    public static final String EDGE_ID = "edgeId";

}
