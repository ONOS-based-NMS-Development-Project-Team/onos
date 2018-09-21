package univ.bupt.soon.mlshow.front.handler.dataset.original;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.CurrentAlarmItem;
import org.onosproject.soon.dataset.original.sdhnet.HistoryAlarmItem;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.front.Utils;

import java.util.List;

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;

/**
 * #soonEvent: currentAlarmDataRequest
 * {
 * 	"event":"currentAlarmDataRequest",
 * 	"payload":{
 * 		"firstCol":"level",
 * 		"firstDir":"asc",
 * 		"secondCol":"alarmSource",
 * 		"secondDir":"asc",
 *        }
 * }
 * #soonEvent: currentAlarmDataResponse
 * {
 * 	"event":"currentAlarmDataResponse",
 * 	"payload":{
 * 		"currentAlarms":[{
 * 			"level":"",
 * 			"alarmSource":"",
 * 			"name":"",
 * 			"frequency":"",
 * 			"location":"",
 * 			"pathLevel":"",
 * 			"firstHappen":"",
 * 			"recentHappen":"",
 * 			"cleanTime":"",
 * 			"confirmTime":"",
 * 			"clean":"boolean",
 * 			"confirm":"boolean"
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 */
public class CurrentAlarmDataRequestHandler extends TableRequestHandler {
    private static final String DATA_REQ = "currentAlarmDataRequest";
    private static final String DATA_RESP = "currentAlarmDataResponse";
    private static final String TABLES = "currentAlarms";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service;
    // 数据库查询的时候定位查询数据的地方。返回数据在[offset, offset+limit)区间
    private int offset = 0;
    private int limit = 10;

    public CurrentAlarmDataRequestHandler() {
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
        String[] COLUMN_IDS = {LEVEL, ALARM_SOURCE, NAME,
                                FREQUENCY, LOCATION, PATH_LEVEL, FIRST_HAPPEN,
                                RECENT_HAPPEN, CLEAN_TIME, CONFIRM_TIME, CLEAN, CONFIRM};
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
            populateRow(tm.addRow(), (CurrentAlarmItem)it);
        }
        if (its.size() != limit) {
            offset = 0;
        }
        offset += limit;
    }

    private void populateRow(TableModel.Row row, CurrentAlarmItem item) {
//        LEVEL, ALARM_SOURCE, NAME,
//                FREQUENCY, LOCATION, PATH_LEVEL, FIRST_HAPPEN,
//                RECENT_HAPPEN, CLEAN_TIME, CONFIRM_TIME, CLEAN, CONFIRM
        row.cell(LEVEL, levelChToEn(item.getLevel()))
                .cell(ALARM_SOURCE, item.getAlarm_src())
                .cell(NAME, item.getName())
                .cell(FREQUENCY, item.getFrequency())
                .cell(LOCATION, item.getLocation())
                .cell(PATH_LEVEL, item.getPath_level())
                .cell(FIRST_HAPPEN, parseDate(item.getFist_happen()))
                .cell(RECENT_HAPPEN, parseDate(item.getRecent_happen()))
                .cell(CLEAN_TIME, parseDate(item.getClean_time()))
                .cell(CONFIRM_TIME, parseDate(item.getConfirm_time()))
                .cell(CLEAN, item.isClean())
                .cell(CONFIRM, item.isConfirm());
    }
}
