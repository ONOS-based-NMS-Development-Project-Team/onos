package org.onosproject.soon;


import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.osgi.service.dmt.Uri;

import java.util.List;

/**
 * 通过与TensorFlow的交互，实现对onos上的内置机器学习平台。
 * 该类表示机器学习平台对外所能提供的服务
 */
public interface MLPlatformService {
    /**
     * 判断是否具备预测的条件
     * @return 是否可以进行预测，如果数据集缺失等，返回false
     */
    boolean getStatus(); // 缺入参

    /**
     * 提供当前系统中数据集的相关信息
     * @param id 训练好的模型的id
     * @param trainDatasetId 训练集id
     * @param testDatasetId 测试集id
     * @param describ 关于数据集的简单描述
     * @return 返回各种数据集的数量和编号以及相关描述
     */
    List<Item> dataSetsInfo( List<Integer> id, List<Integer> trainDatasetId,  List<Integer> testDatasetId, List<String> describ); // 废了

    /**
     * 更新系统中数据集信息，并进行编号
     * @param id 模型id
     * @param trainDatasetId 训练集id
     * @param testDatasetId 测试集id
     * @return 系统中所有数据集的信息
     */
    List<Item> updateDataSetsInfo(List<Integer> id, List<Integer> trainDatasetId,  List<Integer> testDatasetId);


    /**
     * 向机器学习平台发送训练集
     * @param trainAddress 训练集uri地址
     * @param trainDatasetId 训练集id
     * @param describ 简单描述
     * @return 是否发送成功
     */
    boolean sendTrainDataSet(Uri trainAddress, int trainDatasetId, String describ);

    /**
     * 向机器学习平台发送测试集
     * @param testAddress 测试集uri地址
     * @param testDatasetId 测试集id
     * @param describ 简单描述
     * @return 是否发送成功
     */
    boolean sendTestDataSet(Uri testAddress, int testDatasetId, String describ);

    /**
     * 初始化训练的参数，对算法、类型等进行选择
     * @param ServiceType 需要进行预测的业务类型（重构、定位、预测）
     * @param Type 机器学习算法类型
     * @param OptimizerType 优化器类型
     * @return 是否设置成功，如果参数初始化设置失败，返回false
     */
    boolean setInitConfig(int ServiceType, MLAlgorithmType Type, int OptimizerType);

    /**
     * 执行对相关业务的预测
     * @return 不同算法的准确率
     */
    List<Double> startResultPredict();

    /**
     * 该接口用来保存相关模型设置、预测结果等信息为文件
     * @return 是否存储成功
     */
    boolean saveResult();

    /**
     * 训练完成后机器学习平台向App返回相关提示信息
     * @param type
     * @param id
     * @param trainDatasetId
     * @param testDatasetId
     * @param describ
     * @return 完成训练的数据信息
     */
    List<Item> trainningFinishNotice(MLAlgorithmType type, int id, int trainDatasetId, int testDatasetId, String describ);
}
