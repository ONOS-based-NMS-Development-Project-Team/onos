package univ.bupt.soon.mlshow.front.handler.dataset.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
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
 * #soonEvent: faultClassificationDataSetDataRequest
 * {
 * 	"event":"faultClassificationDataSetDataRequest",
 * 	"payload":{
 * 		"firstCol":"dataId",
 * 		"firstDir":"asc",
 * 		"secondCol":"faultType",
 * 		"secondDir":"asc",
 * 		"setting":{
 * 			"algorithmType":"",
 * 			"dataSetType":"",
 * 			//"dataSetId":"",
 * 			//"modelId":""
 *                }    * 	}
 * }
 * #soonEvent: faultClassificationDataSetDataResponse
 * {
 * 	"event":"faultClassificationDataSetDataResponse",
 * 	"payload":{
 * 		"alarmPredDataSets":[{
 * 			"dataId":"",
 * 			"faultType":"",
 * 			"dataSetId":"",
 * 			"dataSetType":"",
 * 			"input":[]
 *        },{}],
 * 		"ANNOTS":{}    * 	}
 * }
 */
public class FailClassDataSetDataRequestHandler  extends TableRequestHandler {


    private static final String DATA_REQ = "faultClassificationDataSetDataRequest";
    private static final String DATA_RESP = "faultClassificationDataSetDataResponse";
    private static final String TABLES = "faultClassificationDataSets";

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


    public FailClassDataSetDataRequestHandler() {
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
        String[] COLUMN_IDS = {DATA_ID, FAULT_TYPE, DATASET_ID, DATASET_TYPE, INPUT_1,
            INPUT_2,INPUT_3,INPUT_4,INPUT_5,INPUT_6};
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
        // 根据算法类型,是否是训练集,数据集id,模型id来刷新数据集
        // 其中只有isTrain和datasetId可以用来查询
//        if (service.getAppStatics().getTrainDataset().containsKey(datasetId)) {
            // 现在只有一个数据集,因此,只需要判断该数据集id是否包含在统计信息里面即可
            String sql = "SELECT * FROM failure_class WHERE train=" + isTrain + " OFFSET " + offset + " LIMIT " + limit + ";";
            List<Item> results = service.updateData(sql, FailureClassificationItem.class);
            for (Item it : results) {
                FailureClassificationItem item = (FailureClassificationItem) it;
                tableModel.addRow()
                        .cell(DATA_ID, item.getId())
                        .cell(FAULT_TYPE, item.getCls())
                        .cell(DATASET_ID, datasetId)
                        .cell(DATASET_TYPE, isTrain)
                        .cell(INPUT_1, item.input_1())
                        .cell(INPUT_2,item.input_2())
                        .cell(INPUT_3,item.input_3())
                        .cell(INPUT_4,item.input_4())
                        .cell(INPUT_5,item.input_5())
                        .cell(INPUT_6,item.input_6());
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
