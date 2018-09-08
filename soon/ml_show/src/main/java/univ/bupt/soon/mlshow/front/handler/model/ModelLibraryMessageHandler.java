package univ.bupt.soon.mlshow.front.handler.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import com.sun.tools.internal.xjc.model.Model;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.graalvm.compiler.salver.util.ECIDUtil;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.spec.RSAOtherPrimeInfo;
import java.sql.SQLTransactionRollbackException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;

/**
 *
 * #soonEvent:modelLibraryManagementRequest   对应的response也是modelLibraryDataResponse
 * {
 * 	"event":"modelLibraryManagementRequest",
 * 	"payload":{
 * 		"modelId":"",
 * 		"action":"",
 * 		"applicationType":"",
 * 		"algorithmType":"",
 * 		"trainDataSetId":"",
 * 		"testDataSetId":[],
 * 		"modelAccuracy":"",
 * 		"algorithmParams":{}
 *        }
 * #event:modelLibraryManagementResponse
 * {
 * 	"event":"modelLibraryManagementResponse",
 * 	"payload":{
 * 		"modelLibrarys":[{
 * 			"applicationType":"",
 * 			"modelId":"",
 * 			"algorithmType":"",
 * 			"trainDataSetId":"",
 * 			"testDataSetId":[],
 * 			"modelState":"",
 * 			"modelAccuracy":"",
 * 			"algorithmParams":{}
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 * }
 */
public class ModelLibraryMessageHandler extends UiMessageHandler {

    private static final String MODEL_DATA_REQ = "modelLibraryManagementRequest";
    private static final String MODEL_DATA_RESP = "modelLibraryManagementResponse";
    private static final String TABLES = "modelLibrarys";

    private static final String MODEL_MGMT_REQ = "modelLibraryManagementRequest";

    private static final String MODEL_DETAILS_REQ = "modelLibraryDetailRequest";
    private static final String MODEL_DETAILS_RESP = "modelLibraryDetailsResponse";
    private static final String DETAILS = "details";

    private static final String[] COL_IDS = {
            APP_TYPE,MODEL_ID,ALGO_TYPE,TRAIN_DATASET_ID,TEST_DATASET_ID,MODEL_STATE
            ,LOSS,REMAINING_TIME,PRECISION,MODEL_LINK,MODEL_ACCURACY,ALGO_PARAMS
    };

    private static final String[] ALGOPARAMS = {
            INPUT_NUM,OUTPUT_NUM,HIDDEN_LAYER,ACT_FUNTION,WEIGHT_INIT,BIAS_INIT,
            LOSS_FUNCTION,BATCH_SIZE,EPOCH,OPTIMIZER,LR,LR_ADJUST,DROPOUT
    };

    private static final String NO_ROWS_MESSAGE = "no model found";

    private final Map<String,ModelLibraryInfo> modelLibraryInfoMap = new ConcurrentHashMap<>();

    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new ModelLibraryDataRequest(),
                new ModelLibraryMgmtRequest(),
                new ModelLibraryDetailsRequest()
        );
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ModelControlService service;

    public void setService (ModelControlService service) {
        this.service = service;
    }

    private final class ModelLibraryDataRequest extends TableRequestHandler {

        private ModelLibraryDataRequest(){
            super(MODEL_DATA_REQ,MODEL_DATA_RESP,TABLES);
        }

        @Override
        protected String[] getColumnIds(){ return COL_IDS; }

        @Override
        protected String noRowsMessage(ObjectNode payload) {
            return NO_ROWS_MESSAGE;
        }

        @Override
        public void process (ObjectNode payload) {

            //解析排序参数
            String firstCol = JsonUtils.string(payload, FIRST_COL, APP_TYPE);
            String firstDir = JsonUtils.string(payload, FIRST_DIR, ASC);
            String secondCol = JsonUtils.string(payload, SECOND_COL, MODEL_ID);
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
            this.sendMessage(MODEL_DATA_REQ, rootNode);
        }

        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            if(modelLibraryInfoMap.isEmpty()){
                return;
            }else {
                for(String key : modelLibraryInfoMap.keySet()){
                    ModelLibraryInfo info = modelLibraryInfoMap.get(key);
                    tm.addRow()
                            .cell(APP_TYPE,info.getMlAppType())
                            .cell(MODEL_ID,info.getModelId())
                            .cell(LOSS,info.getLoss())
                            .cell(REMAINING_TIME,info.getRemainingTime())
                            .cell(PRECISION,info.getPrecision())
                            .cell(MODEL_LINK,info.getModelLink())
                            .cell(ALGO_TYPE,info.getMlModelDetail().getConfig().)
                }
            }
        }

    }

    private final class ModelLibraryMgmtRequest extends RequestHandler {

    }

    private final class ModelLibraryDetailsRequest extends RequestHandler {

    }
}
