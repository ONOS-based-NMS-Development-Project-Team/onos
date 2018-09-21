package univ.bupt.soon.mlshow.front.handler.dataset.original;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.PerformanceItem;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;


/**
 * #soonEvent: performanceDataRequest
 * {
 * 	"event":"performanceDataRequest",
 * 	"payload":{
 * 		"firstCol":"node",
 * 		"firstDir":"asc",
 * 		"secondCol":"board",
 * 		"secondDir":"asc",
 *        }
 * }
 * #soonEvent: performanceDataResponse
 * {
 * 	"event":"performanceDataResponse",
 * 	"payload":{
 * 		"performances":[{
 * 			"node":"",
 * 			"board":"",
 * 			"port":"",
 * 			"component":"",
 * 			"event":"",
 * 			"endTime":"",
 * 			"maxVal":"",
 * 			"curVal":"",
 * 			"minVal":""
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 */
public class PerformanceDataRequestHandler extends TableRequestHandler {

    private static final String DATA_REQ = "performanceDataRequest";
    private static final String DATA_RESP = "performanceDataResponse";
    private static final String TABLES = "performances";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service;
    // 数据库查询的时候定位查询数据的地方。返回数据在[offset, offset+limit)区间
    private int offset = 0;
    private int limit = 30;

    public PerformanceDataRequestHandler() {
        super(DATA_REQ, DATA_RESP, TABLES);
    }

    public void setService(ModelControlService service) {
        this.service = service;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    protected String[] getColumnIds() {
        String[] COLUMN_IDS = {NODE, BOARD, PORT, COMPONENT, EVENT, END_TIME, MAX_VAL, CUR_VAL, MIN_VAL};
        return COLUMN_IDS;
    }

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
        rootNode.set(TABLES, TableUtils.generateRowArrayNode(tm));
        rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
        this.sendMessage(DATA_RESP, rootNode);
    }



    @Override
    protected void populateTable(TableModel tm, ObjectNode payload) {
        List<Item> its = service.updateData(offset, limit);
        for (Item it : its) {
            populateRow(tm.addRow(), (PerformanceItem)it);
        }
        if (its.size() != limit) {
            offset = 0;
        }
        offset += limit;
    }

    private void populateRow(TableModel.Row row, PerformanceItem item) {
//        NODE, BOARD, PORT, COMPONENT, EVENT, END_TIME, MAX_VAL, CUR_VAL, MIN_VAL
        row.cell(NODE, item.getNode())
                .cell(BOARD, item.getBoard())
                .cell(PORT, item.getPort())
                .cell(COMPONENT, item.getComponent())
                .cell(EVENT, eventMap.get(item.getEvent()))
                .cell(END_TIME, formatter.format(item.getEnd_time()))
                .cell(MAX_VAL, item.getMax_val())
                .cell(CUR_VAL, item.getCur_val())
                .cell(MIN_VAL, item.getMin_val());
    }
}
