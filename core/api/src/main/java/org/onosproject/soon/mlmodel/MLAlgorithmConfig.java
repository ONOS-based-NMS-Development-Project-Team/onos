package org.onosproject.soon.mlmodel;


/**
 * 所有机器学习模型的父类
 */
public class MLAlgorithmConfig {
    public final MLAlgorithmType type;


    public MLAlgorithmConfig(MLAlgorithmType type) {
        this.type = type;
    }
}
