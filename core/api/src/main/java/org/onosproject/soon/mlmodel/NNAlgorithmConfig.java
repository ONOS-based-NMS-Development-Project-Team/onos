package org.onosproject.soon.mlmodel;

import org.onosproject.soon.mlmodel.MLAlgorithmType;

import java.util.List;

import static org.onosproject.soon.mlmodel.MLAlgorithmType.FCNNModel;


/**
 * 神经网络模型的参数，留给赵子飘实现
 */
public class NNAlgorithmConfig extends MLModelConfig {

    private int inpLayNeuNum; // 输入层神经元个数
    private int hidLayNum; // 隐藏层层数
    //列表中的值为各个隐藏层的神经元个数
    private List<Integer> hidLayNeuNum;
    private int outpLayNeuNum;//输出神经元个数
    private double learningRate;//学习率，区间为(0, 1)
    private int weights;//权重初始化方法，1代表
    private int bias;//偏置初始化方法，1代表
    private List<Integer> activation;//每层的激活函数，1代表ReLU，2代表sigmoid，3代表LeakyReLU，4代表PReLU，5代表tanh
    private int steps;
    // loss function definition
    private String lossFun;//损失函数

    public NNAlgorithmConfig(MLAlgorithmType type) {
        super(type);
    }

    public NNAlgorithmConfig(MLAlgorithmType type, int inpLayNeuNum, int hidLayNum,
                             List<Integer> hidLayNeuNum, int outpLayNeuNum, double learningRate,
                             int weights, int bias, List<Integer> activation,
                             int steps, String lossFun) {
        super(type);
        this.inpLayNeuNum = inpLayNeuNum;
        this.hidLayNum = hidLayNum;
        this.hidLayNeuNum = hidLayNeuNum;
        this.outpLayNeuNum = outpLayNeuNum;
        this.learningRate = learningRate;
        this.weights = weights;
        this.bias = bias;
        this.activation = activation;
        this.steps = steps;
        this.lossFun = lossFun;
    }

    public int getInpLayNeuNum() {
        return inpLayNeuNum;
    }

    public void setInpLayNeuNum(int inpLayNeuNum) {
        this.inpLayNeuNum = inpLayNeuNum;
    }

    public int getHidLayNum() {
        return hidLayNum;
    }

    public void setHidLayNum(int hidLayNum) {
        this.hidLayNum = hidLayNum;
    }

    public List<Integer> getHidLayNeuNum() {
        return hidLayNeuNum;
    }

    public void setHidLayNeuNum(List<Integer> hidLayNeuNum) {
        this.hidLayNeuNum = hidLayNeuNum;
    }

    public int getOutpLayNeuNum() {
        return outpLayNeuNum;
    }

    public void setOutpLayNeuNum(int outpLayNeuNum) {
        this.outpLayNeuNum = outpLayNeuNum;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public int getWeights() {
        return weights;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public int getBias() {
        return bias;
    }

    public void setBias(int bias) {
        this.bias = bias;
    }

    public List<Integer> getActivation() {
        return activation;
    }

    public void setActivation(List<Integer> activation) {
        this.activation = activation;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getLossFun() {
        return lossFun;
    }

    public void setLossFun(String lossFun) {
        this.lossFun = lossFun;
    }
}
