package org.onosproject.soon.dataset.original.sdhnet;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.dataset.original.Item;

import java.util.Date;

/**
 * CREATE TABLE public.performance
 * (
 *   node character varying(50) NOT NULL,
 *   board character varying(50) NOT NULL,
 *   port character varying(50) NOT NULL,
 *   component character varying(50) NOT NULL,
 *   event character varying(50) NOT NULL,
 *   end_time timestamp with time zone NOT NULL,
 *   max_val real,
 *   cur_val real,
 *   min_val real,
 *   CONSTRAINT performance_pkey PRIMARY KEY (node, board, port, component, event, end_time),
 *   CONSTRAINT board_const FOREIGN KEY (node, board)
 *       REFERENCES public.boards (node, name) MATCH SIMPLE
 *       ON UPDATE NO ACTION ON DELETE NO ACTION, -- 单板外键约束
 *   CONSTRAINT node_const FOREIGN KEY (node)
 *       REFERENCES public.nodes (name) MATCH SIMPLE
 *       ON UPDATE NO ACTION ON DELETE NO ACTION -- 节点的外键依赖
 * )
 * WITH (
 *   OIDS=FALSE
 * );
 * ALTER TABLE public.performance
 *   OWNER TO postgres;
 * COMMENT ON CONSTRAINT board_const ON public.performance IS '单板外键约束';
 * COMMENT ON CONSTRAINT node_const ON public.performance IS '节点的外键依赖';
 */
public class PerformanceItem implements Item {

    private String node;
    private String board;
    private String port;
    private String component;
    private String event;
    private Date end_time;
    private float max_val;
    private float cur_val;
    private float min_val;

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public float getMax_val() {
        return max_val;
    }

    public void setMax_val(float max_val) {
        this.max_val = max_val;
    }

    public float getCur_val() {
        return cur_val;
    }

    public void setCur_val(float cur_val) {
        this.cur_val = cur_val;
    }

    public float getMin_val() {
        return min_val;
    }

    public void setMin_val(float min_val) {
        this.min_val = min_val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PerformanceItem that = (PerformanceItem) o;

        return new EqualsBuilder()
                .append(max_val, that.max_val)
                .append(cur_val, that.cur_val)
                .append(min_val, that.min_val)
                .append(node, that.node)
                .append(board, that.board)
                .append(port, that.port)
                .append(component, that.component)
                .append(event, that.event)
                .append(end_time, that.end_time)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(node)
                .append(board)
                .append(port)
                .append(component)
                .append(event)
                .append(end_time)
                .append(max_val)
                .append(cur_val)
                .append(min_val)
                .toHashCode();
    }
}
