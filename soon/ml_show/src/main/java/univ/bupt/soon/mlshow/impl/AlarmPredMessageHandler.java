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

/**
 * Skeletal ONOS UI Table-View message handler.
 */
public class AlarmPredMessageHandler extends UiMessageHandler {

    private static final String ALARM_PRE_DATA_REQ = "alarmPredDataRequest";
    private static final String ALARM_PRE_DATA_RESP = "alarmPredDataResponse";
    private static final String SAMPLE_TABLES = "alarmPreds";

    private static final String ALARM_PRE_DETAIL_REQ = "alarmPreDetailsRequest";
    private static final String ALARM_PRE_DETAIL_RESP = "alarmPreDetailsResponse";
    private static final String DETAILS = "details";

    private static final String NO_ROWS_MESSAGE = "No items found";

    private static final String ALARM_SOURCE = "alarmSource";
    private static final String LEVEL = "level";
    private static final String NAME = "name";
    private static final String TIME_OCCUR = "timeOccur";
    private static final String TIME_FIRST_OCCUR = "timeFirstOccur";

    private static final String[] COLUMN_IDS = { ALARM_SOURCE, LEVEL, NAME, TIME_OCCUR, TIME_FIRST_OCCUR};

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new AlarmPredDataRequestHandler()
//                new AlarmPreDetailRequestHandler()
        );
    }

    // handler for sample table requests
    private final class AlarmPredDataRequestHandler extends TableRequestHandler {

        private AlarmPredDataRequestHandler() {
            super(ALARM_PRE_DATA_REQ, ALARM_PRE_DATA_RESP, SAMPLE_TABLES);
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
	    rootNode.set(SAMPLE_TABLES, TableUtils.generateRowArrayNode(tm));
	    rootNode.set("ANNOTS", TableUtils.generateAnnotObjectNode(tm));
	    this.sendMessage(ALARM_PRE_DATA_RESP, rootNode);
        }


        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            // === NOTE: the table model supplied here will have been created
            // via  a call to createTableModel(). To assign non-default
            // cell formatters or comparators to the table model, override
            // createTableModel() and set them there.

            // === retrieve table row items from some service...
            // SomeService ss = get(SomeService.class);
            // List<Item> items = ss.getItems()

            // fake data for demonstration purposes...
            List<Item> items = getItems();
            for (Item item: items) {
                populateRow(tm.addRow(), item);
            }
        }

        private void populateRow(TableModel.Row row, Item item) {
            row.cell(ALARM_SOURCE, item.alarmSource)
                    .cell(LEVEL, item.level)
                    .cell(NAME, item.name)
		    .cell(TIME_OCCUR, item.timeOccur) 
                    .cell(TIME_FIRST_OCCUR, item.timeFirstOccur);
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
    private static Item getItem(String alarmSource) {
        // We realize this code is really inefficient, but
        // it suffices for our purposes of demonstration...
        for (Item item : getItems()) {
            if (item.alarmSource().equals(alarmSource)) {
                return item;
            }
        }
        return null;
    }

    static int offset=0;
    static int limit = 10;
    // Produce a list of items.
    private static List<Item> getItems() {
        List<Item> rtn = Lists.newArrayList();
        List<org.onosproject.soon.dataset.original.Item> its = SoonUiComponent.modelServices.get(MLAppType.ALARM_PREDICTION).updateData(offset,limit);
        for (org.onosproject.soon.dataset.original.Item it : its) {
            AlarmPredictionItem tmp = (AlarmPredictionItem) it;
            double[] d = tmp.getInput();
            rtn.add(new Item(String.valueOf(d[0]), (int)d[1], String.valueOf(d[2]), String.valueOf(d[3]), String.valueOf(d[4])));
        }
        offset += limit;
        return rtn;
    }

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

}
