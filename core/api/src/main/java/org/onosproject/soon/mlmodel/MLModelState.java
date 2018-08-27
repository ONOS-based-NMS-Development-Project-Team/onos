package org.onosproject.soon.mlmodel;

/**
 * 模型当前所处的状态
 */
public enum MLModelState {
    CONFIGURED, // 已经配置好了算法参数，但是尚未在任何数据集上训练出模型
    RUNNING, // 正在运行算法
    STOPPED, // 已经配置好了算法参数，而且已经有在数据集上训练出来的模型
    OFFINELINE, // 离线状态。表示该模型的迭代已经结束，不再进行后续的训练
}
