package org.onosproject.soon.platform;


import org.onosproject.soon.MonitorData;

import java.net.URI;
import java.util.List;

/**
 * 平台的回调接口。可见{@link MLPlatformService}。
 */
public interface PlatformCallback {

    /**
     * 训练集传输结束时的消息通知。
     * @param msgId 消息id
     * @param trainDatasetId 传输的训练集id
     */
    void trainDatasetTransEnd(int msgId, int trainDatasetId);

    /**
     * 测试集传输结束时的消息通知。
     * @param msgId 消息id
     * @param testDatasetId 传输的测试集id
     */
    void testDatasetTransEnd(int msgId, int testDatasetId);

    /**
     * 训练结束时的消息通知。接收到END_NOTIFY时该函数被触发。
     * 和{@link MLPlatformService#startTrain(int)} 方法是一对
     */
    void trainingEnd(int msgId);

    /**
     * 获取到URL的通知。接收到URL_NOTIFY时该函数被触发。
     * 和{@link MLPlatformService#queryURL(int)}方法是一对
     */
    void ResultUrl(int msgId, URI uri);


    /**
     * 获取到训练过程中的关键指标的变化。接收到PROCESS_NOTIFY时该函数被触发
     * 该方法调用一次，表示中间结果的一次采集和传递。
     * 介于训练开始和训练结束之间的通知。
     */
    void intermediateResult(int msgId, MonitorData monitorData);

    /**
     * 模型应用结果。接收到MODEL_APPLY_NOTIFY时该函数被触发。
     * @param results 应用结果
     */
    void applyResult(int msgId, List<List<Double>> results);

    /**
     * 模型评估结果。
     * @param msgId 消息id
     * @param testDatasetId 测试集id
     * @param results 原数据标签
     */
    void evalResult(int msgId, int testDatasetId, List<List<Double>> results);


    /**
     * 配置异常。在从CONFIGURING状态转变为RUNNING状态时调用的
     * {@link MLPlatformService#startTrain(int)}方法时，可能会触发的错误。
     * @param description 异常描述。
     */
    void configException(int msgId, String description);

    /**
     * 训练异常。在RUNNING状态时可能触发的错误。
     * @param description 异常描述。
     */
    void runningException(int msgId, String description);
}
