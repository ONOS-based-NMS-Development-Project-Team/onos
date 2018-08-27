package org.onosproject.soon.dataset.original;


import java.util.Date;

/**
 * 故障分类的数据实现
 * 在数据库中的表结构实现是：
 *   id integer NOT NULL DEFAULT nextval('failure_class_id_seq'::regclass),
 *   train boolean NOT NULL,
 *   dataid integer NOT NULL,
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
 *   class character varying(30), -- 标签，表示故障类型
 *   CONSTRAINT failure_class_pkey PRIMARY KEY (id)
 */
public class FailureClassificationItem implements Item {

    private int id;

    private boolean train;
    private int dataid;
    private String level0;
    private String name0;
    private String node0;
    private String board0;
    private Date time0;

    private String level1;
    private String name1;
    private String node1;
    private String board1;
    private Date time1;

    private String level2;
    private String name2;
    private String node2;
    private String board2;
    private Date time2;

    private String level3;
    private String name3;
    private String node3;
    private String board3;
    private Date time3;

    private String level4;
    private String name4;
    private String node4;
    private String board4;
    private Date time4;

    private String level5;
    private String name5;
    private String node5;
    private String board5;
    private Date time5;

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

    public int getDataid() {
        return dataid;
    }

    public void setDataid(int dataid) {
        this.dataid = dataid;
    }

    public String getLevel0() {
        return level0;
    }

    public void setLevel0(String level0) {
        this.level0 = level0;
    }

    public String getName0() {
        return name0;
    }

    public void setName0(String name0) {
        this.name0 = name0;
    }

    public String getNode0() {
        return node0;
    }

    public void setNode0(String node0) {
        this.node0 = node0;
    }

    public String getBoard0() {
        return board0;
    }

    public void setBoard0(String board0) {
        this.board0 = board0;
    }

    public Date getTime0() {
        return time0;
    }

    public void setTime0(Date time0) {
        this.time0 = time0;
    }

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getNode1() {
        return node1;
    }

    public void setNode1(String node1) {
        this.node1 = node1;
    }

    public String getBoard1() {
        return board1;
    }

    public void setBoard1(String board1) {
        this.board1 = board1;
    }

    public Date getTime1() {
        return time1;
    }

    public void setTime1(Date time1) {
        this.time1 = time1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getNode2() {
        return node2;
    }

    public void setNode2(String node2) {
        this.node2 = node2;
    }

    public String getBoard2() {
        return board2;
    }

    public void setBoard2(String board2) {
        this.board2 = board2;
    }

    public Date getTime2() {
        return time2;
    }

    public void setTime2(Date time2) {
        this.time2 = time2;
    }

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getNode3() {
        return node3;
    }

    public void setNode3(String node3) {
        this.node3 = node3;
    }

    public String getBoard3() {
        return board3;
    }

    public void setBoard3(String board3) {
        this.board3 = board3;
    }

    public Date getTime3() {
        return time3;
    }

    public void setTime3(Date time3) {
        this.time3 = time3;
    }

    public String getLevel4() {
        return level4;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public String getNode4() {
        return node4;
    }

    public void setNode4(String node4) {
        this.node4 = node4;
    }

    public String getBoard4() {
        return board4;
    }

    public void setBoard4(String board4) {
        this.board4 = board4;
    }

    public Date getTime4() {
        return time4;
    }

    public void setTime4(Date time4) {
        this.time4 = time4;
    }

    public String getLevel5() {
        return level5;
    }

    public void setLevel5(String level5) {
        this.level5 = level5;
    }

    public String getName5() {
        return name5;
    }

    public void setName5(String name5) {
        this.name5 = name5;
    }

    public String getNode5() {
        return node5;
    }

    public void setNode5(String node5) {
        this.node5 = node5;
    }

    public String getBoard5() {
        return board5;
    }

    public void setBoard5(String board5) {
        this.board5 = board5;
    }

    public Date getTime5() {
        return time5;
    }

    public void setTime5(Date time5) {
        this.time5 = time5;
    }
}
