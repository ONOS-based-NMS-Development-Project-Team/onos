package org.onosproject.soon.dataset.original;


import com.google.common.base.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Arrays;

/**
 * CREATE TABLE public.alarm_prediction
 * (
 *   input real[], -- 输入数据，长度是108
 *   alarm_happen boolean, -- label表示是否发生告警
 *   id integer NOT NULL DEFAULT nextval('alarm_pred1_id_seq'::regclass),
 *   train boolean, -- 是否是训练集，true表示是训练集，false表示是测试集
 *   dataid smallint,
 *   CONSTRAINT alarm_pred1_primary_key PRIMARY KEY (id)
 * )
 */
public class AlarmPredictionItem implements Item {


    private int id;
    private double[] input;
    private boolean alarm_happen;
    private boolean train;
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

    @Override
    public String toString() {
        return "AlarmPredictionItem{" +
                "id=" + id +
                ", input=" + Arrays.toString(input) +
                ", alarm_happen=" + alarm_happen +
                ", train=" + train +
                ", dataid=" + dataid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmPredictionItem that = (AlarmPredictionItem) o;
        return id == that.id &&
                alarm_happen == that.alarm_happen &&
                train == that.train &&
                dataid == that.dataid &&
                comp(input, that.input);
    }

    private boolean comp(double[] a, double[] b) {
        if (a==null && b==null) {
            return true;
        }
        if (a==null || b==null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i=0; i<a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    private int hash(double[] a) {
        if (a == null) {
            return 0;
        }
        int rtn = Objects.hashCode(a[0]);
        if (a.length>1) {
            for (int i=1; i<a.length; i++) {
                rtn = Objects.hashCode(rtn, a[i]);
            }
        }
        return rtn;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, hash(input), alarm_happen, train, dataid);
    }

}
