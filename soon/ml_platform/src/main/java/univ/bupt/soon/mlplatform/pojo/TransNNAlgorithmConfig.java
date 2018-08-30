package univ.bupt.soon.mlplatform.pojo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.mlmodel.config.nn.*;

import java.util.List;

public class TransNNAlgorithmConfig {
    // 输入神经元个数
    private int inputNum;
    // 输出神经元个数
    private int outputNum;
    // 隐藏层每层神经元个数
    private List<Integer> hiddenLayer;
    // 激活函数
    private String activationFunction;
    // 算法weights参数初始化方式
    private String weightInit;
    // 算法bias参数初始化方式
    private String biasInit;
    // loss 函数
    private String lossFunction;
    // batch
    private int batchSize;
    // epoch
    private int epoch;
    // 优化器
    private String optimizer;
    // 初始化学习率
    private double learningRate;
    // 学习率调整方法
    private String lrAdjust;
    // dropout参数
    private double dropout;


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

    public String getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(String activationFunction) {
        this.activationFunction = activationFunction;
    }

    public String getWeightInit() {
        return weightInit;
    }

    public void setWeightInit(String weightInit) {
        this.weightInit = weightInit;
    }

    public String getBiasInit() {
        return biasInit;
    }

    public void setBiasInit(String biasInit) {
        this.biasInit = biasInit;
    }

    public String getLossFunction() {
        return lossFunction;
    }

    public void setLossFunction(String lossFunction) {
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

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public String getLrAdjust() {
        return lrAdjust;
    }

    public void setLrAdjust(String lrAdjust) {
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

        TransNNAlgorithmConfig that = (TransNNAlgorithmConfig) o;

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



    /**
     * 类型转换，方便进行JSON序列化
     * @return
     */
    public static TransNNAlgorithmConfig trans(NNAlgorithmConfig nn) {
        TransNNAlgorithmConfig rtn = new TransNNAlgorithmConfig();
        rtn.activationFunction = nn.getActivationFunction().getName();
        rtn.batchSize = nn.getBatchSize();
        rtn.biasInit = nn.getBiasInit().getName();
        rtn.dropout = nn.getDropout();
        rtn.epoch = nn.getEpoch();
        rtn.hiddenLayer = nn.getHiddenLayer();
        rtn.inputNum = nn.getInputNum();
        rtn.learningRate = nn.getLearningRate();
        rtn.lossFunction = nn.getLossFunction().getName();
        rtn.lrAdjust = nn.getLrAdjust().getName();
        rtn.optimizer = nn.getOptimizer().getName();
        rtn.outputNum = nn.getOutputNum();
        rtn.weightInit = nn.getWeightInit().getName();
        return rtn;
    }
}
