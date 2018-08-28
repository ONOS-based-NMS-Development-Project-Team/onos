package org.onosproject.soon;


import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLModelConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 模型控制服务
 */
public interface ModelControlService {


    /**
     * 获取服务所属的具体应用的类型
     * @return 具
     */
    MLAppType getServiceName();

    /**
     增加新的模型
     @param type : 神经网络类型标识，包含全连接神经网络、卷积神经网络、循环神经网络。
     @param trainDatasetId : 该神经网络模型的版本号。对于同一个训练任务，根据训练集的更新，训练相关参数设置不同，会有多个版本号出现。
     @param config :  模型具体配置。
     @return :  增加的神经网络模型的id。如果为-1,表示模型增加失败
     **/
    int addNewModel(MLAlgorithmType type, int trainDatasetId, MLModelConfig config);


    /**
     * 开始进行模型训练
     * @param type 机器学习算法类型
     * @param id 算法的指定配置参数的实现
     * @param trainDatasetId 训练数据集的id
     * @param testDatasetIds 测试集id的集合。表示在多少个测试集上进行测试。如果为null，表示不进行测试。
     * @return 是否开始成功。如果当前模型已经处于训练状态，则返回false。
     */
    boolean startTraining(MLAlgorithmType type, int id, int trainDatasetId, List<Integer> testDatasetIds);

    /**
     * 中断模型的训练过程
     * @param type 机器学习算法类型
     * @param id 算法的指定配置参数的实现
     * @param trainDatasetId 训练数据集的id
     * @return 是否中断成功。如果该模型本来就不处于训练状态，则仍然返回true
     */
    boolean stopTraining(MLAlgorithmType type, int id, int trainDatasetId);

    /**
     * 获取指定模型应用的结果
     * @param type 算法类型
     * @param id 模型id
     * @param trainDatasetId 训练集id
     * @param recentItemNum 将模型应用于最近发生的几次数据中去
     * @return 返回模型应用的结果。return.size()==recentItemNum
     */
    List<String> getAppliedResult(MLAlgorithmType type, int id, int trainDatasetId, int recentItemNum);


    /**
     * 获取模型在测试集上的评估结果
     * @param type 算法类型
     * @param id 模型id
     * @param trainDatasetId 训练集id
     * @param testDatasetId 测试集id
     * @param topk 在测试集上的topk要求
     * @return 在测试集上的topk准确率
     */
    List<Double> getModelEvaluation(MLAlgorithmType type, int id, int trainDatasetId, int testDatasetId, List<Integer> topk);


    /**
     * 更新数据集，用于前台展示
     * @param begin 开始时间
     * @param end 结束时间
     * @param node 节点
     * @param board 节点的单板，可为null
     * @param port 单板的端口，可为null
     * @param itemNum 获取的数据条目数
     * @return 返回Item的列表。
     */
    List<Item> updateData(Date begin, Date end, String node, String board, String port, int itemNum);


    /**
     * 更新数据集，用于前台展示。实现分页查询
     * @param offset
     * @param limit
     * @return
     */
    List<Item> updateData(int offset, int limit);


    /**
     * 获取模型配置
     * @param type 机器学习算法类型
     * @param id 模型id，表示指定算法的不同配置的实现
     * @param annotations 相关解释
     * @return 返回指定模型的配置
     */
    MLModelConfig getModelConfig(MLAlgorithmType type, int id, Map<String, String> annotations);


    /**
     * 获取该应用的训练集，测试集，已训练模型等等的信息
     * @return 统计信息
     */
    Statistics getAppStatics();
}
