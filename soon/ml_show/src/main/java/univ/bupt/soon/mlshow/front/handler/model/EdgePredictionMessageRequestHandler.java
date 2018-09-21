package univ.bupt.soon.mlshow.front.handler.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.EdgePredictionItem;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;
import static univ.bupt.soon.mlshow.front.Utils.getStringEvaluate;

public class EdgePredictionMessageRequestHandler extends UiMessageHandler {

    private static final String DATA_REQ = "edgePredDataRequest";
    private static final String DATA_RESP = "edgePredDataResponse";
    private static final String TABLES = "edgePreds";

    private static final String DATA_APPLY_REQ = "edgePredApplyRequest";
    private static final String DATA_APPLY_RESP = "edgePredApplyResponse";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service = MLMessageHandler.modelServices.get(MLAppType.LINK_PREDICTION);

    public Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new EdgePredictionMessageRequestHandler.EdgePredictionApplyRequestHandler(),
                new EdgePredictionMessageRequestHandler.EdgePredictionDataRequestHandler()
        );
    }

    public class EdgePredictionDataRequestHandler extends TableRequestHandler {


        public EdgePredictionDataRequestHandler() {
            super(DATA_REQ, DATA_RESP, TABLES);
        }

        @Override
        protected String[] getColumnIds() {
            String[] COLUMN_IDS = {TIMEPOINT, EDGE_ID, MODEL_ID, ONE_HOUR_AFTER,
                    TWO_HOURS_BEFORE, TRAIN_DATASET_ID, TEST_DATASET_ID, MODEL_ACCURACY};
            return COLUMN_IDS;
        }

        @Override
        protected String noRowsMessage(ObjectNode objectNode) {
            return NO_ROWS_MESSAGE;
        }

        @Override
        public void process(ObjectNode payload) {
            //排序参数解析
            String firstCol = JsonUtils.string(payload, FIRST_COL, TIMEPOINT);
            String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
            String secondCol = JsonUtils.string(payload, SECOND_COL, EDGE_ID);
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
                if (ModelLibraryMessageHandler.modelLibraryInfoMap.get(i).getMlAppType() == MLAppType.LINK_PREDICTION) {
                    Set<Integer> msgIdSet = ModelLibraryMessageHandler.modelResultMap.get(i).keySet();
                    for (int j : msgIdSet) {
                        Pair<List<Item>, List<String>> pair = (Pair<List<Item>, List<String>>) ModelLibraryMessageHandler.modelResultMap.get(i).get(j);

                        List<Item> input = pair.getLeft();
                        List<String> result = pair.getRight();

                        ModelLibraryInfo model = ModelLibraryMessageHandler.modelLibraryInfoMap.get(i);
                        for (int a = 0; a < input.size(); a++) {
                            EdgePredictionItem it = (EdgePredictionItem) input.get(a);
                            tm.addRow()
                                    .cell(TIMEPOINT, it.getTimepoint())
                                    .cell(EDGE_ID, arrayToString(it.getEdge_id()))
                                    .cell(MODEL_ID, i)
                                    .cell(ONE_HOUR_AFTER, result.get(a))
                                    .cell(TWO_HOURS_BEFORE, arrayToString(it.getTwo_hours_before()))
                                    .cell(TRAIN_ID, model.getMlModelDetail().getTrainDatasetId())
                                    .cell(TEST_ID, listToString(model.getTestDataSetId()))
                                    .cell(MODEL_ACCURACY, getStringEvaluate(model.getMlModelDetail().getPerformances()));
                        }
                    }
                }
            }

        }
    }

    private class EdgePredictionApplyRequestHandler extends RequestHandler {

        private EdgePredictionApplyRequestHandler () {super(DATA_APPLY_REQ);}

        int modelId;
        int msgId;
        int recentItemNum;
        Map<Integer,Object> msgMap = new ConcurrentHashMap<>();

        @Override
        public void process (ObjectNode payload) {

            modelId = (int)JsonUtils.number(payload,MODEL_ID);
            recentItemNum = (int)JsonUtils.number(payload,RECENT_ITEM_NUM);

            Pair<Boolean, Integer> applyPair = service.applyModel(modelId, recentItemNum);
            if (applyPair.getLeft()) {
                msgId = applyPair.getRight();
                msgMap.put(msgId,new Object());
                ModelLibraryMessageHandler.modelResultMap.put(modelId,msgMap);
            } else {
                ConcurrentHashMap<String, String> javaMap = new ConcurrentHashMap<>();
                javaMap.put("apply", "fail apply model" + modelId + "in java");
                ModelLibraryMessageHandler.modelManagementMapJava.put(modelId, javaMap);
            }
        }
    }

}
