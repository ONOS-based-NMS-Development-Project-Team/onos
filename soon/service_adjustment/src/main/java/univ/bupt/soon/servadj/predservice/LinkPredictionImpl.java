package univ.bupt.soon.servadj.predservice;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.EdgePredictionItem;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.platform.PlatformCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.servadj.ServiceAdjustComponent;

import java.io.FileInputStream;
import java.net.URI;
import java.util.*;

/**
 * 链路预测模型
 */
public class LinkPredictionImpl implements ModelControlService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MLAppType serviceName = MLAppType.LINK_PREDICTION;

    private DatabaseAdapter database;
    private MLPlatformService platformService;
    // modelId和模型配置之间的映射关系
    private Map<Integer, MLAlgorithmConfig> configs = Maps.newConcurrentMap();
    // modelId和平台回调接口的映射关系
    private Map<Integer, InternalPlatformCallback> ipcs = Maps.newConcurrentMap();
    // modelId和前台回调接口的映射关系
    private Map<Integer, ForegroundCallback> fcbs = Maps.newConcurrentMap();
    // modelId和websocketId的映射关系
    private Map<Integer, Integer> wsIds = Maps.newConcurrentMap();
    // 该应用类型下的可用训练集id集合.Map.key表示训练集id,Map.value表示训练集的size
    private Map<Integer, Integer> trainIds = Maps.newConcurrentMap();
    // 该应用类型下的可用测试集.Map.key表示测试集id, Map.value表示测试集的size
    private Map<Integer, Integer> testIds = Maps.newConcurrentMap();

    public LinkPredictionImpl(DatabaseAdapter database, MLPlatformService platformService) {
        this.database = database;
        this.platformService = platformService;
    }

    public MLPlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(MLPlatformService platformService) {
        this.platformService = platformService;
    }


    /**
     @return Pair.Left表示增加的神经网络模型的id。如果为-1,表示模型增加失败; Pair.Right表示发送的模型配置消息的msgId
     */
    @Override
    public Pair<Integer, Integer> addNewModel(MLAlgorithmType mlAlgorithmType,
                                              MLAlgorithmConfig mlAlgorithmConfig,
                                              ForegroundCallback foregroundCallback) {
        /* 增加一个新模型配置，需要确定算法类型，算法参数配置，建立平台回调接口注册与实现，前台回调接口注册与实现，并且将服务连接起来 */
        try {
            String path = System.getenv("ONOS_ROOT");
            String file = path + "/soon/resources/ml_platform.properties";
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            String addr = properties.getProperty("server_uri");
            int websocketId = platformService.addWebsocketConnection(URI.create(addr));
            if (websocketId != -1) {
                // 如果websocket连接建立成功
                InternalPlatformCallback ipc = new InternalPlatformCallback();  // 新建一个平台回调接口
                if (platformService.registerCallback(websocketId, ipc)) {  // 注册平台回调接口
                    // 注册一个和websocketId映射的模型id
                    int modelId = platformService.requestNewModelId(websocketId, mlAlgorithmType);
                    if (modelId != -1) {  // 如果注册成功
                        ipc.modelId = modelId;  // 指定回调接口的modelId值
                        int msgId = platformService.sendMLConfig(websocketId, mlAlgorithmConfig);
                        if (msgId != -1) {  // 如果模型配置消息发送成功
                            // 进行相关配置
                            ipcs.put(modelId, ipc);
                            fcbs.put(modelId, foregroundCallback);
                            ipc.fcb = foregroundCallback;
                            wsIds.put(modelId, websocketId);
                            configs.put(modelId, mlAlgorithmConfig);
                            return Pair.of(modelId, msgId);
                        }
                    }
                } else {
                    // 如果平台注册失败
                    platformService.unregisterCallback(websocketId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Pair.of(-1, -1);
    }

    /**
     * 为模型modelId传输所有可用的训练集和测试集，并且返回
     * @param modelId 模型id
     * @return Pair.Left表示训练集id的集合，Pair.Right表示测试集id的集合
     */
    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int modelId) {
        // TODO 关于训练集的设置太简单，后期一定要抽象出来
        // 目前该函数只能在初期被调用一次！！！
//        if (!trainIds.isEmpty()) {
//            return null;
//        }
        // 目前来说，只有一个训练集，训练集就是测试集
        int websocketId = wsIds.get(modelId);
        int trainDatasetId = platformService.requestNewTrainDatasetId(websocketId);
        int testDatasetId = platformService.requestNewTestDatasetId(websocketId);
        Set<Integer> trids = Sets.newHashSet(trainDatasetId);
        Set<Integer> teids = Sets.newHashSet(testDatasetId);

        List<Item> trainData = database.queryData("*", "", "edge_load", EdgePredictionItem.class);
        int size = trainData.size();
        trainIds.put(trainDatasetId, size);
        testIds.put(testDatasetId, size);

        SegmentForDataset segmentForDataset = convertToSegmentForDataset(trainData, trainDatasetId, true);
        platformService.sendTrainData(websocketId, segmentForDataset);
        segmentForDataset.setTrainData(false);
        platformService.sendTestData(websocketId, segmentForDataset);
        return Pair.of(trids, teids);
    }


    /**
     *
     * @param trainDatasetId 为模型modelId设置使用的训练集
     * @param modelId
     * @return
     */
    @Override
    public int setDataset(int modelId, int trainDatasetId) {
        if (trainIds.containsKey(trainDatasetId)) {
            // 如果包含，说明该训练集已经发送过去了，可以采用
            int websocketId = wsIds.get(modelId);
            return platformService.sendTrainDatasetId(websocketId, trainDatasetId);
        } else {
            // 如果不包含，说明现在没有这个数据集
            return -1;
        }
    }

    @Override
    public Pair<Boolean, Integer> startTraining(int modelId) {
        int websocketId = wsIds.get(modelId);
        int msgId = platformService.startTrain(websocketId);
        if (msgId == -1) {
            return Pair.of(false, msgId);
        } else {
            return Pair.of(true, msgId);
        }
    }

    @Override
    public Pair<Boolean, Integer> stopTraining(int modelId) {
        int websocketId = wsIds.get(modelId);
        int msgId = platformService.stopTrain(websocketId);
        if (msgId == -1) {
            return Pair.of(false, msgId);
        } else {
            return Pair.of(true, msgId);
        }
    }

    @Override
    public Pair<Boolean, Integer> applyModel(int modelId, int recentItemNum) {
        // 随机选取几个训练集的数据，进行应用
        List<Item> result = database.queryData(
                "*", " limit "+recentItemNum, "edge_load", EdgePredictionItem.class);
        List<List<Double>> input = parseInput(result);
        int websocketId = wsIds.get(modelId);
        int msgId = platformService.applyModel(websocketId, input);
        if (msgId != -1) {
            InternalPlatformCallback pfc = ipcs.get(modelId);
            pfc.inputs.put(msgId, input);
            return Pair.of(false, msgId);
        } else {
            return Pair.of(true, msgId);
        }
    }

    @Override
    public Pair<Boolean, Integer> deleteModel(int modelId) {
        int websocketId = wsIds.get(modelId);
        int msgId = platformService.deleteModel(websocketId);
        if (msgId == -1) {
            return Pair.of(false, msgId);
        } else {
            configs.remove(modelId);
            return Pair.of(true, msgId);
        }
    }

    @Override
    public Pair<Boolean, Integer> getModelEvaluation(int modelId, int testDatasetId) {
        if (testIds.containsKey(testDatasetId)) {
            int websocketId = wsIds.get(modelId);
            int msgId = platformService.evalModel(websocketId, testDatasetId);
            if (msgId == -1) {
                return Pair.of(false, msgId);
            } else {
                return Pair.of(true, msgId);
            }
        } else {
            return Pair.of(false, -1);
        }
    }

    @Override
    public MLAppType getServiceName() {
        return serviceName;
    }

    @Override
    public MLAlgorithmConfig getModelConfig(int modelId) {
        return configs.get(modelId);
    }

    @Override
    public Statistics getAppStatics() {
        Statistics rtn = new Statistics(trainIds, testIds, configs);
        return rtn;
    }

    @Override
    public List<Item> updateData(Date begin, Date end, String node, String board, String port, int itemNum) {
        // 该应用不支持这样的查询方式
        return null;
    }

    @Override
    public List<Item> updateData(int offset, int limit) {
        return  database.queryData("*", " offset "+offset+" limit "+limit,
                "edge_load", EdgePredictionItem.class);
    }

    @Override
    public Pair<Boolean, Integer> queryURL(int modelId) {
        int msgId = platformService.queryURL(wsIds.get(modelId));
        if (msgId == -1) {
            return Pair.of(false, msgId);
        } else {
            return Pair.of(true, msgId);
        }
    }


    /**
     * 从data中将input解析出来
     * @param data
     * @return
     */
    List<List<Double>> parseInput(List<Item> data) {
        List<List<Double>> rtn = Lists.newArrayList();
        for (Item i : data) {
            EdgePredictionItem item = (EdgePredictionItem) i;
            List<Double> tmp = Lists.newArrayList();
            double[] edge_id = item.getEdge_id();
            for (double j : edge_id) {
                tmp.add(j);
            }
            tmp.add(item.getTimepoint());
            double[] in = item.getTwo_hours_before();
            for (double j : in) {
                tmp.add(j);
            }
            rtn.add(tmp);
        }
        return rtn;
    }

    /**
     * 将数据转换成SegmentForDataset类型的数据
     * @param data 要被转化的数据
     * @param datasetId 数据集id
     * @param isTrain 是否是训练集
     * @return 转化后的对象
     */
    SegmentForDataset convertToSegmentForDataset(List<Item> data, int datasetId, boolean isTrain) {
        SegmentForDataset rtn = new SegmentForDataset();
        rtn.setDatasetId(datasetId);
        rtn.setTrainData(isTrain);
        rtn.setPartOfDataset(false);
        rtn.setIndex(0);
        List<List<Double>> inputData = Lists.newArrayList();
        List<List<Double>> outputData = Lists.newArrayList();
        for (Item lpi : data) {
            EdgePredictionItem item = (EdgePredictionItem) lpi;
            List<Double> tmpIn = Lists.newArrayList();
            List<Double> tmpOut = Lists.newArrayList();
            // 增加edge_id
            for (double i : item.getEdge_id()) {
                tmpIn.add(i);
            }
            // 增加时间
            tmpIn.add(item.getTimepoint());
            // 增加过去两个小时
            for (double i : item.getTwo_hours_before()) {
                tmpIn.add(i);
            }
            // 增加模型输出
            for (double i : item.getOne_hour_after()) {
                tmpOut.add(i);
            }
            inputData.add(tmpIn);
            outputData.add(tmpOut);
        }
        rtn.setInput(inputData);
        rtn.setOutput(outputData);
        return rtn;
    }
    /**
     * 平台回调接口的内部实现类
     */
    class InternalPlatformCallback implements PlatformCallback {
        int modelId = -1;
        ForegroundCallback fcb = null;

        Map<Integer, List<List<Double>>> inputs = Maps.newHashMap();

        @Override
        public void trainDatasetTransEnd(int msgId, int trainDatasetId) {
            // 训练集传输结束
            fcb.trainDatasetTransEnd(msgId, trainDatasetId);
        }

        @Override
        public void testDatasetTransEnd(int msgId, int testDatasetId) {
            // 测试集传输结束
            fcb.testDatasetTransEnd(msgId, testDatasetId);
        }

        @Override
        public void trainingEnd(int msgId) {
            // 训练结束
            fcb.trainEnd(msgId);
        }

        @Override
        public void ResultUrl(int msgId, URI uri) {
            // 获取URI的结果
            fcb.ResultUrl(msgId, uri);
        }

        @Override
        public void intermediateResult(int msgId, MonitorData monitorData) {
            // 中间训练结果的提示
            fcb.intermediateResult(msgId, monitorData);
        }

        @Override
        public void applyResult(int msgId, List<List<Double>> list) {
            // 模型应用的结果
            List<String> result = Lists.newArrayList();
            for (List<Double> i : list) {
                result.add(i.toString());
            }
            List<List<Double>> tmp = inputs.get(msgId);
            List<Item> data = parse(tmp);
            fcb.appliedModelResult(msgId, data, result);
        }


        @Override
        public void evalResult(int msgId, int testDatasetId, List<List<Double>> results) {
            // 评估模型在测试集上的应用结果
            // TODO 这个还不知道该怎么评估,直接先给1.0
            fcb.modelEvaluation(msgId, "Not implemented yet, Hahahahahaha");

        }


        @Override
        public void configException(int msgId, String description) {
            // 异常信息触发
            // TODO 还没有进行处理的机制
            fcb.operationFailure(msgId, description);
        }

        @Override
        public void runningException(int msgId, String description) {
            // 运行异常信息触发
            // TODO 还没有进行处理的机制
            fcb.operationFailure(msgId, description);
        }

        /**
         * 将inp解析成List<Item>对象
         * @param inp
         * @return
         */
        List<Item> parse(List<List<Double>> inp) {
            List<Item> rtn = Lists.newArrayList();
            for (List<Double> list : inp) {
                EdgePredictionItem item = new EdgePredictionItem();
                double[] edge_id = new double[15];
                double[] two_hours_before = new double[30];
                double[] one_hour_after = new double[15];
                for (int i=0; i<14; i++) {
                    edge_id[i] = list.get(i);
                }
                for (int i=0; i<30; i++) {
                    two_hours_before[i] = list.get(i+15);
                }
                if (list.size() == 60) {  // 如果包含label数据
                    for (int i = 0; i < 15; i++) {
                        one_hour_after[i] = list.get(i + 45);
                    }
                } else {
                    one_hour_after = null;
                }
                item.setEdge_id(edge_id);
                item.setTimepoint(list.get(14));
                item.setTwo_hours_before(two_hours_before);
                item.setOne_hour_after(one_hour_after);
                rtn.add(item);
            }
            return rtn;
        }
    }
}
