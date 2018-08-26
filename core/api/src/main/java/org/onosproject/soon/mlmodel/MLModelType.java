package org.onosproject.soon.mlmodel;


/**
 * 训练所使用的网络模型
 */
public enum MLModelType {
    FCNNModel, // 全连接神经网络
    RNNModel, // RNN模型
    CNNModel, // CNN模型
    LSTMModel,
    RandomForestModel
}
