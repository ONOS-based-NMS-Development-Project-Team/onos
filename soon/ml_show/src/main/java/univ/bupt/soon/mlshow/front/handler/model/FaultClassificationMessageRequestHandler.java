package univ.bupt.soon.mlshow.front.handler.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;
import static univ.bupt.soon.mlshow.front.Utils.INPUT;
import static univ.bupt.soon.mlshow.front.Utils.getStringEvaluate;

public class FaultClassificationMessageRequestHandler extends UiMessageHandler {
    private static final String DATA_REQ = "faultClassificationDataRequest";
    private static final String DATA_RESP = "faultClassificationDataResponse";
    private static final String TABLES = "faultClassifications";

    private static final String DATA_APPLY_REQ = "faultClassificationApplyRequest";
    private static final String DATA_APPLY_RESP = "faultClassificationApplyResponse";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service = MLMessageHandler.modelServices.get(MLAppType.FAILURE_CLASSIFICATION);

    public void setService(ModelControlService service) {
        this.service = service;
    }

    public Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new FaultClassificationMessageRequestHandler.FaultClassificationApplyRequestHandler(),
                new FaultClassificationMessageRequestHandler.FaultClassificationDataRequestHandler()
        );
    }

    private class FaultClassificationDataRequestHandler extends TableRequestHandler {

        public FaultClassificationDataRequestHandler() {
            super(DATA_REQ, DATA_RESP, TABLES);
        }

        @Override
        protected String[] getColumnIds() {
            String[] COLUMN_IDS = {TIME, FAULT_TYPE, MODEL_ID, TRAIN_DATASET_ID, TEST_DATASET_ID, MODEL_ACCURACY, INPUT};
            return COLUMN_IDS;
        }

        @Override
        protected String noRowsMessage(ObjectNode objectNode) {
            return NO_ROWS_MESSAGE;
        }

        @Override
        public void process(ObjectNode payload) {
            //排序参数解析
            String firstCol = JsonUtils.string(payload, FIRST_COL, TIME);
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
                if (ModelLibraryMessageHandler.modelLibraryInfoMap.get(i).getMlAppType() == MLAppType.FAILURE_CLASSIFICATION) {
                    Set<Integer> msgIdSet = ModelLibraryMessageHandler.modelResultMap.get(i).keySet();
                    for (int j : msgIdSet) {
                        Pair<List<Item>, List<String>> pair = (Pair<List<Item>, List<String>>) ModelLibraryMessageHandler.modelResultMap.get(i).get(j);
                        List<Item> input = pair.getLeft();
                        List<String> result = pair.getRight();

                        ModelLibraryInfo model = ModelLibraryMessageHandler.modelLibraryInfoMap.get(i);
                        for (int a = 0; a < input.size(); a++) {
                            FailureClassificationItem it = (FailureClassificationItem) input.get(a);
                            tm.addRow()
<<<<<<< HEAD
                                    .cell(TIME, it.getTime0())
                                    .cell(FAULT_TYPE, result.get(a))
                                    .cell(MODEL_ID, i)
                                    .cell(TRAIN_ID, model.getMlModelDetail().getTrainDatasetId())
                                    .cell(TEST_ID, listToString(model.getTestDataSetId()))
                                    .cell(MODEL_ACCURACY, getStringEvaluate(model.getMlModelDetail().getPerformances()))
                                    .cell(INPUT, itemToString(it));
=======
                                .cell(TIME, it.getTime0())
                                .cell(FAULT_TYPE, result.get(i))
                                .cell(MODEL_ID, i)
                                .cell(TRAIN_ID, model.getMlModelDetail().getTrainDatasetId())
                                .cell(TEST_ID, listToString(model.getTestDataSetId()))
                                .cell(MODEL_ACCURACY, getStringEvaluate(model.getMlModelDetail().getPerformances()))
                                .cell(INPUT, itemToString(it));
>>>>>>> 419a04e43292cde816bdb6e1685e167c23be0984
                        }
                    }
                }
            }
        }

        protected String itemToString(FailureClassificationItem item) {
            String s = item.getBoard0() + ","
                    + item.getLevel0() + ","
                    + item.getName0() + ","
                    + item.getNode0() + ","
                    + item.getBoard1() + ","
                    + item.getLevel1() + ","
                    + item.getName1() + ","
                    + item.getNode1() + ","
                    + item.getBoard2() + ","
                    + item.getLevel2() + ","
                    + item.getName2() + ","
                    + item.getNode2() + ","
                    + item.getBoard3() + ","
                    + item.getLevel3() + ","
                    + item.getName3() + ","
                    + item.getNode3() + ","
                    + item.getBoard4() + ","
                    + item.getLevel4() + ","
                    + item.getName4() + ","
                    + item.getNode4() + ","
                    + item.getBoard5() + ","
                    + item.getLevel5() + ","
                    + item.getName5() + ","
                    + item.getNode5() + ",";
            return s;
        }
    }

    private class FaultClassificationApplyRequestHandler extends RequestHandler {

        private FaultClassificationApplyRequestHandler () {super(DATA_APPLY_REQ);}

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
