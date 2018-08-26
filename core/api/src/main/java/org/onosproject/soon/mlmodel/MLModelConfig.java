package org.onosproject.soon.mlmodel;


/**
 * 所有机器学习模型的父类
 */
public class MLModelConfig {
    public final MLModelType type;
    public final int id;


    public MLModelConfig(MLModelType type, int id) {
        this.type = type;
        this.id = id;
    }
}
