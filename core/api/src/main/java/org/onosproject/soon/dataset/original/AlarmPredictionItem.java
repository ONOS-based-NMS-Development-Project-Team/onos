package org.onosproject.soon.dataset.original;


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
}
