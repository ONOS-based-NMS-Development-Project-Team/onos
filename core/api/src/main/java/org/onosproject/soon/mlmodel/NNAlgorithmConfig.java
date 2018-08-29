package org.onosproject.soon.mlmodel;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.mlmodel.config.nn.*;

import java.util.List;

/**
 * 神经网络模型的参数，留给赵子飘实现
 */
public class NNAlgorithmConfig extends MLAlgorithmConfig {
    // 输入神经元个数
    private int inputNum;
    // 输出神经元个数
    private int outputNum;
    // 隐藏层每层神经元个数
    private List<Integer> hiddenLayer;
    // 激活函数
    private ActivationFunction activationFunction;
    // 算法weights参数初始化方式
    private ParamInit weightInit;
    // 算法bias参数初始化方式
    private ParamInit biasInit;
    // loss 函数
    private LossFunction lossFunction;
    // batch
    private int batchSize;
    // epoch
    private int epoch;
    // 优化器
    private Optimizer optimizer;
    // 初始化学习率
    private double learningRate;
    // 学习率调整方法
    private LRAdjust lrAdjust;
    // dropout参数
    private double dropout;

    public NNAlgorithmConfig(MLAlgorithmType type, int inputNum, int outputNum, List<Integer> hiddenLayer,
                             ActivationFunction activationFunction, ParamInit weightInit, ParamInit biasInit,
                             LossFunction lossFunction, int batchSize, int epoch, Optimizer optimizer,
                             double learningRate, LRAdjust lrAdjust, double dropout) {
        super(type);
        this.inputNum = inputNum;
        this.outputNum = outputNum;
        this.hiddenLayer = hiddenLayer;
        this.activationFunction = activationFunction;
        this.weightInit = weightInit;
        this.biasInit = biasInit;
        this.lossFunction = lossFunction;
        this.batchSize = batchSize;
        this.epoch = epoch;
        this.optimizer = optimizer;
        this.learningRate = learningRate;
        this.lrAdjust = lrAdjust;
        this.dropout = dropout;
    }


    public int getInputNum() {
        return inputNum;
    }

    public void setInputNum(int inputNum) {
        this.inputNum = inputNum;
    }

    public int getOutputNum() {
        return outputNum;
    }

    public void setOutputNum(int outputNum) {
        this.outputNum = outputNum;
    }

    public List<Integer> getHiddenLayer() {
        return hiddenLayer;
    }

    public void setHiddenLayer(List<Integer> hiddenLayer) {
        this.hiddenLayer = hiddenLayer;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public ParamInit getWeightInit() {
        return weightInit;
    }

    public void setWeightInit(ParamInit weightInit) {
        this.weightInit = weightInit;
    }

    public ParamInit getBiasInit() {
        return biasInit;
    }

    public void setBiasInit(ParamInit biasInit) {
        this.biasInit = biasInit;
    }

    public LossFunction getLossFunction() {
        return lossFunction;
    }

    public void setLossFunction(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public LRAdjust getLrAdjust() {
        return lrAdjust;
    }

    public void setLrAdjust(LRAdjust lrAdjust) {
        this.lrAdjust = lrAdjust;
    }

    public double getDropout() {
        return dropout;
    }

    public void setDropout(double dropout) {
        this.dropout = dropout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NNAlgorithmConfig that = (NNAlgorithmConfig) o;

        return new EqualsBuilder()
                .append(inputNum, that.inputNum)
                .append(outputNum, that.outputNum)
                .append(batchSize, that.batchSize)
                .append(epoch, that.epoch)
                .append(learningRate, that.learningRate)
                .append(dropout, that.dropout)
                .append(hiddenLayer, that.hiddenLayer)
                .append(activationFunction, that.activationFunction)
                .append(weightInit, that.weightInit)
                .append(biasInit, that.biasInit)
                .append(lossFunction, that.lossFunction)
                .append(optimizer, that.optimizer)
                .append(lrAdjust, that.lrAdjust)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(inputNum)
                .append(outputNum)
                .append(hiddenLayer)
                .append(activationFunction)
                .append(weightInit)
                .append(biasInit)
                .append(lossFunction)
                .append(batchSize)
                .append(epoch)
                .append(optimizer)
                .append(learningRate)
                .append(lrAdjust)
                .append(dropout)
                .toHashCode();
    }
}
