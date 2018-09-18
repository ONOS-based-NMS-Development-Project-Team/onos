package org.onosproject.soon.foreground;


import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.platform.MLPlatformService;

import java.net.URI;
import java.util.List;

/**
 * 和前台应用ml_show之间的回调接口。详情可见{@link ModelControlService}类的模型控制方法
 */
public interface ForegroundCallback {


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 成败判定方法 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /**
     * 当以下几种{@link ModelControlService}的方法调用失败或者发生异常，则调用该方法作为回调：
     * 1. {@link ModelControlService#addNewModel(MLAlgorithmType, MLAlgorithmConfig, ForegroundCallback)}
     * 2. {@link ModelControlService#startTraining(int)}
     * 3. {@link ModelControlService#stopTraining(int)}
     * 4. {@link ModelControlService#applyModel(int, int)}
     * 5. {@link ModelControlService#deleteModel(int)}
     * 6. {@link ModelControlService#getModelEvaluation(int, int)}
     * @param msgId 发送消息的msgId
     * @param description 异常描述
     */
    void operationFailure(int msgId, String description);


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 获取结果方法 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/


    /**
     * 方法{@link ModelControlService#applyModel(int, int)}的回调
     * @param input 模型输入数据
     * @param output 模型返回结果
     */
    void appliedModelResult(int msgId, List<Item> input, List<String> output);


    /**
     * 方法{@link ModelControlService#getModelEvaluation(int, int)}的回调方法
     * @param result 测试集评估结果
     */
    void modelEvaluation(int msgId, String result);

    /**
     * 方法{@link ModelControlService#transAvailableDataset(int)}的回调方法,表示某个训练集传输结束
     * @param trainDatasetId 训练集id
     */
    void trainDatasetTransEnd(int msgId, int trainDatasetId);

    /**
     * 方法{@link ModelControlService#transAvailableDataset(int)}的回调方法,表示某个测试集传输结束
     * @param testDatasetId 测试集id
     */
    void testDatasetTransEnd(int msgId, int testDatasetId);

    /**
     * 获取到URL的通知。接收到URL_NOTIFY时该函数被触发。{@link MLPlatformService#queryURL(int)}的回调方法
     */
    void ResultUrl(int msgId, URI uri);


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 训练进度提示 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/

    /**
     * 方法{@link ModelControlService#startTraining(int)}的回调方法
     *
     */
    void trainEnd(int msgId);


    /**
     * 获取到训练过程中的关键指标的变化。接收到PROCESS_NOTIFY时该函数被触发
     * 该方法调用一次，表示中间结果的一次采集和传递。
     * 介于训练开始和训练结束之间的通知。
     */
    void intermediateResult(int msgId, MonitorData monitorData);


    void originData(SegmentForDataset segmentForDataset);
}
