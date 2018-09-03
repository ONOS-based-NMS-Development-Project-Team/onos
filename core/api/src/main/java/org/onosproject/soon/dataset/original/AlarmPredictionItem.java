package org.onosproject.soon.dataset.original;


import com.google.common.base.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Arrays;

/**
 * CREATE TABLE public.alarm_prediction
 * (
 *   id serial,
 *   train boolean, -- 是否是训练集，true表示是训练集，false表示是测试集
 *   input_type character varying(30),  -- 输入数据的类型
 *   input real[], -- 输入数据，长度不定,随着告警变化而变化
 *   alarm_happen boolean, -- label表示是否发生告警
 *   CONSTRAINT alarm_pred1_primary_key PRIMARY KEY (id)
 * )
 */
public class AlarmPredictionItem implements Item {


    private int id;
    private boolean train;
    private String input_type;
    private double[] input;
    private boolean alarm_happen;
    private int dataid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getInput() {
        return input;
    }

    public void setInput(double[] input) {
        this.input = input;
    }

    public boolean isAlarm_happen() {
        return alarm_happen;
    }

    public void setAlarm_happen(boolean alarm_happen) {
        this.alarm_happen = alarm_happen;
    }

    public boolean isTrain() {
        return train;
    }

    public void setTrain(boolean train) {
        this.train = train;
    }

    public int getDataid() {
        return dataid;
    }

    public void setDataid(int dataid) {
        this.dataid = dataid;
    }

    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        this.input_type = input_type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AlarmPredictionItem that = (AlarmPredictionItem) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(train, that.train)
                .append(alarm_happen, that.alarm_happen)
                .append(dataid, that.dataid)
                .append(input_type, that.input_type)
                .append(input, that.input)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(train)
                .append(input_type)
                .append(input)
                .append(alarm_happen)
                .append(dataid)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AlarmPredictionItem{" +
                "id=" + id +
                ", train=" + train +
                ", input_type='" + input_type + '\'' +
                ", input=" + Arrays.toString(input) +
                ", alarm_happen=" + alarm_happen +
                ", dataid=" + dataid +
                '}';
    }
}
