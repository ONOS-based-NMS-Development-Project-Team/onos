package univ.bupt.soon.mlshow.front.handler.model;

import com.eclipsesource.json.JsonArray;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;

import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.config.nn.NNAlgorithmConfig;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.json

import static org.onosproject.ui.table.TableModel.sortDir;
import static univ.bupt.soon.mlshow.front.Utils.*;

/**
 *
 * #soonEvent:modelLibraryManagementRequest   对应的response也是modelLibraryDataResponse
 * {
 * 	"event":"modelLibraryManagementRequest",
 * 	"payload":{
 * 		"modelId":"",
 * 		"Action":"",
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

    protected MLAlgorithmConfig getRightConfig (ModelLibraryInfo info) {
        MLAlgorithmType algoType = info.getMlAlgorithmType();
        MLAlgorithmConfig config = new MLAlgorithmConfig(MLAlgorithmType.FCNNModel);
        switch (algoType){
            case  FCNNModel:
                config = (NNAlgorithmConfig)info.getMlModelDetail().getConfig();
                break;
            case RNNModel :
                break;
            case CNNModel :
                break;
            case LSTMModel :
                break;
            case RandomForestModel :
                break;
        }
        return config;
    }

    protected Object[] getModelAccuracy (ModelLibraryInfo info) {
        Map<Integer,Double> modelAccuracyMap =  info.getMlModelDetail().getPerformances();
        List<Double> modelAccuracy = new ArrayList();
        for(Integer key : modelAccuracyMap.keySet()){
            double accuracy = modelAccuracyMap.get(key);
            modelAccuracy.add(accuracy);
        }
        Object[] modelAccuracyAraay = modelAccuracy.toArray();
        return modelAccuracyAraay;
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
                    () -> tm.addRow()
                            .cell(APP_TYPE, info.getMlAppType())
                            .cell(MODEL_ID, info.getModelId())
                            .cell(REMAINING_TIME, info.getRemainingTime())
                            .cell(ALGO_TYPE, info.getMlAlgorithmType())
                            .cell(TRAIN_ID,info.getMlModelDetail().getTrainDatasetId())
                            .cell(MODEL_STATE,info.getMlModelDetail().getState())
                            .cell(MODEL_ACCURACY,getModelAccuracy(info))
                }
            }
        }

    }

    private final class ModelLibraryMgmtRequest extends RequestHandler {
        private ModelLibraryMgmtRequest(){
            super(MODEL_DETAILS_REQ);
        }

        protected void doAction (String action,ObjectNode payload,JsonNode st) {
            if(action == null){
                return;
            }else{
                switch (action){
                    case "add": {
                        long modelId = JsonUtils.number(payload,MODEL_ID);
                        String appType = JsonUtils.string(payload,APP_TYPE);
                        String algoType = JsonUtils.string(payload,ALGO_TYPE);
                        long trianId = JsonUtils.number(payload,TRAIN_ID);
                        int inputNum = st.get("inputNum").asInt(-1);
                        int outputNum = st.get("outputNum").asInt(-1);
                        int inputNum = st.get("inputNum").asInt(-1);
                        List<Integer> hiddenLayer = JsonArray.


                    }
                }
            }
        }

        @Override
        public void process (ObjectNode payload) {

            //解析managementRequest参数
            //long modelId = JsonUtils.number(payload,MODEL_ID);
            String action = JsonUtils.string(payload,ACTION);
            JsonNode st = payload.get("modelParams");

        }

    }

    private final class ModelLibraryDetailsRequest extends RequestHandler {

    }
}
