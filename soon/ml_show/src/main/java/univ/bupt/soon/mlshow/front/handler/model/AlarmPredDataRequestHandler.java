package univ.bupt.soon.mlshow.front.handler.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 			"time":"",
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
public class AlarmPredDataRequestHandler extends TableRequestHandler {
    private static final String DATA_REQ = "alarmPredDataRequest";
    private static final String DATA_RESP = "alarmPredDataResponse";
    private static final String TABLES = "alarmPreds";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service;
    // 数据库查询的时候定位查询数据的地方。返回数据在[offset, offset+limit)区间
    private int offset = 0;
    private int limit = 10;

    public AlarmPredDataRequestHandler() {
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
        String[] COLUMN_IDS = {TIME, ALARM_HAPPEN, MODEL_ID, TRAIN_DATASET_ID, TEST_DATASET_ID, TEST_ACCURACY};
        return COLUMN_IDS;
    }

    @Override
    protected String noRowsMessage(ObjectNode objectNode) {
        return NO_ROWS_MESSAGE;
    }

    @Override
    protected void populateTable(TableModel tableModel, ObjectNode objectNode) {
        // TODO
    }
}
