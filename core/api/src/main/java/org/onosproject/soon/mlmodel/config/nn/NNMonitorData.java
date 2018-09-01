package org.onosproject.soon.mlmodel.config.nn;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.MonitorData;

/**
 * 神经网络的训练过程中的关键性能数据
 */
public class NNMonitorData implements MonitorData {

    private double loss=-1;
    private long remainingTime=-1;
    private double precision=-1;


    public double getLoss() {
        return loss;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NNMonitorData that = (NNMonitorData) o;

        return new EqualsBuilder()
                .append(loss, that.loss)
                .append(remainingTime, that.remainingTime)
                .append(precision, that.precision)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(loss)
                .append(remainingTime)
                .append(precision)
                .toHashCode();
    }


    @Override
    public String getDescription() {
        return "NNMonitorData{" +
                "loss=" + loss +
                ", remainingTime=" + remainingTime +
                ", precision=" + precision +
                '}';
    }
}
