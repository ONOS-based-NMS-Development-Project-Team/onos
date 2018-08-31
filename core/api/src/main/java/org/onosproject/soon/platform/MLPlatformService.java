package org.onosproject.soon.platform;

import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;

import java.net.URI;
import java.util.List;

/**
 * 通过与TensorFlow的交互，实现对onos上的内置机器学习平台。
 * 该类提供单向的方法调用;反向的方法回复可见{@link PlatformCallback}。两者通过发送消息的id号进行匹配，共同表示机器学习平台对外所能提供的服务。
 * 用法：如果要调用该服务，需要依次增加websocket连接，注册回调，请求训练集、测试集、模型id，然后才能进行正常的模型控制。
 *
 */
public interface MLPlatformService {

    /**
     * 增加一个websocket连接
     * @param address websocket连接地址
     * @return 该websocket连接的id号。以后发送消息的时候需要指定websocket连接号。如果为-1表示id号增加失败。
     */
    int addWebsocketConnection(URI address);


    /**
     * 注册一个回调接口，用于接收指定websocket的回调结果。一个websocket连接只能绑定一个回调接口
     * @param callback 回调类
     * @return 是否注册成功
     */
    boolean registerCallback(int websocketId, PlatformCallback callback);

    /**
     * 注销一个回调接口，用于接收指定websocket的回调结果
     * @return 是否注册成功
     */
    boolean unregisterCallback(int websocketId);

    /**
     * 向平台请求分配一个训练集id
     * @return 申请到的训练集id号。-1表示分配失败
     */
    int requestNewTrainDatasetId(int websocketId);

    /**
     * 向平台请求分配一个测试集id
     * @return 申请到的测试集id号。-1表示分配失败
     */
    int requestNewTestDatasetId(int websocketId);

    /**
     * 向平台请求分配一个模型id
     * @param algType 模型采用的算法类型。通过这个类型可以找到相关的映射类型。
     * @return 申请到的模型id号。-1表示分配失败
     */
    int requestNewModelId(int websocketId, MLAlgorithmType algType);

    /**
     * 发送训练集数据
     * @param trainData 训练集数据
     * @return 发送消息的编号
     */
    int sendTrainData(int websocketId, SegmentForDataset trainData);


    /**
     * 发送测试集数据
     * @param testData 测试集数据
     * @return 发送消息的编号
     */
    int sendTestData(int websocketId, SegmentForDataset testData);

    /**
     * 发送具体的模型配置。
     * 模型所处的状态不会被解析使用
     * @param config 模型配置
     * @return 发送消息的编号。
     */
    int sendMLConfig(int websocketId, MLAlgorithmConfig config);

    /**
     * 改变训练数据集的id。
     * @param id 训练数据集id
     * @return 发送消息的编号
     */
    int sendTrainDatasetId(int websocketId, int id);

    /**
     * 开始训练
     * @return 发送消息的编号
     */
    int startTrain(int websocketId);

    /**
     * 停止训练
     * @return 发送消息的编号
     */
    int stopTrain(int websocketId);

    /**
     * 请求Tensorboard的URL
     * @return 发送消息的编号
     */
    int queryURL(int websocketId);

    /**
     * 应用模型，得到结果。
     * @param input 输入参数。
     * @return 发送消息的编号
     */
    int applyModel(int websocketId, List<List<Double>> input);

    /**
     * 在远端删除该模型
     * @return 发送消息的编号
     */
    int deleteModel(int websocketId);

    /**
     * 在远端删除训练集
     * @param trainDataId 训练集id
     * @return 发送消息的编号
     */
    int deleteTrainDataset(int websocketId, int trainDataId);

    /**
     * 在远端删除测试集
     * @param testDataId 测试集id
     * @return 发送消息的编号
     */
    int deleteTestDataset(int websocketId, int testDataId);

    /**
     * 查询平台，目前是否在远端已经存在id为trainDatasetId的训练集
     * @param trainDatasetId 训练集id
     * @return 是否存在
     */
    boolean containTrainId(int trainDatasetId);

    /**
     * 查询平台，目前是否在远端已经存在id为testDatasetId的测试集
     * @param testDatasetId 测试集id
     * @return 是否存在
     */
    boolean containTestId(int testDatasetId);

    /**
     * 查询平台，目前是否在远端已经存在id为modelId的模型
     * @param modelId 模型id
     * @return 是否存在
     */
    boolean containModelId(int modelId);

}
