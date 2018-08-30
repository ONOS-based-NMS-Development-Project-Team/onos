package org.onosproject.soon.foreground;


import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;

import java.util.Date;
import java.util.List;

/**
 * 模型控制服务
 */
public interface ModelControlService {


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 模型控制方法 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/

    /**
     增加新的模型。在websocket连接中，会发送/config/model/type和/config/model进行配置。默认返回/config/model的配置消息id
     @param type : 神经网络类型标识，包含全连接神经网络、卷积神经网络、循环神经网络。
     @param trainDatasetId : 该神经网络模型的版本号。对于同一个训练任务，根据训练集的更新，训练相关参数设置不同，会有多个版本号出现。
     @param config :  模型具体配置。
     @return :  Pair.Left表示增加的神经网络模型的id。如果为-1,表示模型增加失败; Pair.Right表示发送的模型配置消息的msgId
     **/
    Pair<Integer, Integer> addNewModel(MLAlgorithmType type, int trainDatasetId, MLAlgorithmConfig config);


    /**
     * 开始进行模型训练
     * @param type 机器学习算法类型
     * @param modelId 算法的指定配置参数的实现
     * @param trainDatasetId 训练数据集的id
     * @param testDatasetIds 测试集id的集合。表示在多少个测试集上进行测试。如果为null，表示不进行测试。
     * @return Pair.Left表示是否开始成功。如果当前模型已经处于训练状态，则返回false。Pair.Right表示发送的训练开始消息的msgId
     */
    Pair<Boolean, Integer> startTraining(MLAlgorithmType type, int modelId, int trainDatasetId, List<Integer> testDatasetIds);

    /**
     * 中断模型的训练过程
     * @param type 机器学习算法类型
     * @param id 算法的指定配置参数的实现
     * @param trainDatasetId 训练数据集的id
     * @return Pair.Left表示是否终端成功。如果当前模型不可中断，则返回false。Pair.Right表示发送的训练开始消息的msgId
     */
    Pair<Boolean, Integer> stopTraining(MLAlgorithmType type, int id, int trainDatasetId);

    /**
     * 请求指定模型应用的结果
     * @param type 算法类型
     * @param modelId 模型id
     * @param trainDatasetId 训练集id
     * @param recentItemNum 将模型应用于最近发生的几次数据中去
     * @return 发送消息的msgId
     */
    Pair<Boolean, Integer> applyModel(MLAlgorithmType type, int modelId, int trainDatasetId, int recentItemNum);


    /**
     * 请求删除指定模型
     * @param type 算法类型
     * @param modelId 模型id
     * @return 发送消息的msgId
     */
    Pair<Boolean, Integer> deleteModel(MLAlgorithmType type, int modelId);


    /**
     * 获取模型在测试集上的评估结果
     * @param type 算法类型
     * @param id 模型id
     * @param testDatasetId 测试集id
     * @return 发送消息的msgId
     */
    Pair<Boolean, Integer> getModelEvaluation(MLAlgorithmType type, int id, int testDatasetId);



    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 查询相关方法 **********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/


    /**
     * 获取服务所属的具体应用的类型
     * @return 一个类型对应一个ModelControlService的实现类
     */
    MLAppType getServiceName();


    /**
     * 获取模型配置
     * @param type 机器学习算法类型
     * @param modelId 模型id
     * @return 返回指定模型的配置
     */
    MLAlgorithmConfig getModelConfig(MLAlgorithmType type, int modelId);


    /**
     * 获取该应用的训练集，测试集，已训练模型等等的信息
     * @return 统计信息
     */
    Statistics getAppStatics();


    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/
    /******************************************** 数据库查询方法 ********************************************************/
    /**××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××**/


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
     * @param offset 查询数据起点
     * @param limit 查询条目数
     * @return 查询到的数据
     */
    List<Item> updateData(int offset, int limit);


    /**
     * 直接在tableName表上使用sql语句查询
     * @param sql sql语句
     * @param tableName 数据库的表名
     * @return 查询结果
     */
    List<Item> updateData(String sql, String tableName);
}
