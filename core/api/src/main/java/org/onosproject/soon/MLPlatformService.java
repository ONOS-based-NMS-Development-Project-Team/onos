package org.onosproject.soon;


import org.onosproject.soon.dataset.DataSegment;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLModelDetail;
import java.net.URL;
import java.util.List;

/**
 * 通过与TensorFlow的交互，实现对onos上的内置机器学习平台。
 * 该类表示机器学习平台对外所能提供的服务
 */
public interface MLPlatformService {

    /**
     * 发送具体的模型细节，包括算法类型，模型配置参数，训练集id等等
     * @param detail
     */
    void sendMLConfig(MLModelDetail detail);


    /**
     * 发送训练集数据
     * @param trainData 训练集数据
     * @return 训练集数据id
     */
    int sendTrainData(List<List<Double>> trainData);


    /**
     * 发送测试集数据
     * @param testData 测试集数据
     * @return 测试集数据id
     */
    int sendTestData(List<List<Double>> testData);

    /**
     * 开始训练
     */
    void startTrain();

    /**
     * 停止训练
     */
    void stopTrain();

    /**
     * 应用模型，得到结果。
     * @param input 输入参数。其中包含的label不予处理
     * @return 返回值
     */
    List<Double> applyModel(List<List<Double>> input);

    /**
     * 在远端删除该模型
     */
    void deleteModel();

    /**
     * 在远端删除训练集
     * @param trainDataId
     */
    void deleteTrainDataset(int trainDataId);

    /**
     * 在远端删除测试集
     * @param testDataId
     */
    void deleteTestDataset(int testDataId);

    /**
     * 获取TensorBoard的URL地址
     * @return
     */
    URL getResultURL();
}
