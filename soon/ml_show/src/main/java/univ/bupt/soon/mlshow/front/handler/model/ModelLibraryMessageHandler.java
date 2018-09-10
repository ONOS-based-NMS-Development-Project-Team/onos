package univ.bupt.soon.mlshow.front.handler.model;

import com.eclipsesource.json.JsonArray;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;


import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.MLModelDetail;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.security.spec.RSAOtherPrimeInfo;
import java.sql.SQLTransactionRollbackException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.json.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class ModelLibraryMessageHandler extends UiMessageHandler implements ForegroundCallback{

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

    private final Map<Integer,ModelLibraryInfo> modelLibraryInfoMap = new ConcurrentHashMap<>();
    private final Map<Integer,Object> modelManagementMap = new ConcurrentHashMap<>();

    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new ModelLibraryDataRequest(),
                new ModelLibraryMgmtRequest(),
                new ModelLibraryDetailsRequest()
        );
    }
    @Override
    public void operationFailure(int modelId,int msgId,String description){
        modelManagementMap.get(modelId).put(msgId,description);
    }

    @Override
    public void appliedModelResult(int msgId, List<Item> input, List<String> output) { }

    @Override
    public void modelEvaluation(int msgId, String result) { }

    @Override
    public void trainDatasetTransEnd(int msgId, int trainDatasetId) { }

    @Override
    public void testDatasetTransEnd(int msgId, int testDatasetId) { }

    @Override
    public void ResultUrl(int msgId, URI uri) { }

    @Override
    public void trainEnd(int msgId) { }
    @Override
    public void intermediateResult(int msgId, MonitorData monitorData) { }

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

    protected List<Integer> arrayToList (JsonNode arrNode) {
        List<Integer> arrList = new ArrayList<>();
        if(arrNode.isArray()){
            for(final JsonNode objNode : arrNode){
                int i = objNode.asInt(-1);
                arrList.add(i);
            }
        }
        return arrList;
    }

    public int addModel (MLAlgorithmType algoType,MLAlgorithmConfig config) {
        Pair<Integer,Integer> addNewModelPair = service.addNewModel(algoType,config,new ModelLibraryMessageHandler());
        int modelId = addNewModelPair.getKey();
        int addNewModelMsgId = addNewModelPair.getRight();
        modelManagementMap.put(modelId,new HashMap<>());
        modelManagementMap.get(modelId).put(addNewModelMsgId,null);
        if(modelId == -1){
            modelManagementMap.put(modelId,"load model failure in java");
        }
        return modelId;
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

        int modelId;

        //action为add时 参数解析
        private MLAppType appType;
        private MLAlgorithmType algoType;
        private int trainId;

        //action为delete时，解析modelId

        //action为start时，解析参数modelId，判断state

        //action为evaluate时，解析参数modelId以及
        private List<Integer> testId;

        //action为add时，解析算法参数
        private int inputNum;
        private int outputNum;
        private List<Integer> hiddenLayer;
        private ActivationFunction activationFunction;
        private ParamInit weightInit;
        private ParamInit biasInit;
        private LossFunction lossFunction;
        private int batchSize;
        private int epoch;
        private Optimizer optimizer;
        private double learningRate;
        private LRAdjust lrAdjust;
        private double dropout;

        protected void doAction (String action,ObjectNode payload,JsonNode st) {
            if(action == null){
                return;
            }else{
                switch (action){
                    case "add": {
                        appType = MLAppType.parseStr(JsonUtils.string(payload,APP_TYPE));
                        algoType = MLAlgorithmType.parseStr(JsonUtils.string(payload,ALGO_TYPE));
                        trainId = (int)JsonUtils.number(payload,TRAIN_ID);
                        inputNum = st.get("inputNum").asInt(-1);
                        outputNum = st.get("outputNum").asInt(-1);
                        JsonNode arrNode = st.get("hiddenLayer");
                        hiddenLayer =  arrayToList(arrNode);
                        activationFunction = ActivationFunction.parseStr(st.get("activationFunction").asText());
                        weightInit = ParamInit.parseStr(st.get("weightInit").asText());
                        biasInit = ParamInit.parseStr(st.get("biasInit").asText());
                        lossFunction = LossFunction.parseStr(st.get("lossFunction").asText());
                        batchSize = st.get("batchSize").asInt();
                        epoch = st.get("epoch").asInt();
                        optimizer = Optimizer.parseStr(st.get("optimizer").asText());
                        learningRate = st.get("learningRate").asDouble();
                        lrAdjust = LRAdjust.parseStr(st.get("lrAdjust").asText());
                        dropout = st.get("dropout").asDouble();
                        //组装MLAlgorithmConfig
                        NNAlgorithmConfig config = new NNAlgorithmConfig(algoType,inputNum,outputNum,hiddenLayer,activationFunction,
                                weightInit,biasInit,lossFunction,batchSize,epoch,optimizer,learningRate,lrAdjust,dropout);
                        modelId = addModel(algoType,config);
                        MLModelDetail detail = new MLModelDetail(config,null,trainId,modelId,null);//state获得，
                        ModelLibraryInfo newModel = new ModelLibraryInfo(appType,algoType,detail,modelId);
                        modelLibraryInfoMap.put(modelId,newModel);
                    }
                    case "delete":{
                        modelId = (int)JsonUtils.number(payload,MODEL_ID);
                        Pair<Boolean,Integer> deleteModelPair = service.deleteModel(modelId);
                        if(deleteModelPair.getLeft()){
                            modelLibraryInfoMap.remove(modelId);
                        }
                        else{
                            //主动给前台发送删除失败的alert，这个可以在前台加一个alertResponse以及对应的handler
                        }
                    }
                    case "start":{
                        modelId = (int)JsonUtils.number(payload,MODEL_ID);
                        Pair<Boolean,Integer> startModelPair = service.startTraining(modelId);
                        if(startModelPair.getLeft()){
                            //更改模型状态
                        }
                        else {
                            //同上
                        }
                    }
                    case "evaluate":{
                        modelId = (int)JsonUtils.number(payload,MODEL_ID);
                        JsonNode arrNode = st.get("testDataSetId");
                        int setTestMsgId;
                        testId = arrayToList(arrNode);
                        for(int i=0;i<testId.size();i++){
                            Pair<Boolean,Integer> evaModelPair = service.getModelEvaluation(modelId,testId.get(i));
                            if(evaModelPair.getLeft()){

                            }
                        }

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

        @Override
        public void operationFailure (int msgId,String description){

        }

        @Override


    }

    private final class ModelLibraryDetailsRequest extends RequestHandler {

    }
}
