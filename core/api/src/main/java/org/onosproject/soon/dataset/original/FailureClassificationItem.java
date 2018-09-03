package org.onosproject.soon.dataset.original;


import com.google.common.base.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

/**
 * 故障分类的数据实现
 * 在数据库中的表结构实现是：
 *   id integer NOT NULL DEFAULT nextval('failure_class_id_seq'::regclass),
 *   train boolean NOT NULL,
 *   level0 character varying(8) NOT NULL,
 *   name0 character varying(50) NOT NULL,
 *   node0 character varying(50) NOT NULL,
 *   board0 character varying(50) NOT NULL,
 *   time0 timestamp without time zone NOT NULL,
 *   level1 character varying(8),
 *   name1 character varying(50),
 *   node1 character varying(50),
 *   board1 character varying(50),
 *   time1 timestamp without time zone,
 *   level2 character varying(8),
 *   name2 character varying(50),
 *   node2 character varying(50),
 *   board2 character varying(50),
 *   time2 timestamp without time zone,
 *   level3 character varying(8),
 *   name3 character varying(50),
 *   node3 character varying(50),
 *   board3 character varying(50),
 *   time3 timestamp without time zone,
 *   level4 character varying(8),
 *   name4 character varying(50),
 *   node4 character varying(50),
 *   board4 character varying(50),
 *   time4 timestamp without time zone,
 *   level5 character varying(8),
 *   name5 character varying(50),
 *   node5 character varying(50),
 *   board5 character varying(50),
 *   time5 timestamp without time zone,
 *   cls character varying(30), -- 标签，表示故障类型
 *   CONSTRAINT failure_class_pkey PRIMARY KEY (id)
 */
public class FailureClassificationItem implements Item {

    private int id;

    private boolean train;
    private double level0;
    private double name0;
    private double node0;
    private double board0;
    private double time0;

    private double level1;
    private double name1;
    private double node1;
    private double board1;
    private double time1;

    private double level2;
    private double name2;
    private double node2;
    private double board2;
    private double time2;

    private double level3;
    private double name3;
    private double node3;
    private double board3;
    private double time3;

    private double level4;
    private double name4;
    private double node4;
    private double board4;
    private double time4;

    private double level5;
    private double name5;
    private double node5;
    private double board5;
    private double time5;

    private double cls;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTrain() {
        return train;
    }

    public void setTrain(boolean train) {
        this.train = train;
    }

    public double getLevel0() {
        return level0;
    }

    public void setLevel0(double level0) {
        this.level0 = level0;
    }

    public double getName0() {
        return name0;
    }

    public void setName0(double name0) {
        this.name0 = name0;
    }

    public double getNode0() {
        return node0;
    }

    public void setNode0(double node0) {
        this.node0 = node0;
    }

    public double getBoard0() {
        return board0;
    }

    public void setBoard0(double board0) {
        this.board0 = board0;
    }

    public double getTime0() {
        return time0;
    }

    public void setTime0(double time0) {
        this.time0 = time0;
    }

    public double getLevel1() {
        return level1;
    }

    public void setLevel1(double level1) {
        this.level1 = level1;
    }

    public double getName1() {
        return name1;
    }

    public void setName1(double name1) {
        this.name1 = name1;
    }

    public double getNode1() {
        return node1;
    }

    public void setNode1(double node1) {
        this.node1 = node1;
    }

    public double getBoard1() {
        return board1;
    }

    public void setBoard1(double board1) {
        this.board1 = board1;
    }

    public double getTime1() {
        return time1;
    }

    public void setTime1(double time1) {
        this.time1 = time1;
    }

    public double getLevel2() {
        return level2;
    }

    public void setLevel2(double level2) {
        this.level2 = level2;
    }

    public double getName2() {
        return name2;
    }

    public void setName2(double name2) {
        this.name2 = name2;
    }

    public double getNode2() {
        return node2;
    }

    public void setNode2(double node2) {
        this.node2 = node2;
    }

    public double getBoard2() {
        return board2;
    }

    public void setBoard2(double board2) {
        this.board2 = board2;
    }

    public double getTime2() {
        return time2;
    }

    public void setTime2(double time2) {
        this.time2 = time2;
    }

    public double getLevel3() {
        return level3;
    }

    public void setLevel3(double level3) {
        this.level3 = level3;
    }

    public double getName3() {
        return name3;
    }

    public void setName3(double name3) {
        this.name3 = name3;
    }

    public double getNode3() {
        return node3;
    }

    public void setNode3(double node3) {
        this.node3 = node3;
    }

    public double getBoard3() {
        return board3;
    }

    public void setBoard3(double board3) {
        this.board3 = board3;
    }

    public double getTime3() {
        return time3;
    }

    public void setTime3(double time3) {
        this.time3 = time3;
    }

    public double getLevel4() {
        return level4;
    }

    public void setLevel4(double level4) {
        this.level4 = level4;
    }

    public double getName4() {
        return name4;
    }

    public void setName4(double name4) {
        this.name4 = name4;
    }

    public double getNode4() {
        return node4;
    }

    public void setNode4(double node4) {
        this.node4 = node4;
    }

    public double getBoard4() {
        return board4;
    }

    public void setBoard4(double board4) {
        this.board4 = board4;
    }

    public double getTime4() {
        return time4;
    }

    public void setTime4(double time4) {
        this.time4 = time4;
    }

    public double getLevel5() {
        return level5;
    }

    public void setLevel5(double level5) {
        this.level5 = level5;
    }

    public double getName5() {
        return name5;
    }

    public void setName5(double name5) {
        this.name5 = name5;
    }

    public double getNode5() {
        return node5;
    }

    public void setNode5(double node5) {
        this.node5 = node5;
    }

    public double getBoard5() {
        return board5;
    }

    public void setBoard5(double board5) {
        this.board5 = board5;
    }

    public double getTime5() {
        return time5;
    }

    public void setTime5(double time5) {
        this.time5 = time5;
    }

    public double getCls() {
        return cls;
    }

    public void setCls(double cls) {
        this.cls = cls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FailureClassificationItem that = (FailureClassificationItem) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(train, that.train)
                .append(level0, that.level0)
                .append(name0, that.name0)
                .append(node0, that.node0)
                .append(board0, that.board0)
                .append(time0, that.time0)
                .append(level1, that.level1)
                .append(name1, that.name1)
                .append(node1, that.node1)
                .append(board1, that.board1)
                .append(time1, that.time1)
                .append(level2, that.level2)
                .append(name2, that.name2)
                .append(node2, that.node2)
                .append(board2, that.board2)
                .append(time2, that.time2)
                .append(level3, that.level3)
                .append(name3, that.name3)
                .append(node3, that.node3)
                .append(board3, that.board3)
                .append(time3, that.time3)
                .append(level4, that.level4)
                .append(name4, that.name4)
                .append(node4, that.node4)
                .append(board4, that.board4)
                .append(time4, that.time4)
                .append(level5, that.level5)
                .append(name5, that.name5)
                .append(node5, that.node5)
                .append(board5, that.board5)
                .append(time5, that.time5)
                .append(cls, that.cls)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(train)
                .append(level0)
                .append(name0)
                .append(node0)
                .append(board0)
                .append(time0)
                .append(level1)
                .append(name1)
                .append(node1)
                .append(board1)
                .append(time1)
                .append(level2)
                .append(name2)
                .append(node2)
                .append(board2)
                .append(time2)
                .append(level3)
                .append(name3)
                .append(node3)
                .append(board3)
                .append(time3)
                .append(level4)
                .append(name4)
                .append(node4)
                .append(board4)
                .append(time4)
                .append(level5)
                .append(name5)
                .append(node5)
                .append(board5)
                .append(time5)
                .append(cls)
                .toHashCode();
    }
}
