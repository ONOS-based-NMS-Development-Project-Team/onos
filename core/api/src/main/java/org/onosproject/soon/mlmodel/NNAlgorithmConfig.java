package org.onosproject.soon.mlmodel;

import org.onosproject.soon.mlmodel.MLAlgorithmType;

import java.util.List;

import static org.onosproject.soon.mlmodel.MLAlgorithmType.FCNNModel;


/**
 * 神经网络模型的参数，留给赵子飘实现
 */
public class NNAlgorithmConfig extends MLModelConfig {

    private int neuronsInputLayer;
    private int hiddenLayers;
    //列表中的值为隐藏层的神经元个数
    private List<Integer> neuronsHiddenLayers;
    private int neuronsOutputLayer;
    private double learningRate;
    private List<Double> weights;
    private List<Double> bias;
    private String activation;
    private int steps;
    private String loss;

    public NNAlgorithmConfig(MLAlgorithmType type) {
        super(type);
    }

    public NNAlgorithmConfig(MLAlgorithmType type, int neuronsInputLayer, int hiddenLayers,
                             List<Integer> neuronsHiddenLayers, int neuronsOutputLayer, double learningRate,
                             List<Double> weights, List<Double> bias, String activation, int steps, String loss) {
        super(type);
        this.neuronsInputLayer = neuronsInputLayer;
        this.hiddenLayers = hiddenLayers;
        this.neuronsHiddenLayers = neuronsHiddenLayers;
        this.neuronsOutputLayer = neuronsOutputLayer;
        this.learningRate = learningRate;
        this.weights = weights;
        this.bias = bias;
        this.activation = activation;
        this.steps = steps;
        this.loss = loss;
    }

    //getter and setter
    public int getNeuronsInputLayer() {
        return neuronsInputLayer;
    }

    public void setNeuronsInputLayer(int neuronsInputLayer) {
        this.neuronsInputLayer = neuronsInputLayer;
    }

    public int getHiddenLayers() {
        return hiddenLayers;
    }

    public void setHiddenLayers(int hiddenLayers) {
        this.hiddenLayers = hiddenLayers;
    }

    public List<Integer> getNeuronsHiddenLayers() {
        return neuronsHiddenLayers;
    }

    public void setNeuronsHiddenLayers(List<Integer> neuronsHiddenLayers) {
        this.neuronsHiddenLayers = neuronsHiddenLayers;
    }

    public int getNeuronsOutputLayer() {
        return neuronsOutputLayer;
    }

    public void setNeuronsOutputLayer(int neuronsOutputLayer) {
        this.neuronsOutputLayer = neuronsOutputLayer;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }

    public List<Double> getBias() {
        return bias;
    }

    public void setBias(List<Double> bias) {
        this.bias = bias;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getLoss() {
        return loss;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }

}
