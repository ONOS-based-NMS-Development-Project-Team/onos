package org.onosproject.soon.mlmodel;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 指定类型和指定配置的模型的状态.
 * 一个模型，是由id唯一指定的，算法类型，算法配置，训练集id
 */
public class MLModelDetail {

    private MLAlgorithmConfig config; // 模型配置
    private MLModelState state;  // 模型所处状态
    private int trainDatasetId;  // 训练集id
    private final int id; // 该模型的id
    // Key - 测试集id， Key.Value - 在测试集id上的最好表现
    private Map<Integer, String> performances = Maps.newConcurrentMap();

    public MLAlgorithmConfig getConfig() {
        return config;
    }

    public void setConfig(MLAlgorithmConfig config) {
        this.config = config;
    }

    public MLModelState getState() {
        return state;
    }

    public void setState(MLModelState state) {
        this.state = state;
    }

    public int getTrainDatasetId() {
        return trainDatasetId;
    }

    public void setTrainDatasetId(int trainDatasetId) {
        this.trainDatasetId = trainDatasetId;
    }

    public Map<Integer, String> getPerformances() {
        return performances;
    }

    public void setPerformances(Map<Integer, String> performances) {
        this.performances = performances;
    }

    public int getId() {
        return id;
    }

    public MLModelDetail(MLAlgorithmConfig config, MLModelState state, int trainDatasetId,
                         int id, Map<Integer, String> performances) {
        this.config = config;
        this.state = state;
        this.trainDatasetId = trainDatasetId;
        this.id = id;
        this.performances = performances;
    }
}
