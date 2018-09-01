package org.onosproject.soon;

import com.google.common.base.Objects;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;

import java.util.List;
import java.util.Map;

/**
 * 数据集和相关模型的统计结果类
 */
public class Statistics {
    // 数据集，Pair.Left表示训练集的id，Pair.Right表示训练集的size
    private Map<Integer, Integer> trainDataset;
    // 数据集，Pair.Left表示测试集的id，Pair.Right表示测试集的size
    private Map<Integer, Integer> testDataset;
    // 模型的统计结果. Map.key表示模型id,Map.value表示模型配置
    private Map<Integer, MLAlgorithmConfig> modelConfigs;

    public Statistics(Map<Integer, Integer> trainDataset, Map<Integer, Integer> testDataset,
                      Map<Integer, MLAlgorithmConfig> modelConfigs) {
        this.trainDataset = trainDataset;
        this.testDataset = testDataset;
        this.modelConfigs = modelConfigs;
    }

    public Map<Integer, Integer> getTrainDataset() {
        return trainDataset;
    }

    public void setTrainDataset(Map<Integer, Integer> trainDataset) {
        this.trainDataset = trainDataset;
    }

    public Map<Integer, Integer> getTestDataset() {
        return testDataset;
    }

    public void setTestDataset(Map<Integer, Integer> testDataset) {
        this.testDataset = testDataset;
    }

    public Map<Integer, MLAlgorithmConfig> getModelConfigs() {
        return modelConfigs;
    }

    public void setModelConfigs(Map<Integer, MLAlgorithmConfig> modelConfigs) {
        this.modelConfigs = modelConfigs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return Objects.equal(trainDataset, that.trainDataset) &&
                Objects.equal(testDataset, that.testDataset) &&
                Objects.equal(modelConfigs, that.modelConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(trainDataset, testDataset, modelConfigs);
    }
}
