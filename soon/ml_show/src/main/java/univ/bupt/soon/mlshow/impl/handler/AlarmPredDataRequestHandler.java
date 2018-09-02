package univ.bupt.soon.mlshow.impl.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.impl.SoonUiComponent;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.onosproject.ui.table.TableModel.sortDir;

/**
 * handler for alarmPred model application table requests
 */
public class AlarmPredDataRequestHandler extends TableRequestHandler implements ForegroundCallback {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String ALARM_PRED_DATA_REQ = "alarmPredDataRequest";
    private static final String ALARM_PRED_DATA_RESP = "alarmPredDataResponse";
    private static final String ALARM_PRED_TABLES = "alarmPreds";

    private static final String INPUT_NUM = "inputNum";
    private static final String OUTPUT_NUM = "outputNum";
    private static final String HIDDEN_LAYER = "hiddenLayer";
    private static final String ACTIVATION_FUNCTION = "activationFunction";
    private static final String WEIGHT_INIT = "weightInit";
    private static final String BIAS_INIT = "biasInit";
    private static final String LOSS_FUNCTION = "lossFunction";
    private static final String BATCH_SIZE = "batchSize";
    private static final String EPOCH = "epoch";
    private static final String OPTIMIZER = "optimizer";
    private static final String LEARNING_RATE = "learningRate";
    private static final String LR_ADJUST = "lrAdjust";
    private static final String DROP_OUT = "dropout";
    private static final String APPLIED_RESULT = "AppliedResult";
    private static final String TIME = "time";
    private static final String MODEL_ID = "modelId";
    private static final String NO_ROWS_MESSAGE = "No AlarmPredictionApplication found";
    private static final String[] ALARM_PRED_DATA_COLUMN_IDS = {APPLIED_RESULT};

    private Map<Integer, Map<Integer,List<String>>> APModelIdMsgIdValue = new HashMap<>();

    public AlarmPredDataRequestHandler() {
        super(ALARM_PRED_DATA_REQ, ALARM_PRED_DATA_RESP, ALARM_PRED_TABLES);
    }

    // if necessary, override defaultColumnId() -- if it isn't "id"
    @Override
    protected String[] getColumnIds() {
        return ALARM_PRED_DATA_COLUMN_IDS;
    }

    // if required, override createTableModel() to set column formatters / comparators
    @Override
    protected String noRowsMessage(ObjectNode payload) {
        return NO_ROWS_MESSAGE;
    }

    @Override
    public void process(ObjectNode payload) {
        int msgid = 1;
        //处理payload，得到里面的sortParams和setting
        ObjectNode setting = (ObjectNode) payload.path("setting");
        //读取setting里的modelId，然后应用模型，获得结果
        String modelId = JsonUtils.string(setting, "modelId", null);
        String recentItemNum = JsonUtils.string(setting, "recentItemNum", null);
        int modelIdInt = Integer.parseInt(modelId);
        int recentItemNumInt = Integer.parseInt(recentItemNum);
        //如果map中包含
        if(!APModelIdMsgIdValue.containsKey(modelIdInt)){
            APModelIdMsgIdValue.put(modelIdInt, new HashMap<>());
            //此处调用应用结果方法，pair.left为是否调用成功，pair.right为该操作的msgId
            Pair<Boolean, Integer> pair = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).applyModel(modelIdInt, recentItemNumInt);
            msgid = pair.getRight();
            if(pair.getKey()){
                APModelIdMsgIdValue.get(modelIdInt).put(msgid, null);
            }else{
                //调用应用结果方法，返回值.left为false，说明调用方法没有成功
                log.info("在告警预测app中，调用方法applyModel()失败");
                //todo 获取不到modelId
                //删除map中的数据，前台刷新时，会重新发送请求
                APModelIdMsgIdValue.remove(modelIdInt);
            }
        }

        if (APModelIdMsgIdValue.get(modelIdInt).get(msgid) != null) {
            List<String> applyResult = APModelIdMsgIdValue.get(modelIdInt).get(msgid);

            TableModel tm = createTableModel();
            for (String it: applyResult) {
                populateRow(tm.addRow(), it, modelIdInt);
            }
            String firstCol = JsonUtils.string(payload, FIRST_COL, defaultColumnId());
            String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
            String secondCol = JsonUtils.string(payload, SECOND_COL, null);
            String secondDir = JsonUtils.string(payload, SECOND_DIR, null);
            tm.sort(firstCol, sortDir(firstDir), secondCol, sortDir(secondDir));
            this.addTableConfigAnnotations(tm, payload);
            ObjectNode rootNode = MAPPER.createObjectNode();
            rootNode.set(ALARM_PRED_TABLES, TableUtils.generateRowArrayNode(tm));
            rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
            APModelIdMsgIdValue.remove(modelIdInt);
            this.sendMessage(ALARM_PRED_DATA_RESP, rootNode);
        }

    }


    @Override
    protected void populateTable(TableModel tm, ObjectNode payload) {
        //default 情况下，获得默认的结果
//            int id = 0;
//            int trainDatasetId = 0;
//            int recentItemNum = 3;
//            List<String> item = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).getAppliedResult(FCNNModel, id, trainDatasetId, recentItemNum);
//            List<String> item = new ArrayList<>();
//            item.add("1,2,3");
//            item.add("a,b,c");
//

//            int id = 1;
//TODO MODEL CONFIGURATION INFO
//            MLAlgorithmConfig item = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).getModelConfig(FCNNModel, id,null);
//
//            NNAlgorithmConfig it = (NNAlgorithmConfig)item;
//            //            List<Item> items = getItems();
////            for (Item item: items) {
//                populateRow(tm.addRow(), it);
////            }
    }

    private void populateRow(TableModel.Row row, String it, int modelIdInt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        row.cell(TIME, dateFormat.format(new Date()))
                .cell(APPLIED_RESULT, it)
                .cell(MODEL_ID, modelIdInt);
//                    .cell(OUTPUT_NUM, item.getOutputNum())
//                    .cell(HIDDEN_LAYER, item.getHiddenLayer())
//                    .cell(ACTIVATION_FUNCTION, item.getActivationFunction())
//                    .cell(WEIGHT_INIT, item.getWeightInit())
//                    .cell(BIAS_INIT, item.getBiasInit())
//                    .cell(LOSS_FUNCTION, item.getLossFunction())
//                    .cell(BATCH_SIZE, item.getBatchSize())
//                    .cell(EPOCH, item.getEpoch())
//                    .cell(OPTIMIZER, item.getOptimizer())
//                    .cell(LEARNING_RATE, item.getLearningRate())
//                    .cell(LR_ADJUST, item.getLrAdjust())
//                    .cell(DROP_OUT, item.getDropout());
    }

    @Override
    public void operationFailure(int msgId, String description) {

    }

    @Override
    public void appliedModelResult(int msgId, List<Item> input, List<String> output) {
        //TODO 将输出的结果赋值，找不到modelId
//            APModelIdMsgIdValue.get()put(msgId, output);


    }

    @Override
    public void modelEvaluation(int msgId, String result) {

    }

    @Override
    public void trainDatasetTransEnd(int msgId, int trainDatasetId) {

    }

    @Override
    public void testDatasetTransEnd(int msgId, int testDatasetId) {

    }

    @Override
    public void ResultUrl(int msgId, URI uri) {

    }

    @Override
    public void trainEnd(int msgId) {

    }

    @Override
    public void intermediateResult(int msgId, MonitorData monitorData) {

    }
}
