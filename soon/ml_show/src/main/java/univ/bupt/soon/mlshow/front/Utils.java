package univ.bupt.soon.mlshow.front;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
    // List
    public static List<String> nodesEng = Lists.newArrayList();
    // 性能表中的性能事件的中英文映射
    public static Map<String, String> eventMap = Maps.newHashMap();
    // List
    public static List<String> evetnsEng = Lists.newArrayList();
    // 初始化nodesMap和eventMap
    static {
        String nodeFile = "nodes2eng.csv";
        String eventFile = "events2eng.csv";
        String path = System.getenv("ONOS_ROOT");

        parseTrans(path + "/soon/resources/"+nodeFile, nodeMap);
        parseTrans(path + "/soon/resources/"+eventFile, eventMap);

        nodesEng.addAll(nodeMap.values());
        evetnsEng.addAll(eventMap.values());
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

    public static String levelChToEn (String s) {
        String level = "";
        if(s == "紧急"){
            level = "Critical";
        }
        else if(s == "重要"){
            level = "Major";
        }
        else if(s == "次要"){
            level = "Minor";
        }
        else if(s == "告警"){
            level = "Warning";
        }
        return level;
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

    public static String alarmInpParse(List<Double> list){
        List<String> display = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            if (i < 11) {
                double a = Math.random();
                double b = a + 38;
                String s = "-" + b;
                Double d = new Double(s);
                String q = "IN_PWR_LOW:" + d;
                display.add(q);
            } else if (i < 23) {
                double a = Math.random();
                double b = a + 660;
                String s = "-" + b;
                Double d = new Double(s);
                String q = "OUT_PWR_ABN:" + d;
                display.add(q);
            } else {
                double a = Math.random();
                double b = a + 11;
                String s = "-" + b;
                Double d = new Double(s);
                String q = "R_LOS:" + d;
                display.add(q);
            }
        }
            boolean first = true;
            StringBuilder result = new StringBuilder();
            for (String string : display) {
            if(first){
                first = false;
            }else {
                result.append(",");
            }
            result.append(string);
        }
            return result.toString();
        }


    public static String alarmOtpParse(List<Double> list){
        if (list.get(0) == 1.0 && list.get(1) == 0.0){
            return String.valueOf(true);
        }else {
            return String.valueOf(false);
        }
    }

    public static String waveParse(Double d){
        double w = d;
        BigDecimal bd = new BigDecimal(w*80);
        return String.valueOf(bd.setScale(0,BigDecimal.ROUND_HALF_UP));
    }

    public static String classEnd(List<Double> list) {
        if (list.get(0) == 1.0) {
            return "Board Failure";
        } else if (list.get(0) == 2.0) {
            return "Broken of Optical Cable";
        } else if (list.get(0) == 3.0) {
            return "Clock Failure";
        } else if (list.get(0) == 4.0) {
            return "Control Card Failure";
        } else if (list.get(0) == 5.0) {
            return "Equipment Power Off";
        } else if (list.get(0) == 6.0) {
            return "Loss of Line are Large ";
        } else {
            return "Deterioration of Basic Environment";
        }
    }

    public static String classInp(List<Double> list){
        List<String> level = new ArrayList<>();
        List<String> boards = new ArrayList<>();
        List<String> nodes = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> time = new ArrayList<>();
        level.add("Important");
        level.add("Urgent");
        names.add("R_LOS");
        names.add("R_LOF");
        names.add("IN_PWR_LOW");
        names.add("IN_PWR_HIGH");
        names.add("TU_LOP");
        names.add("T_LOSEX");
        names.add("T_ALOS");
        names.add("SECU_ALM");
        names.add("S1_SYN_CHANGE");
        boards.add("28-N3SL17-1(SDH-1)");
        boards.add("29-N4SL17-1(SDH-1)");
        boards.add("28-N3SL18-1(SDH-1)");
        boards.add("11-N3SL17-1(SDH-1)");
        boards.add("18-N3SL20-1(SDH-1)");
        boards.add("28-N3SL23-1(SDH-1)");
        boards.add("28-N1SF17-1(SDH-1)");
        boards.add("28-N3SR19-1(SDH-1)");
        boards.add("30-NF16E7-1(SDH-1)");
        boards.add("28-N3NLZ7-1(SDH-1)");
        boards.add("28-N3SL17-1(SDH-2)");
        boards.add("30-N4LQ18-2(SDH-1)");
        boards.add("28-N4SL17-2(SDH-1)");
        boards.add("2-N3SL17-1(SDH-1)");
        boards.add("28-N56L67-1(SDH-1)");
        boards.add("45-N3CL17-1(SDH-1)");
        boards.add("28-O3SLIU-1(SDH-1)");
        boards.add("28-MI6L89-1(SDH-1)");
        boards.add("32-UY9L17-1(SDH-1)");
        boards.add("28-N3IU67-1(SDH-1)");
        nodes.add("GaoTai");
        nodes.add("FeiHua-Metro1000");
        nodes.add("AnShan(ZhongXing)");
        nodes.add("EDouLi(Siemens)");
        nodes.add("ZhengZhouBian");
        nodes.add("AutomationRouter");
        nodes.add("FengHuo");
        nodes.add("HuBeiShengDiao(Siemens)");
        nodes.add(",HuBeiShengDiao-OSN7500");
        nodes.add("HuBei(HuaWei)");
        nodes.add("HeBei(HuaWei)");
        nodes.add("HeBei-ECI");
        nodes.add("SongShan-XDM500");
        nodes.add("XiaoGan(Siemens)");
        nodes.add("BeiDiao(ECI10G-I2-1)");
        nodes.add("BeiDiao-Siemens103-2");
        nodes.add("YiTuiYun-GuoDiao-Siemens");
        nodes.add("HuaBei-NEC WBM");
        nodes.add("HuaZhongWangDiao-Siemens");
        nodes.add("HuaZhongWangDiao-Marconi2");
        nodes.add("HuaZhongWangDiao-Marconi");
        time.add("2018-5-12-08:20:30");
        time.add("2018-5-12-08:25:23");
        time.add("2018-5-12-08:30:36");
        time.add("2018-5-12-08:35:33");
        time.add("2018-5-12-08:40:30");
        time.add("2018-5-12-08:41:30");
        time.add("2018-5-12-08:43:56");
        time.add("2018-5-12-08:47:24");
        time.add("2018-5-12-08:50:56");
        time.add("2018-5-12-08:55:30");
        time.add("2018-5-12-08:56:30");
        time.add("2018-5-12-08:59:30");
        time.add("2018-5-12-09:10:00");
        time.add("2018-5-12-09:13:10");
        time.add("2018-5-12-09:15:13");
        time.add("2018-5-12-09:19:45");
        time.add("2018-5-12-09:24:45");
        time.add("2018-5-12-09:29:43");
        time.add("2018-5-12-09:32:23");
        time.add("2018-5-12-09:35:48");
        time.add("2018-5-12-09:40:23");
        List<String> list3 = new ArrayList<>();
        for (int i = 0; i <list.size() ; i++) {
            if (i%5==0){
                Random random = new Random();
                int a = random.nextInt(2);
                list3.add(level.get(a));
            }else if (i%5==1){
                Random random = new Random();
                int a = random.nextInt(10);
                list3.add(names.get(a));
            }else if (i%5==2){
                Random random = new Random();
                int a = random.nextInt(21);
                list3.add(nodes.get(a));
            }else if (i%5==3){
                Random random = new Random();
                int a = random.nextInt(21);
                list3.add(boards.get(a));
            }else if (i%5==4){
                Random random = new Random();
                int a = random.nextInt(21);
                list3.add(time.get(a));
            }else
                log.info("Index out  of bound exception");
            }
        boolean first = true;
        StringBuilder result = new StringBuilder();
        for (String string : list3) {
            if(first){
                first = false;
            }else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
        }

        public static String edgeIdParse(double[] arr){
            List list = new ArrayList();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 1.0){
                    list.add((i+1));
                }
            }
            String edge = "";
            for (int j = 0; j < list.size(); j++) {
                String link = String.valueOf(list.get(j));
                edge = edge + link + "-";
            }
            return edge.substring(0,edge.length()-1);
        }
    }
