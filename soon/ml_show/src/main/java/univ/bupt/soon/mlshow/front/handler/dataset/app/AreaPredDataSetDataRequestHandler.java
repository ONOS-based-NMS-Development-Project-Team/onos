package univ.bupt.soon.mlshow.front.handler.dataset.app;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.AreaPredictionItem;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
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
 * #soonEvent: areaPredDataSetDataRequest
 * {
 * 	"event":"areaPredDataSetDataRequest",
 * 	"payload":{
 * 		"firstCol":"dataId",
 * 		"firstDir":"asc",
 * 		"secondCol":"areaId",
 * 		"secondDir":"asc",
 * 		"setting":{
 * 			"algorithmType":"",
 * 			"dataSetType":"",
 * 			//"dataSetId":"",
 * 			//"modelId":""
 *                }    * 	}
 * }
 * #soonEvent: areaPredDataSetDataResponse
 * {
 * 	"event":"areaPredDataSetDataResponse",
 * 	"payload":{
 * 		"areaPredDataSets":[{
 * 			"dataId":"",
 * 			"areaId":"",
 * 			"dataSetId":"",
 * 			"dataSetType":"",
 * 			"tide":"",
 * 			"timePoint":"",
 * 			"oneHoursAfter":[],
 * 			"twoHoursBefore":[]
 *        },{}],
 * 		"ANNOTS":{}    * 	}
 * }
 */
public class AreaPredDataSetDataRequestHandler extends TableRequestHandler {

    private static final String DATA_REQ = "areaPredDataSetDataRequest";
    private static final String DATA_RESP = "areaPredDataSetDataResponse";
    private static final String TABLES = "areaPredDataSets";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service;
    // 数据库查询的时候定位查询数据的地方。返回数据在[offset, offset+limit)区间
    private int offset = 0;
    private int limit = 10;

    // 查询参数的存储
    private MLAlgorithmType algType;
    private String isTrain;
    private int datasetId;
    private int modelId;


    public AreaPredDataSetDataRequestHandler() {
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
        String[] COLUMN_IDS = {DATA_ID, AREA_ID, DATASET_ID, DATASET_TYPE, TIDE, TIMEPOINT, ONE_HOUR_AFTER, TWO_HOURS_BEFORE};
        return COLUMN_IDS;
    }

    @Override
    protected String noRowsMessage(ObjectNode objectNode) {
        return NO_ROWS_MESSAGE;
    }

    @Override
    public void process(ObjectNode payload) {
        // 参数解析
        String firstCol = JsonUtils.string(payload, FIRST_COL, LEVEL);
        String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
        String secondCol = JsonUtils.string(payload, SECOND_COL, ALARM_SRC);
        String secondDir = JsonUtils.string(payload, SECOND_DIR, ASC);
        // 解析setting
        JsonNode st = payload.get("setting");
        String at = st.get("algorithmType").asText(MLAlgorithmType.FCNNModel.getName());
        String datasetType = st.get("dataSetType").asText();  // train或者test
        datasetId = st.get("dataSetId").asInt(-1);
        modelId = st.get("modelId").asInt(-1);
        algType = MLAlgorithmType.FCNNModel.parseStr(at);
        if (datasetType.equals("test")) {
            isTrain = "false";
        } else {
            isTrain = "true";
        }
        // 推送数据
        TableModel tm = createTableModel();
        this.populateTable(tm, payload);
        // 排序,并推送数据给前台
        tm.sort(firstCol, sortDir(firstDir), secondCol, sortDir(secondDir));
        this.addTableConfigAnnotations(tm, payload);
        ObjectNode rootNode = MAPPER.createObjectNode();
        rootNode.set(TABLES, TableUtils.generateRowArrayNode(tm));
        rootNode.set(ANNOTS, TableUtils.generateAnnotObjectNode(tm));
        this.sendMessage(DATA_RESP, rootNode);
    }

    @Override
    protected void populateTable(TableModel tableModel, ObjectNode objectNode) {
        // 区域预测中,训练集和测试集是一样的
        // 其中只有isTrain和datasetId可以用来查询
//        if (service.getAppStatics().getTrainDataset().containsKey(datasetId)) {
            String sql = "SELECT * FROM area_load OFFSET "+offset+" LIMIT "+limit;
            List<Item> results = service.updateData(sql, AreaPredictionItem.class);
            for (Item it : results) {
                AreaPredictionItem item = (AreaPredictionItem) it;
                tableModel.addRow()
                        .cell(DATA_ID, item.getId())
                        .cell(AREA_ID, item.getArea_id())
                        .cell(DATASET_ID, datasetId)
                        .cell(DATASET_TYPE, isTrain)
                        .cell(TIDE, item.getTide())
                        .cell(TIMEPOINT, item.getTimepoint())
                        .cell(ONE_HOUR_AFTER, checkNull(item.getOne_hour_after()))
                        .cell(TWO_HOURS_BEFORE, checkNull(item.getTwo_hours_before()));
            }
            if (results.size() != limit) {
                offset = 0;
            }
            offset += limit;
//        } else {
//            log.error("No such dataset {}", datasetId);
//        }
    }
}
