package org.onosproject.soon.foreground;


import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;

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
     * 1. {@link ModelControlService#addNewModel(MLAlgorithmType, int, MLAlgorithmConfig)}
     * 2. {@link ModelControlService#startTraining(MLAlgorithmType, int, int, List)}
     * 3. {@link ModelControlService#stopTraining(MLAlgorithmType, int, int)}
     * 4. {@link ModelControlService#applyModel(MLAlgorithmType, int, int, int)}
     * 5. {@link ModelControlService#deleteModel(MLAlgorithmType, int)}
     * 6. {@link ModelControlService#getModelEvaluation(MLAlgorithmType, int, int)}
     * @param msgId 发送消息的msgId
     */
    void operationFailure(int msgId);


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 获取结果方法 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/


    /**
     * 方法{@link ModelControlService#applyModel(MLAlgorithmType, int, int, int)}的回调
     * @param input 模型输入数据
     * @param output 模型返回结果
     */
    void appliedModelResult(int msgId, List<Item> input, List<String> output);


    /**
     * 方法{@link ModelControlService#getModelEvaluation(MLAlgorithmType, int, int)}的回调方法
     * @param result 测试集评估结果
     */
    void modelEvaluation(int msgId, double result);


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 训练进度提示 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/

    /**
     * 方法{@link ModelControlService#startTraining(MLAlgorithmType, int, int, List)}的回调方法
     *
     */
    void trainEnd(int msgId);
}
