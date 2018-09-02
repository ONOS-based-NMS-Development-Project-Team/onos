package univ.bupt.soon.mlshow.front.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.HistoryAlarmItem;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static univ.bupt.soon.mlshow.front.Utils.*;

import static org.onosproject.ui.table.TableModel.sortDir;

/**
 * handler for AlarmHistorical data requests
  */
public class AlarmHistoricalDataRequestHandler extends TableRequestHandler {

    private static final String ALARM_HIST_DATA_REQ = "historicalAlarmDataRequest";
    private static final String ALARM_HIST_DATA_RESP = "historicalAlarmDataResponse";
    private static final String ALARM_HIST_TABLES = "historicalAlarms";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service;
    // 数据库查询的时候定位查询数据的地方。返回数据在[offset, offset+limit)区间
    static int offset = 0;
    static int limit = 10;

    public AlarmHistoricalDataRequestHandler() {
        super(ALARM_HIST_DATA_REQ, ALARM_HIST_DATA_RESP, ALARM_HIST_TABLES);
    }

    public void setService(ModelControlService service) {
        this.service = service;
    }

    @Override
    protected String[] getColumnIds() {
        String[] COLUMN_IDS = {LEVEL, NAME, ALARM_SRC, TP, LOCATION, HAPPEN_TIME};
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
        rootNode.set(ALARM_HIST_TABLES, TableUtils.generateRowArrayNode(tm));
        rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
        this.sendMessage(ALARM_HIST_DATA_RESP, rootNode);
    }



    @Override
    protected void populateTable(TableModel tm, ObjectNode payload) {
        List<Item> its = service.updateData(offset, limit);
        for (Item it : its) {
            populateRow(tm.addRow(), (HistoryAlarmItem)it);
        }
        offset += limit;
    }

    private void populateRow(TableModel.Row row, HistoryAlarmItem item) {
        row.cell(LEVEL, item.getLevel())
                .cell(ALARM_SRC, item.getAlarm_src())
                .cell(NAME, item.getName())
                .cell(TP, item.getTp())
                .cell(LOCATION, item.getLocation())
                .cell(HAPPEN_TIME, formatter.format(item.getHappen_time()));
    }
}