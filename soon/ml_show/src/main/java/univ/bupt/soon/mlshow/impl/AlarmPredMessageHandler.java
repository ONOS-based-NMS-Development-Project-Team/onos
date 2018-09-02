/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package univ.bupt.soon.mlshow.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;

import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.*;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
//import org.onosproject.soon.mlmodel.MLModelConfig;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.text.SimpleDateFormat;

import static org.onosproject.soon.mlmodel.MLAlgorithmType.FCNNModel;
import static org.onosproject.ui.table.TableModel.sortDir;

/**
 * Skeletal ONOS UI Table-View message handler.
 */
public class AlarmPredMessageHandler extends UiMessageHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String ID = "id";
    private static final String INPUT = "input";
    private static final String ALARM_HAPPEN = "alarm_happen";
    private static final String TRAIN = "train";
    private static final String DATAID = "dataid";

    private static final String ALARM_SOURCE = "alarmSource";
    private static final String LEVEL = "level";
    private static final String NAME = "name";
    private static final String TIME_OCCUR = "timeOccur";
    private static final String TIME_FIRST_OCCUR = "timeFirstOccur";

    private static final String ANNOTS = "annots";
    private static final String RESULT = "result";

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new AlarmHistoricalDetailRequestHandler(),
                new AlarmHistoricalDataRequestHandler(),
//                new AlarmPredTrainDataRequestHandler(),
                new AlarmPredDataRequestHandler()
        );
    }

/************************************* net dataset *************************************************/

/************************ alarmHistoricalData of net dataset *********************************/
    private static final String ALARM_HIST_DATA_REQ = "alarmHistoricalDataRequest";
    private static final String ALARM_HIST_DATA_RESP = "alarmHistoricalDataResponse";
    private static final String ALARM_HIST_TABLES = "alarmHistoricals";

    private static final String ALARM_HIST_DETAILS_REQ = "alarmHistoricalDetailsRequest";
    private static final String ALARM_HIST_DETAILS_RESP = "alarmHistoricalDetailsResponse";
    private static final String ALARM_HIST_DETAILS_TABLES = "alarmHistoricalDetails";

//    private static final String LEVEL = "level";
//    private static final String NAME = "name";
    private static final String ALARM_SRC = "alarm_src";
    private static final String TP = "tp";
    private static final String LOCATION = "location";
    private static final String HAPPEN_TIME = "happen_time";
    private static final String CLEAN_TIME = "clean_time";
    private static final String CONFIRM_TIME = "confirm_time";
    private static final String PATH_LEVEL = "path_level";

    private static final String FIRST_COL = "firstCol";
    private static final String FIRST_DIR = "firstDir";
    private static final String SECOND_COL = "secondCol";
    private static final String SECOND_DIR = "secondDir";

    private static final String ASC = "asc";

    // handler for AlarmHistorical data requests
    private final class AlarmHistoricalDataRequestHandler extends TableRequestHandler {
        private static final String NO_ROWS_MESSAGE = "No applications found";
        private final String[] COLUMN_IDS = {LEVEL, NAME, ALARM_SRC, TP, LOCATION, HAPPEN_TIME};
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private AlarmHistoricalDataRequestHandler() {
            super(ALARM_HIST_DATA_REQ, ALARM_HIST_DATA_RESP, ALARM_HIST_TABLES);
        }

        // if necessary, override defaultColumnId() -- if it isn't "id"
        @Override
        protected String[] getColumnIds() {
            return COLUMN_IDS;
        }

        // if required, override createTableModel() to set column formatters / comparators
        @Override
        protected String noRowsMessage(ObjectNode payload) {
            return NO_ROWS_MESSAGE;
        }

        @Override
        public void process(ObjectNode payload) {
            TableModel tm = createTableModel();
            this.populateTable(tm, payload);
            String firstCol = JsonUtils.string(payload, FIRST_COL, LEVEL);
            String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
            String secondCol = JsonUtils.string(payload, SECOND_COL, ALARM_SRC);
            String secondDir = JsonUtils.string(payload, SECOND_DIR, ASC);
            tm.sort(firstCol, sortDir(firstDir), secondCol, sortDir(secondDir));
            this.addTableConfigAnnotations(tm, payload);
            ObjectNode rootNode = MAPPER.createObjectNode();
            rootNode.set(ALARM_HIST_TABLES, TableUtils.generateRowArrayNode(tm));
            rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
            this.sendMessage(ALARM_HIST_DATA_RESP, rootNode);
        }

        int offset = 0;
        int limit = 10;

        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
//            List<org.onosproject.soon.dataset.original.Item> its = SoonUiComponent.modelServices.get(MLAppType.ORIGINAL_DATA).updateData(offset,limit);
            List<org.onosproject.soon.dataset.original.sdhnet.HistoryAlarmItem> its = new ArrayList<>();
            HistoryAlarmItem a = new HistoryAlarmItem();
            HistoryAlarmItem b = new HistoryAlarmItem();
            a.setLevel("a");
            a.setName("aa");
            a.setAlarm_src("aaa");
            a.setTp("aaaa");
            a.setLocation("aaaaa");
            a.setHappen_time(new Date());
            a.setHappen_time(new Date());
            a.setHappen_time(new Date());
            b.setPath_level("aaaaaa");
            b.setLevel("b");
            b.setName("bb");
            b.setAlarm_src("bbb");
            b.setTp("bbbb");
            b.setLocation("bbbbb");
            b.setHappen_time(new Date());
            b.setHappen_time(new Date());
            b.setHappen_time(new Date());
            b.setPath_level("bbbbbb");
            its.add(a);
            its.add(b);
            for (HistoryAlarmItem it : its) {
                HistoryAlarmItem tmp = (HistoryAlarmItem) it;
                populateRow(tm.addRow(), tmp);
            }
            offset += limit;
        }

//        private void populateRow(TableModel.Row row, Item item) {
        private void populateRow(TableModel.Row row, HistoryAlarmItem item) {
            row.cell(LEVEL, item.getLevel())
                    .cell(ALARM_SRC, item.getAlarm_src())
                    .cell(NAME, item.getName())
                    .cell(TP, item.getTp())
                    .cell(LOCATION, item.getLocation())
                    .cell(HAPPEN_TIME, formatter.format(item.getHappen_time()));
        }
    }

    // handler for AlarmHistorical details requests
    private final class AlarmHistoricalDetailRequestHandler extends RequestHandler {

        private AlarmHistoricalDetailRequestHandler() {
            super(ALARM_HIST_DETAILS_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            int id = Integer.valueOf(string(payload, ID)).intValue();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //当offset==limit时，取一条数据，所以只得到一条数据
            Item item = SoonUiComponent.modelServices.get(MLAppType.ORIGINAL_DATA).updateData(id,id).get(0);
            HistoryAlarmItem it = (HistoryAlarmItem) item;

            ObjectNode rootNode = objectNode();
            ObjectNode data = objectNode();
            rootNode.set(ALARM_HIST_DETAILS_TABLES, data);

            if (item == null) {
                rootNode.put(RESULT, "Item with id '" + id + "' not found");
                log.warn("attempted to get item detail for id '{}'", id);

            } else {
                rootNode.put(RESULT, "Found item with id '" + id + "'");

                data.put(LEVEL, it.getLevel());
                data.put(ALARM_SRC, it.getAlarm_src());
                data.put(NAME, it.getName());

                data.put(TP, it.getTp());
                data.put(LOCATION, it.getLocation());
                data.put(HAPPEN_TIME, formatter.format(it.getHappen_time()));
                data.put(CLEAN_TIME, formatter.format(it.getClean_time()));
                data.put(CONFIRM_TIME, formatter.format(it.getConfirm_time()));
                data.put(PATH_LEVEL, it.getPath_level());
        }

            sendMessage(ALARM_HIST_DETAILS_RESP, rootNode);
        }
    }


/************************* processed dataset for training or testing *****************************************/



/************************************** fault classifiction app *************************************************/



/************************************** alarm prediction app *************************************************/

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
    private static final String APPLIED_RESULT = "alarmHappen";
    private static final String TIME = "time";
    private static final String MODEL_ID = "modelId";

// todo model config info
// private static final String[] ALARM_PRED_DATA_COLUMN_IDS = {
//            INPUT_NUM, OUTPUT_NUM, HIDDEN_LAYER, ACTIVATION_FUNCTION, WEIGHT_INIT, BIAS_INIT,
//            LOSS_FUNCTION, BATCH_SIZE, EPOCH, OPTIMIZER, LEARNING_RATE, LR_ADJUST, DROP_OUT};
    private static final String[] ALARM_PRED_DATA_COLUMN_IDS = {TIME,APPLIED_RESULT,MODEL_ID};

    // handler for alarmPred model application table requests
    private final class AlarmPredDataRequestHandler extends TableRequestHandler implements ForegroundCallback {

        private static final String NO_ROWS_MESSAGE = "No AlarmPredictionApplication found";

        Map<Integer, Map<Integer,List<String>>> APModelIdMsgIdValue = new HashMap<>();

        private AlarmPredDataRequestHandler() {
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
//            TableModel tm = createTableModel();
//            this.populateTable(tm, payload);
            int msgid = 2;
            //处理payload，得到里面的sortParams和setting
                ObjectNode setting = (ObjectNode) payload.path("setting");
            //读取setting里的modelId，然后应用模型，获得结果
            String modelId = JsonUtils.string(setting, "modelId", null);
            String recentItemNum = JsonUtils.string(setting, "recentItemNum", null);
            int modelIdInt = Integer.valueOf(modelId).intValue();
                int recentItemNumInt = Integer.valueOf(recentItemNum).intValue();
            //如果map中包含
            if(APModelIdMsgIdValue.containsKey(modelId) == false){
                    APModelIdMsgIdValue.put(modelIdInt, new HashMap<>());
                //此处调用应用结果方法，pair.left为是否调用成功，pair.right为该操作的msgId
                //Pair<Boolean, Integer> pair = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).applyModel(modelIdInt, recentItemNumInt);
                Pair<Boolean, Integer> pair = Pair.of(true,2);
                msgid = pair.getRight();
                if(pair.getKey() == true){
                    APModelIdMsgIdValue.get(modelIdInt).put(msgid,new ArrayList<>());
                }else{
                    //调用应用结果方法，返回值.left为false，说明调用方法没有成功
                    log.info("在告警预测app中，调用方法applyModel()失败");
                    //todo 获取不到modelId
                    //删除map中的数据，前台刷新时，会重新发送请求
                    APModelIdMsgIdValue.remove(modelIdInt);
                }
            }
            //todo 为了测试，延时5秒后在 APModelIdMsgIdValue.get(modelIdInt).get(msgid) 赋值

            APModelIdMsgIdValue.get(modelIdInt).get(msgid).add("abcdefg");

            if( APModelIdMsgIdValue.get(modelIdInt).get(msgid) != null){
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

    // ===================================================================
    // NOTE: The code below this line is to create fake data for this
    //       sample code. Normally you would use existing services to
    //       provide real data.

    // Lookup a single item.
//    private static Item getItem(String alarmSource) {
//        // We realize this code is really inefficient, but
//        // it suffices for our purposes of demonstration...
//        for (Item item : getItems()) {
//            if (item.alarmSource().equals(alarmSource)) {
//                return item;
//            }
//        }
//        return null;
//    }

    static int offset=0;
    static int limit = 10;
    // Produce a list of items.
    private static List<Item> getItems() {
        List<Item> rtn = Lists.newArrayList();
        List<org.onosproject.soon.dataset.original.Item> its = SoonUiComponent.modelServices.get(MLAppType.ORIGINAL_DATA).updateData(offset,limit);
        for (org.onosproject.soon.dataset.original.Item it : its) {
//            HistoryAlarmItem tmp = (HistoryAlarmItem) it;
//            double[] d = tmp.getInput();
//            rtn.add(new HistoryAlarmItem(tmp.getLevel(), tmp.getName(), tmp.getAlarm_src(),
//                    tmp.getTp(), tmp.getLocation(),tmp.getHappen_time(),tmp.getClean_time(),
//                    tmp.getConfirm_time(),tmp.getPath_level()));
            rtn.add(it);
        }
        offset += limit;
        return rtn;
    }

}
