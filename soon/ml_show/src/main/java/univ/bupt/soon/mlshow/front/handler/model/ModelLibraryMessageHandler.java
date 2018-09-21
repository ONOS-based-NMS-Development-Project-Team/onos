package univ.bupt.soon.mlshow.front.handler.model;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.grpc.internal.IoUtils;
import org.apache.commons.collections.map.CompositeMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


import org.omg.CORBA.INTERNAL;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.foreground.ModelControlServiceAbstract;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.MLModelDetail;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.store.service.ConsistentMapException;
import org.onosproject.ui.JsonUtils;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.security.spec.RSAOtherPrimeInfo;
import java.sql.SQLTransactionRollbackException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.json.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import univ.bupt.soon.mlshow.front.MLMessageHandler;
import univ.bupt.soon.mlshow.front.Utils;

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
 * 			"ModelState":"",
 * 			"modelAccuracy":"",
 * 			"algorithmParams":{}
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 * }
 */
public class ModelLibraryMessageHandler extends UiMessageHandler{

    private static final String MODEL_DATA_REQ = "modelLibraryDataRequest";
    private static final String MODEL_DATA_RESP = "modelLibraryDataResponse";
    private static final String TABLES = "modelLibrarys";

    private static final String MODEL_MGMT_REQ = "modelLibraryManagementRequest";

    private static final String MODEL_DETAILS_REQ = "modelLibraryDetailsRequest";
    private static final String MODEL_DETAILS_RESP = "modelLibraryDetailsResponse";
    private static final String DETAILS = "details";

    private static final String MODEL_AVAITRAIN = "modelTrainAvailable";
    private static final String MODEL_AVAITEST = "modelTestAvailable";

    private static final String[] DETAILS_COL_IDS = {
            APP_TYPE,MODEL_ID,ALGO_TYPE,TRAIN_DATASET_ID,TEST_DATASET_ID,MODEL_STATE
            ,LOSS,REMAINING_TIME,PRECISION,MODEL_LINK,MODEL_ACCURACY,ALGO_PARAMS
    };

    private static final String[] TABLE_COL_IDS = {
            APP_TYPE,MODEL_ID,REMAINING_TIME,ALGO_TYPE,TRAIN_ID,MODEL_ACCURACY,AVAI_TEST,AVAI_TRAIN
    };

    private static final String[] ALGOPARAMS = {
            INPUT_NUM,OUTPUT_NUM,HIDDEN_LAYER,ACT_FUNTION,WEIGHT_INIT,BIAS_INIT,
            LOSS_FUNCTION,BATCH_SIZE,EPOCH,OPTIMIZER,LR,LR_ADJUST,DROPOUT
    };

    private static final String NO_ROWS_MESSAGE = "no model found";

    protected static final Map<Integer,ModelLibraryInfo> modelLibraryInfoMap = new ConcurrentHashMap<>();

    //modelId msgId 模型输入 模型输出
    protected static Map<Integer,Map<Integer,Object>> modelResultMap = new ConcurrentHashMap<>();

    //python调用回调函数operationFailure赋值存放 modelId action 以及python端错误description
    protected static Map<Integer,String> modelManagementMap = new ConcurrentHashMap<>();

    //java 不同的操作以及返回msgId存放 modelId action action java端
    protected static Map<Integer,ConcurrentHashMap<String,String>> modelManagementMapJava = new ConcurrentHashMap<>();

    //modelId msgId 是否训练完成  用来提醒前台相应信息
    private final Map<Integer, ConcurrentHashMap<Integer,Boolean>> modelTrainEnd = new ConcurrentHashMap<>();

    //test data set id 与对应 msgId存放，供modelEvaluation使用
    private final Map<Integer,Map<Integer,Integer>> modelMsgId = new ConcurrentHashMap<>();

    //发送可用训练集、测试集时，存放对应的id以供回调函数确定是否发送成功
    private static ConcurrentHashMap<Integer,ConcurrentHashMap<Integer,Boolean>> trainAvai = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer,ConcurrentHashMap<Integer,Boolean>> testAvai = new ConcurrentHashMap<>();

    //注入handlers
    public Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new ModelLibraryDataRequest(),
                new ModelLibraryMgmtRequest(),
                new ModelLibraryDetailsRequest(),
                new AlertHandler()
        );
    }


//    protected MLAlgorithmConfig getRightConfig (ModelLibraryInfo info) {
//        MLAlgorithmType algoType = info.getMlAlgorithmType();
//        MLAlgorithmConfig config = new MLAlgorithmConfig(MLAlgorithmType.FCNNModel);
//        switch (algoType){
//            case  FCNNModel:
//                config = (NNAlgorithmConfig)info.getMlModelDetail().getConfig();
//                break;
//            case RNNModel :
//                break;
//            case CNNModel :
//                break;
//            case LSTMModel :
//                break;
//            case RandomForestModel :
//                break;
//        }
//        return config;
//    }
    protected ModelControlService getService (MLAppType appType) {
        ModelControlService service = null;
        switch (appType){
            case LINK_PREDICTION:
                service = MLMessageHandler.modelServices.get(MLAppType.LINK_PREDICTION);
                break;
            case ALARM_PREDICTION:
                service = MLMessageHandler.modelServices.get(MLAppType.ALARM_PREDICTION);
                break;
            case FAILURE_CLASSIFICATION:
                service = MLMessageHandler.modelServices.get(MLAppType.FAILURE_CLASSIFICATION);
                break;
            case BUSINESS_AREA_PREDICTION:
                service = MLMessageHandler.modelServices.get(MLAppType.BUSINESS_AREA_PREDICTION);
        }
        return service;
    }

    protected String getModelAccuracy (ModelLibraryInfo info) {
        Map<Integer,String> modelAccuracyMap =  info.getMlModelDetail().getPerformances();
        if(modelAccuracyMap == null){
            return "";
        }else {
            List<String> modelAccuracy = new ArrayList<>();
            for (Integer key : modelAccuracyMap.keySet()) {
                String accuracy = modelAccuracyMap.get(key);
                modelAccuracy.add(accuracy);
            }
            if(modelAccuracy.size() == 1){
                return modelAccuracy.get(0);
            }else {
                return String.join(",", modelAccuracy);
            }
        }
    }

    public List<Integer> arrayToList (JsonNode arrNode) {
        List<Integer> arrList = new ArrayList<>();
        if(arrNode.isArray()){
            for(final JsonNode objNode : arrNode){
                int i = objNode.asInt(-1);
                arrList.add(i);
            }
        }
        return arrList;
    }

    protected void setDataSetAvai(Map map,int modelId,Set<Integer> set) {
        ConcurrentHashMap<Integer,Boolean> dataSetMap = new ConcurrentHashMap<>();
        for(int i : set){
            dataSetMap.put(i,false);
        }
        map.put(modelId,dataSetMap);
    }



    private final Logger log = LoggerFactory.getLogger(getClass());

    private final class ModelLibraryDataRequest extends TableRequestHandler {

        private ModelLibraryDataRequest(){
            super(MODEL_DATA_REQ,MODEL_DATA_RESP,TABLES);
        }

        @Override
        protected String[] getColumnIds(){ return TABLE_COL_IDS; }

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
            this.sendMessage(MODEL_DATA_RESP, rootNode);
        }

        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            if(modelLibraryInfoMap.isEmpty()){
                return;
            }else {
                for(int key : modelLibraryInfoMap.keySet()){
                    ModelLibraryInfo info = modelLibraryInfoMap.get(key);
                        tm.addRow()
                                .cell(APP_TYPE, info.getMlAppType().getName())
                                .cell(MODEL_ID, info.getModelId())
                                .cell(REMAINING_TIME, info.getRemainingTime())
                                .cell(ALGO_TYPE, info.getMlAlgorithmType().getName())
                                .cell(TRAIN_ID, info.getMlModelDetail().getTrainDatasetId())
                                .cell(MODEL_ACCURACY, getModelAccuracy(info))
                                .cell(AVAI_TEST,info.getAvailableTest())
                                .cell(AVAI_TRAIN,info.getAvailableTrain());
                }
            }
        }

    }

    private final class ModelLibraryMgmtRequest extends RequestHandler {
        private ModelLibraryMgmtRequest(){
            super(MODEL_MGMT_REQ);
        }

        int modelId;

        //action为add时 参数解析
        private MLAppType appType;
        private MLAlgorithmType algoType;
        private int trainId;
        private String trainS;
        private String testS;

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

        //modelServices.get(MLAppType.BUSINESS_AREA_PREDICTION)
        public int addModel (MLAlgorithmType algoType,MLAlgorithmConfig config,MLAppType appType) throws IOException {
            ModelForegroundCallback callback = new ModelForegroundCallback();

            //调用对应service的addNewModel，获取modelId
            Pair<Integer,Integer> addNewModelPair = getService(appType).addNewModel(algoType,config,callback);
            modelId = addNewModelPair.getKey();
            ConcurrentHashMap<String,String> javaMap = new ConcurrentHashMap<>();
            if(modelId == -1){
                //配置模型失败（Java部分），返回前台失败信息并将此次失败记录进modelManagementMapJava
                javaMap.put("add","load model failure in java");
                modelManagementMapJava.put(modelId,javaMap);
                ObjectNode rootNode = MAPPER.createObjectNode();
                rootNode.put(ANNOTS,"fail to load model "+modelId+"on platform");
                this.sendMessage(MODEL_ALERT,rootNode);
            }

            //传输对应modelId的数据集，并将返回数据集Id存进modelLibraryInfo,随模型的其他信息一起发给前台
            Pair<Set<Integer>,Set<Integer>> pair = getService(appType).transAvailableDataset(modelId);
            Set<Integer> trainIdSet = pair.getLeft();
            Set<Integer> testIdSet = pair.getRight();
            trainS = setToString(trainIdSet);
            testS = setToString(testIdSet);
            setDataSetAvai(trainAvai,modelId,trainIdSet);
            setDataSetAvai(testAvai,modelId,testIdSet);

            callback.modelId = modelId;
            //int addNewModelMsgId = addNewModelPair.getRight();
            return modelId;
        }

        protected void doAction (String action,ObjectNode payload) throws IOException {
            if(action == null){
                return;
            }else{
                switch (action){
                    case "add": {
                        JsonNode st = payload.get("algorithmParams");
                        appType = MLAppType.parseStr(JsonUtils.string(payload,APP_TYPE));
                        algoType = MLAlgorithmType.parseStr(JsonUtils.string(payload,ALGO_TYPE));
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
                        modelId = addModel(algoType,config,appType);
                        MLModelDetail detail = new MLModelDetail(config,null,trainId,modelId,null);//state获得，
                        ModelLibraryInfo newModel = new ModelLibraryInfo(appType,algoType,detail,modelId);
                        newModel.setAvailableTest(testS);
                        newModel.setAvailableTrain(trainS);
                        modelLibraryInfoMap.put(modelId,newModel);
                        break;
                    }
                    case "trainSet":
                        modelId = (int)JsonUtils.number(payload,MODEL_ID);
                        appType = modelLibraryInfoMap.get(modelId).getMlAppType();
                        trainId = Integer.parseInt(JsonUtils.string(payload,TRAIN_ID));
                        int trainSetMsgId = getService(appType).setDataset(modelId,trainId);
                        modelLibraryInfoMap.get(modelId).getMlModelDetail().setTrainDatasetId(trainId);
                        if(trainSetMsgId == 0){
                            ConcurrentHashMap<String,String> javaMap = new ConcurrentHashMap<>();
                            javaMap.put("trainSet","fail set train data set"+trainId+" on model"+modelId+"in java");
                            modelManagementMapJava.put(modelId,javaMap);
                            ObjectNode rootNode = MAPPER.createObjectNode();
                            rootNode.put(ANNOTS,"fail set train data set"+trainId+" on model"+modelId+"in java");
                            this.sendMessage(MODEL_ALERT,rootNode);
                        }
                        break;

                    case "delete":{
                        modelId = Integer.parseInt(JsonUtils.string(payload,MODEL_ID));
                        appType =  modelLibraryInfoMap.get(modelId).getMlAppType();
                        Pair<Boolean,Integer> deleteModelPair = getService(appType).deleteModel(modelId);
                        if(deleteModelPair.getLeft()){
                            modelLibraryInfoMap.remove(modelId);
                        }
                        else{
                            ConcurrentHashMap<String,String> javaMap = new ConcurrentHashMap<>();
                            javaMap.put("delete","fail delete model"+modelId+"in java");
                            modelManagementMapJava.put(modelId,javaMap);
                            ObjectNode rootNode = MAPPER.createObjectNode();
                            rootNode.put(ANNOTS,"fail to delete model "+modelId+" on platform");
                            this.sendMessage(MODEL_ALERT,rootNode);
                            //主动给前台发送删除失败的alert，这个可以在前台加一个alertResponse以及对应的handler
                        }
                        break;
                    }
                    case "start":{
                        modelId = Integer.parseInt(JsonUtils.string(payload,MODEL_ID));
                        appType = modelLibraryInfoMap.get(modelId).getMlAppType();
                        Pair<Boolean,Integer> startModelPair = getService(appType).startTraining(modelId);
                        if(startModelPair.getLeft()){
//                            ConcurrentHashMap<Integer,Boolean> trainEnd = new ConcurrentHashMap<>();
//                            trainEnd.put(startModelPair.getRight(),true);
//                            modelTrainEnd.put(modelId,trainEnd);
                        }
                        else {
                            ConcurrentHashMap<String,String> javaMap = new ConcurrentHashMap<>();
                            javaMap.put("start","fail start model"+modelId+"in java");
                            modelManagementMapJava.put(modelId,javaMap);
                            ObjectNode rootNode = MAPPER.createObjectNode();
                            rootNode.put(ANNOTS,"model "+modelId+"is already in training");
                            this.sendMessage(MODEL_ALERT,rootNode);
                            //同上
                        }
                        break;
                    }
                    case "evaluate":{
                        modelId = (int)JsonUtils.number(payload,MODEL_ID);
                        appType = modelLibraryInfoMap.get(modelId).getMlAppType();
                        String testIdString = JsonUtils.string(payload,TEST_ID);
                        testId = stringToIntList(testIdString);
                        modelLibraryInfoMap.get(modelId).setTestDataSetId(testId);
                        int setTestMsgId;
                        Map<Integer,Integer> evaMap = new ConcurrentHashMap<>();
                        for(int i=0;i<testId.size();i++){
                            Pair<Boolean,Integer> evaModelPair = getService(appType).getModelEvaluation(modelId,testId.get(i));
                            if(evaModelPair.getLeft()){
                                setTestMsgId = evaModelPair.getRight();
                                evaMap.put(setTestMsgId,testId.get(i));
                            }
                            else {
                                ConcurrentHashMap<String,String> javaMap = new ConcurrentHashMap<>();
                                javaMap.put("evaluate","fail evaluate model"+modelId+"in testId "+testId.get(i)+"in java");
                                modelManagementMapJava.put(modelId,javaMap);
                                ObjectNode rootNode = MAPPER.createObjectNode();
                                rootNode.put(ANNOTS,"fail to evaluate model "+modelId+"in test data set "+testId.get(i)+" on platform");
                                this.sendMessage(MODEL_ALERT,rootNode);
                            }
                        }
                        modelMsgId.put(modelId,evaMap);
                        break;
                    }
                }
            }
        }

        @Override
        public void process (ObjectNode payload) {

            //解析managementRequest参数
            //long modelId = JsonUtils.number(payload,MODEL_ID);
            try {
                String action = JsonUtils.string(payload, ACTION);
                doAction(action, payload);
            }
            catch (Exception ex){
                log.debug(ex.toString());
            }

        }

    }

    private final class ModelLibraryDetailsRequest extends RequestHandler {
        private ModelLibraryDetailsRequest(){
            super(MODEL_DETAILS_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            int modelId = (int)JsonUtils.number(payload,MODEL_ID);

            ModelLibraryInfo model = modelLibraryInfoMap.get(modelId);
            ObjectNode data = objectNode();
            ObjectNode algoParams = objectNode();

            MLAlgorithmConfig config = model.getMlModelDetail().getConfig();

            //装填模型参数ObjectNode
            algoParams.put(INPUT_NUM,((NNAlgorithmConfig) config).getInputNum());
            algoParams.put(OUTPUT_NUM,((NNAlgorithmConfig) config).getOutputNum());
            algoParams.put(HIDDEN_LAYER,((NNAlgorithmConfig) config).getHiddenLayer().toString());
            algoParams.put(ACT_FUNTION,((NNAlgorithmConfig) config).getActivationFunction().getName());
            algoParams.put(WEIGHT_INIT,((NNAlgorithmConfig) config).getWeightInit().getName());
            algoParams.put(BIAS_INIT,((NNAlgorithmConfig) config).getBiasInit().getName());
            algoParams.put(LOSS_FUNCTION,((NNAlgorithmConfig) config).getLossFunction().getName());
            algoParams.put(BATCH_SIZE,((NNAlgorithmConfig) config).getBatchSize());
            algoParams.put(EPOCH,((NNAlgorithmConfig) config).getEpoch());
            algoParams.put(OPTIMIZER,((NNAlgorithmConfig) config).getOptimizer().getName());
            algoParams.put(LR,((NNAlgorithmConfig) config).getLearningRate());
            algoParams.put(LR_ADJUST,((NNAlgorithmConfig) config).getLrAdjust().getName());
            algoParams.put(DROPOUT,((NNAlgorithmConfig) config).getDropout());

            //装填模型详细信息ObjectNode
            data.put(MODEL_ID, String.valueOf(modelId));
            data.put(APP_TYPE,model.getMlAppType().getName());
            data.put(ALGO_TYPE,model.getMlAlgorithmType().getName());
            data.put(TRAIN_ID,model.getMlModelDetail().getTrainDatasetId());
            data.put(TEST_ID,listToString(model.getTestDataSetId()));
            data.put(LOSS, model.getLoss());
            data.put(REMAINING_TIME, model.getRemainingTime());
            data.put(PRECISION, model.getPrecision());
            data.put(MODEL_LINK, model.getModelLink());
            data.put(MODEL_ACCURACY, getStringEvaluate(model.getMlModelDetail().getPerformances()));
            data.put(ALGO_PARAMS,algoParams);

            ObjectNode rootNode = objectNode();
            rootNode.set(DETAILS, data);
            sendMessage(MODEL_DETAILS_RESP, rootNode);
        }
    }

    private class ModelForegroundCallback implements ForegroundCallback {



        public ModelForegroundCallback () {}
        private int modelId;
        private final ModelLibraryMessageHandler.AlertHandler alertHandler = new ModelLibraryMessageHandler().new AlertHandler();
        //ForegroundCallback实现
        @Override
        public void operationFailure(int msgId,String description){
            modelManagementMap.put(msgId,description);
            //alertHandler.failurePyAlert(this.modelId,description);
        }

        @Override
        public void appliedModelResult(int msgId, List<Item> input, List<String> output) {
            Pair<List<Item>,List<String>> resultPair = new ImmutablePair<>(input,output);
            modelResultMap.get(this.modelId).put(msgId,resultPair);
        }


        @Override
        public void modelEvaluation(int msgId, String result) {
            int testId = modelMsgId.get(this.modelId).get(msgId);
            MLModelDetail detail = modelLibraryInfoMap.get(this.modelId).getMlModelDetail();
            Map<Integer,String> map = new ConcurrentHashMap<>();
            map.put(testId,result);
            detail.setPerformances(map);
            modelLibraryInfoMap.get(this.modelId).setMlModelDetail(detail);
        }

        @Override
        public void trainDatasetTransEnd(int msgId, int trainDatasetId) {
            ConcurrentHashMap<Integer,Boolean> map = new ConcurrentHashMap<>();
            map.put(trainDatasetId,true);
            trainAvai.put(this.modelId,map);
            //alertHandler.avaiTrainEndAlert(this.modelId,trainDatasetId);
        }

        @Override
        public void testDatasetTransEnd(int msgId, int testDatasetId) {
            ConcurrentHashMap<Integer,Boolean> map = new ConcurrentHashMap<>();
            map.put(testDatasetId,true);
            testAvai.put(this.modelId,map);
            //alertHandler.avaiTestEndAlert(this.modelId,testDatasetId);
        }

        @Override
        public void ResultUrl(int msgId, URI uri) {
            modelLibraryInfoMap.get(this.modelId).setModelLink("https://"+uri.toASCIIString());
             //alertHandler.urlAlert(this.modelId,uri);
        }

        @Override
        public void trainEnd(int msgId) {
            //告知前台训练完成
            //alertHandler.trainEndAlert(this.modelId);

            //发送url请求
            Pair<Boolean,Integer> urlPair = getService(modelLibraryInfoMap.get(this.modelId).getMlAppType()).queryURL(this.modelId);
            boolean querySuccess = urlPair.getLeft();
            if(!querySuccess){
                //alertHandler.uriFalseAlert(this.modelId);
            }
        }
        @Override
        public void intermediateResult(int msgId, MonitorData monitorData) {
            ModelLibraryInfo model = modelLibraryInfoMap.get(this.modelId);
            NNMonitorData data = (NNMonitorData)monitorData;
            model.setLoss(data.getLoss());
            model.setRemainingTime(data.getRemainingTime());
            model.setPrecision(data.getPrecision());
        }

        @Override
        public void originData(SegmentForDataset segmentForDataset) {
            log.info("hhh");
        }

    }

    private class AlertHandler extends RequestHandler {
        private AlertHandler () {
            super(MODEL_ALERT_REQ);
        }

        protected void avaiTrainEndAlert(int modelId,int trainId) {
            ConcurrentHashMap<Integer,Boolean> map = trainAvai.get(modelId);
            if(map.get(trainId)) {
                ObjectNode rootNode = objectNode();
                rootNode.put(ANNOTS, "success to transfer train data set " + trainId);
                this.sendMessage(MODEL_ALERT, rootNode);
            }
        }

        protected void avaiTestEndAlert(int modelId,int testId) {
            ConcurrentHashMap<Integer,Boolean> map = trainAvai.get(modelId);
            if(map.get(testId)) {
                ObjectNode rootNode = objectNode();
                rootNode.put(ANNOTS, "success to transfer test data set " + testId);
                this.sendMessage(MODEL_ALERT, rootNode);
            }
        }

        protected void failurePyAlert(int modelId,String des){
            ObjectNode rootNode = objectNode();
            rootNode.put(ANNOTS,"there is an error in model"+modelId+"\n"+des);
            this.sendMessage(MODEL_ALERT,rootNode);
        }

        protected void urlAlert(int modelId,URI uri) {
            ObjectNode rootNode = MAPPER.createObjectNode();
            rootNode.put(ANNOTS,"success to get url of model "+modelId+"\n"+"which is https://"+uri.toASCIIString());
            this.sendMessage(MODEL_ALERT,rootNode);
        }

        protected void trainEndAlert(int modelId) {
            ObjectNode rootNode = MAPPER.createObjectNode();
            rootNode.put(ANNOTS,"model "+modelId+" training is completed");
            this.sendMessage(MODEL_ALERT,rootNode);
        }
        protected void uriFalseAlert(int modelId) {
            ObjectNode node = MAPPER.createObjectNode();
            node.put(ANNOTS,"fail to get url of model "+modelId+"on platform");
            this.sendMessage(MODEL_ALERT,node);
        }
        @Override
        public void process (ObjectNode payload) { }
    }


}
