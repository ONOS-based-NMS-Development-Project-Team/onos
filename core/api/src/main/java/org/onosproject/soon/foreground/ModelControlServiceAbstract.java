package org.onosproject.soon.foreground;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.platform.PlatformCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.net.URI;
import java.util.*;

/**
 *
 */
public abstract class ModelControlServiceAbstract implements ModelControlService{


    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final MLAppType serviceName;
    protected final String tableName;
    protected final Class itemClass;
    protected final Class platformCallbackClass;
    protected final DatabaseAdapter database;
    protected final MLPlatformService platformService;
    // modelId和模型配置之间的映射关系
    protected Map<Integer, MLAlgorithmConfig> configs = Maps.newConcurrentMap();
    // modelId和平台回调接口的映射关系
    protected Map<Integer, PlatformCallback> ipcs = Maps.newConcurrentMap();
    // modelId和前台回调接口的映射关系
    protected Map<Integer, ForegroundCallback> fcbs = Maps.newConcurrentMap();
    // modelId和websocketId的映射关系
    protected Map<Integer, Integer> wsIds = Maps.newConcurrentMap();
    // 该应用类型下的可用训练集id集合.Map.key表示训练集id,Map.value表示训练集的size
    protected Map<Integer, Integer> trainIds = Maps.newConcurrentMap();
    // 该应用类型下的可用测试集.Map.key表示测试集id, Map.value表示测试集的size
    protected Map<Integer, Integer> testIds = Maps.newConcurrentMap();

    public ModelControlServiceAbstract(MLAppType serviceName, String tableName, Class itemClass,
                                       Class platformCallbackClass, DatabaseAdapter database,
                                       MLPlatformService platformService) {
        this.serviceName = serviceName;
        this.tableName = tableName;
        this.itemClass = itemClass;
        this.platformCallbackClass = platformCallbackClass;
        this.database = database;
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
                PlatformCallback ipc = (PlatformCallback) platformCallbackClass.newInstance();  // 新建一个平台回调接口
                if (platformService.registerCallback(websocketId, ipc)) {  // 注册平台回调接口
                    // 注册一个和websocketId映射的模型id
                    int modelId = platformService.requestNewModelId(websocketId, mlAlgorithmType);
                    if (modelId != -1) {  // 如果注册成功
                        ipc.setModelId(modelId);  // 指定回调接口的modelId值
                        int msgId = platformService.sendMLConfig(websocketId, mlAlgorithmConfig);
                        if (msgId != -1) {  // 如果模型配置消息发送成功
                            // 进行相关配置
                            ipcs.put(modelId, ipc);
                            fcbs.put(modelId, foregroundCallback);
                            ipc.setForegroundCallback(foregroundCallback);
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
    public abstract Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int modelId);


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
                "*", " limit "+recentItemNum, tableName, itemClass);
        List<List<Double>> input = parseInput(result);
        int websocketId = wsIds.get(modelId);
        int msgId = platformService.applyModel(websocketId, input);
        if (msgId != -1) {
            PlatformCallback pfc = ipcs.get(modelId);
            pfc.getInputs().put(msgId, input);
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
                tableName, itemClass);
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
    public abstract List<List<Double>> parseInput(List<Item> data);

}
