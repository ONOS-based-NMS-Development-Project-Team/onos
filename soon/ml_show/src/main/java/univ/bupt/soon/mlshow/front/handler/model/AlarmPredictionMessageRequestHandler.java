package univ.bupt.soon.mlshow.front.handler.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.front.MLMessageHandler;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;

/**
 * #soonEvent: alarmPredDataRequest
 * {
 * 	"event":"alarmPredDataRequest",
 * 	"payload":{
 * 	"firstCol":"time", 其实就是当前时间
 * 	"firstDir":"asc",
 * 	"secondCol":"alarmHappen",
 * 	"secondDir":"asc",
 * 	"setting":{
 * 		"modelId":"",
 * 		"recentItemNum":"",
 *        }
 * }
 * }
 *
 * #soonEvent: alarmPredDataResponse
 * {
 * 	"event":"alarmPredDataResponse",
 * 	"payload":{
 * 		"alarmPreds":[{
 * 			"time":"",   //应用的最近几条数据中时间最早的一个
 * 			"alarmHappen":"",
 * 			"modelId":"",
 * 			"trainDataSetId":"",
 * 			"testDataSetId":"",
 * 			"testAccuracy":[]
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 */

public class AlarmPredictionMessageRequestHandler extends UiMessageHandler {

    private static final String DATA_REQ = "alarmPredDataRequest";
    private static final String DATA_RESP = "alarmPredDataResponse";
    private static final String TABLES = "alarmPreds";

    private static final String DATA_APPLY_REQ = "alarmPredApplyRequest";
    private static final String DATA_APPLY_RESP = "alarmPredApplyResponse";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service = MLMessageHandler.modelServices.get(MLAppType.ALARM_PREDICTION);

    public Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new AlarmPredictionMessageRequestHandler.AlarmPredictionDataRequestHandler(),
                new AlarmPredictionMessageRequestHandler.AlarmPredictionApplyRequestHandler()
        );
    }

    private class AlarmPredictionDataRequestHandler extends TableRequestHandler {


        public AlarmPredictionDataRequestHandler() {
            super(DATA_REQ, DATA_RESP, TABLES);
        }

        @Override
        protected String[] getColumnIds() {
            String[] COLUMN_IDS = {INPUT_TYPE, ALARM_HAPPEN, MODEL_ID, TRAIN_DATASET_ID, TEST_DATASET_ID, MODEL_ACCURACY, INPUT};
            return COLUMN_IDS;
        }

        @Override
        protected String noRowsMessage(ObjectNode objectNode) {
            return NO_ROWS_MESSAGE;
        }

        @Override
        public void process(ObjectNode payload) {
            //排序参数解析
            String firstCol = JsonUtils.string(payload, FIRST_COL, INPUT_TYPE);
            String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
            String secondCol = JsonUtils.string(payload, SECOND_COL, ALARM_HAPPEN);
            String secondDir = JsonUtils.string(payload, SECOND_DIR, ASC);

            //装填数据
            TableModel tm = createTableModel();
            this.populateTable(tm, payload);

            //排序并推送数据
            tm.sort(firstCol, sortDir(firstDir), secondCol, sortDir(secondDir));
            this.addTableConfigAnnotations(tm, payload);
            ObjectNode rootNode = MAPPER.createObjectNode();
            rootNode.set(TABLES, TableUtils.generateRowArrayNode(tm));
            rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
            this.sendMessage(DATA_RESP, rootNode);
        }

        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            // TODO
            Set<Integer> modelIdSet = ModelLibraryMessageHandler.modelResultMap.keySet();
            for (int i : modelIdSet) {
                if (ModelLibraryMessageHandler.modelLibraryInfoMap.get(i).getMlAppType() == MLAppType.ALARM_PREDICTION) {
                    Set<Integer> msgIdSet = ModelLibraryMessageHandler.modelResultMap.get(i).keySet();
                    for (int j : msgIdSet) {
                        Pair<List<Item>, List<String>> pair = (Pair<List<Item>, List<String>>) ModelLibraryMessageHandler.modelResultMap.get(i).get(j);
                        List<Item> input = pair.getLeft();
                        List<String> result = pair.getRight();

                        ModelLibraryInfo model = ModelLibraryMessageHandler.modelLibraryInfoMap.get(i);
                        for (int a = 0; a < input.size(); a++) {
                            AlarmPredictionItem it = (AlarmPredictionItem) input.get(a);
                            tm.addRow()
                                    .cell(INPUT_TYPE, it.getInput_type())
                                    .cell(ALARM_HAPPEN, result.get(a))
                                    .cell(MODEL_ID, i)
                                    .cell(TRAIN_ID, model.getMlModelDetail().getTrainDatasetId())
                                    .cell(TEST_ID, listToString(model.getTestDataSetId()))
                                    .cell(MODEL_ACCURACY, getStringEvaluate(model.getMlModelDetail().getPerformances()))
                                    .cell(INPUT, arrayToString(it.getInput()));
                        }
                    }
                }
            }
        }
    }

        private class AlarmPredictionApplyRequestHandler extends RequestHandler {
            private AlarmPredictionApplyRequestHandler() {
                super(DATA_APPLY_REQ);
            }

            int modelId;
            int msgId;
            int recentItemNum;
            Map<Integer, Object> msgMap = new ConcurrentHashMap<>();

            @Override
            public void process(ObjectNode payload) {

                modelId = (int) JsonUtils.number(payload, MODEL_ID);
                recentItemNum = (int) JsonUtils.number(payload, RECENT_ITEM_NUM);

                Pair<Boolean, Integer> applyPair = service.applyModel(modelId, recentItemNum);
                if (applyPair.getLeft()) {
                    msgId = applyPair.getRight();
                    msgMap.put(msgId, new Object());
                    ModelLibraryMessageHandler.modelResultMap.put(modelId, msgMap);
                } else {
                    ConcurrentHashMap<String, String> javaMap = new ConcurrentHashMap<>();
                    javaMap.put("apply", "fail apply model" + modelId + "in java");
                    ModelLibraryMessageHandler.modelManagementMapJava.put(modelId, javaMap);
                }
            }
        }

}