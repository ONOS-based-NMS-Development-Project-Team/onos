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
import org.onosproject.soon.MLAppType;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;

import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.*;
import org.onosproject.soon.mlmodel.MLModelConfig;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.text.SimpleDateFormat;

import static org.onosproject.soon.mlmodel.MLAlgorithmType.FCNNModel;

/**
 * Skeletal ONOS UI Table-View message handler.
 */
public class AlarmPredMessageHandler extends UiMessageHandler {

    private static final String ALARM_PRED_TRAIN_DATA_REQ = "alarmPredTrainDataRequest";
    private static final String ALARM_PRED_TRAIN_DATA_RESP = "alarmPredTrainDataResponse";
    private static final String ALARM_PRED_TRAIN_TABLES = "alarmPredTrains";

    private static final String ALARM_PRED_TEST_DATA_REQ = "alarmPredTestDataRequest";
    private static final String ALARM_PRED_TEST_DATA_RESP = "alarmPredTestDataResponse";
    private static final String ALARM_PRED_TEST_TABLES = "alarmPredTests";

    private static final String ALARM_PRED_DATA_REQ = "alarmPredDataRequest";
    private static final String ALARM_PRED_DATA_RESP = "alarmPredDataResponse";
    private static final String ALARM_PRED_TABLES = "alarmPreds";

    private static final String NO_ALARM_PRED_TRAIN_DATA_ROWS_MESSAGE = "No AlarmPredictionItem found";
    private static final String NO_ALARM_PRED_DATA_ROWS_MESSAGE = "No AlarmPredictionApplicationItem found";

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

    private static final String[] ALARM_PRED_TRAIN_DATA_COLUMN_IDS = {
            ID, INPUT, ALARM_HAPPEN, TRAIN, DATAID};

    private static final String[] ALARM_PRED_DATA_COLUMN_IDS = {
            ALARM_SOURCE, LEVEL, NAME, TIME_OCCUR, TIME_FIRST_OCCUR};

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new AlarmHistoricalDetailRequestHandler(),
                new AlarmHistoricalDataRequestHandler(),
                new AlarmPredTrainDataRequestHandler(),
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
            //String firstCol = JsonUtils.string(payload, FIRST_COL, defaultColumnId());
            //String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
            //String secondCol = JsonUtils.string(payload, SECOND_COL, null);
            //String secondDir = JsonUtils.string(payload, SECOND_DIR, null);
            //tm.sort(firstCol, sortDir(firstDir), secondCol, sortDir(secondDir));
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
            List<org.onosproject.soon.dataset.original.Item> its = SoonUiComponent.modelServices.get(MLAppType.ORIGINAL_DATA).updateData(offset,limit);
            for (org.onosproject.soon.dataset.original.Item it : its) {
                HistoryAlarmItem tmp = (HistoryAlarmItem) it;
                populateRow(tm.addRow(), tmp);
            }
            offset += limit;
        }

        private void populateRow(TableModel.Row row, HistoryAlarmItem item) {
            row.cell(LEVEL, item.getLevel())
                    .cell(NAME, item.getName())
                    .cell(ALARM_SRC, item.getAlarm_src())
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
                data.put(NAME, it.getName());
                data.put(ALARM_SRC, it.getAlarm_src());
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

    // handler for alarmPred model application table requests
    private final class AlarmPredDataRequestHandler extends TableRequestHandler {

        private static final String NO_ROWS_MESSAGE = "No AlarmPredictionApplication found";

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
	    TableModel tm = createTableModel();
	    this.populateTable(tm, payload);
	    //String firstCol = JsonUtils.string(payload, FIRST_COL, defaultColumnId());
	    //String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
	    //String secondCol = JsonUtils.string(payload, SECOND_COL, null);
	    //String secondDir = JsonUtils.string(payload, SECOND_DIR, null);
	    //tm.sort(firstCol, sortDir(firstDir), secondCol, sortDir(secondDir));
	    this.addTableConfigAnnotations(tm, payload);
	    ObjectNode rootNode = MAPPER.createObjectNode();
	    rootNode.set(ALARM_PRED_TABLES, TableUtils.generateRowArrayNode(tm));
	    rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
	    this.sendMessage(ALARM_PRED_DATA_RESP, rootNode);
        }


        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            int id = 1;
            MLModelConfig item = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).getModelConfig(FCNNModel, id,null);
//            List<Item> items = getItems();
//            for (Item item: items) {
                populateRow(tm.addRow(), item);
//            }
        }

        private void populateRow(TableModel.Row row, MLModelConfig item) {
            row.cell(ALARM_SOURCE, item.alarmSource())
                    .cell(LEVEL, item.level())
                    .cell(NAME, item.name())
		            .cell(TIME_OCCUR, item.timeOccur())
                    .cell(TIME_FIRST_OCCUR, item.timeFirstOccur());
        }
    }



/**
    // handler for sample item details requests
    private final class AlarmPreDetailRequestHandler extends RequestHandler {

        private AlarmPreDetailRequestHandler() {
            super(ALARM_PRE_DETAIL_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            String id = string(payload, ID, "(none)");

            // SomeService ss = get(SomeService.class);
            // Item item = ss.getItemDetails(id)

            // fake data for demonstration purposes...
            Item item = getItem(id);

            ObjectNode rootNode = objectNode();
            ObjectNode data = objectNode();
            rootNode.set(DETAILS, data);

            if (item == null) {
                rootNode.put(RESULT, "Item with id '" + id + "' not found");
                log.warn("attempted to get item detail for id '{}'", id);

            } else {
                rootNode.put(RESULT, "Found item with id '" + id + "'");

                data.put(ALARM_SOURCE, "a");//should 
            	data.put(LEVEL, 1);
            	data.put(NAME, "panzhihua");
		        data.put(NUMBER_OCCUR, 5);
	            data.put(FIRST_OCCUR_TIME, "2018-05-21/08:25:00");
		        data.put(PROBABILITY_OCCUR, 0.56);

                //data.put(ID, item.id());
                //data.put(LABEL, item.label());
                //data.put(CODE, item.code());
                //data.put(COMMENT, "Some arbitrary comment");
            }

            sendMessage(ALARM_PRE_DETAIL_RESP, rootNode);
        }
    }
*/



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

//    static int offset=0;
//    static int limit = 10;
//    // Produce a list of items.
//    private static List<Item> getItems() {
//        List<Item> rtn = Lists.newArrayList();
//        List<org.onosproject.soon.dataset.original.Item> its = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).updateData(offset,limit);
//        for (org.onosproject.soon.dataset.original.Item it : its) {
//            AlarmPredictionItem tmp = (AlarmPredictionItem) it;
//            double[] d = tmp.getInput();
//            rtn.add(new Item(String.valueOf(d[0]), (int)d[1], String.valueOf(d[2]), String.valueOf(d[3]), String.valueOf(d[4])));
//        }
//        offset += limit;
//        return rtn;
//    }
/**
    // Simple model class to provide sample data
    private static class Item {
        private final String alarmSource;
	    private final int level;
        private final String name;
        private final String timeOccur;
	    private final String timeFirstOccur;

        Item(String alarmSource, int level, String name, String timeOccur, String timeFirstOccur) {
            this.alarmSource = alarmSource;
            this.level = level;
            this.name = name;
            this.timeOccur = timeOccur;
            this.timeFirstOccur = timeFirstOccur;
        }

        String alarmSource() { return alarmSource; }
	    int level() { return level; }
        String name() { return name; }
        String timeOccur() { return timeOccur; }
	    String timeFirstOccur() { return timeFirstOccur; }
    }
*/
}
