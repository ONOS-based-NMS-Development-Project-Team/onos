package univ.bupt.soon.mlshow.front;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类，包含了handler通用的变量
 */
public class Utils {
    private static Logger log = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    // 性能表中节点,历史告警和当前告警中的告警源的中英文映射
    public static Map<String, String> nodeMap = Maps.newHashMap();
    // 性能表中的性能事件的中英文映射
    public static Map<String, String> eventMap = Maps.newHashMap();
    // 初始化nodesMap和eventMap
    static {
        String nodeFile = "nodes2eng.csv";
        String eventFile = "events2eng.csv";
        String path = System.getenv("ONOS_ROOT");

        parseTrans(path + "/soon/resources/"+nodeFile, nodeMap);
        parseTrans(path + "/soon/resources/"+eventFile, eventMap);
        log.info(nodeMap.toString());
        log.info(eventMap.toString());
    }

    /**
     * 解析中英文
     * @param file
     * @param map
     */
    private static void parseTrans(String file, Map<String, String> map) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            reader.readLine(); // 去掉表头
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); // 中文,英文
                map.put(values[0], values[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static String arrayToString (double[] input) {
        String s = "";
        for(int i=0;i<input.length;i++){
            if(i == 1){
                s = String.valueOf(i);
            }else {
                s = s + "," + i;
            }
        }
        return s;
    }

    public static String setToString (Set<Integer> set) {
        String s = "";
        for (Integer i : set) {
            int index = 0;
            int a = i.intValue();
            String b = String.valueOf(a);
            if (index == 0){
                s = b;
                index++;
            }else {
                s = s+","+b;
                index++;
            }
        }
        return s;
    }

    public static List<Integer> stringToIntList (String s) {
        List<Integer> list = new ArrayList<>();
        String[] sa = new String[]{};
        sa = s.split(",");
        for (int i=0;i<sa.length;i++)
        {
            int t = Integer.parseInt(sa[i]);
            list.add(t);
        }
        return list;
    }

    public static String listToString (List<Integer> list) {
        String s = "";
        if(list == null){
            return s;
        }
        for(int i=0;i<list.size();i++){
            if(i<list.size()-1){
                s = s +list.get(i)+",";
            }
            if(i == list.size()-1){
                s = s+list.get(i);
            }
        }
        return s;
    }

    public static String getStringEvaluate (Map<Integer,String> map) {
        String s = "";
        if(map == null){
            return s;
        }
        if (map.isEmpty()){
            return s;
        }
        else{
            for (String i : map.values()) {
                s = i + ",";
            }
            return s;
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

    //failure classification data set input col
    public static final String INPUT_1 = "input-1";
    public static final String INPUT_2 = "input-2";
    public static final String INPUT_3 = "input-3";
    public static final String INPUT_4 = "input-4";
    public static final String INPUT_5 = "input-5";
    public static final String INPUT_6 = "input-6";

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
    public static final String TIMEPOINT = "timePoint";
    public static final String ONE_HOUR_AFTER = "oneHoursAfter";
    public static final String TWO_HOURS_BEFORE = "twoHoursBefore";

    // 边预测
    public static final String EDGE_ID = "edgeId";

    //模型库
    public static final String APP_TYPE = "applicationType";
    public static final String ALGO_TYPE = "algorithmType";
    public static final String TRAIN_ID = "trainDataSetId";
    public static final String TEST_ID = "testDataSetId";
    public static final String MODEL_STATE = "ModelState";
    public static final String LOSS = "loss";
    public static final String REMAINING_TIME = "remainingTime";
    public static final String PRECISION = "precision";
    public static final String MODEL_LINK = "modelLink";
    public static final String MODEL_ACCURACY = "modelAccuracy";
    public static final String ALGO_PARAMS = "algorithmParams";
    public static final String INPUT_NUM = "inputNum";
    public static final String OUTPUT_NUM = "outputNum";
    public static final String HIDDEN_LAYER = "hiddenLayer";
    public static final String ACT_FUNTION = "activationFunction";
    public static final String WEIGHT_INIT = "weightInit";
    public static final String BIAS_INIT = "biasInit";
    public static final String LOSS_FUNCTION = "lossFunction";
    public static final String BATCH_SIZE = "batchSize";
    public static final String EPOCH = "epoch";
    public static final String OPTIMIZER = "optimizer";
    public static final String LR = "learningRate";
    public static final String LR_ADJUST = "lrAdjust";
    public static final String DROPOUT = "dropout";
    public static final String AVAI_TEST = "availableTest";//每个modelId对应的可用数据集存储
    public static final String AVAI_TRAIN = "availableTrain";
    //模型库Request参数
    public static final String ACTION = "action";
    public static final String MODEL_TRAINAVAI = "availableTrain";
    public static final String MODEL_TESTAVAI = "availableTest";
    public static final String MODEL_ALERT = "modelLibraryAlert";
    public static final String MODEL_ALERT_REQ = "modelLibraryAlertRequest";

    //模型应用
    public static final String RECENT_ITEM_NUM = "recentItemNum";
}
