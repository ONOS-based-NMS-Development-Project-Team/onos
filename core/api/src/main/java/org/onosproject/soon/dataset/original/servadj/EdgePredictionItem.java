package org.onosproject.soon.dataset.original.servadj;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.dataset.original.Item;

/**
 * 进行边预测的类
 *
 * CREATE TABLE public.edge_load
 * (
 *   id integer NOT NULL DEFAULT nextval('edge_load_id_seq'::regclass),
 *   edge_id real[], -- 边的表示，长度为14,表示14个节点。14个节点中有且只有两个节点值为1,其余值为0
 *   timepoint real, -- 表示一天24小时内的时间，值为时间点除以24
 *   two_hours_before real[], -- timepoint之前两个小时的流量变化。包含30个点，其中每个点表示4分钟的变化
 *   one_hour_after real[], -- timepoint之后一个小时内的流量变化。包含15个点，其中每个点表示4分钟的变化
 *   CONSTRAINT edge_load_primary_key PRIMARY KEY (id)
 * )
 */
public class EdgePredictionItem implements Item {

    private int id;
    private double[] edge_id;
    private double timepoint;
    private double[] two_hours_before;
    private double[] one_hour_after;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getEdge_id() {
        return edge_id;
    }

    public void setEdge_id(double[] edge_id) {
        this.edge_id = edge_id;
    }

    public double getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(double timepoint) {
        this.timepoint = timepoint;
    }

    public double[] getTwo_hours_before() {
        return two_hours_before;
    }

    public void setTwo_hours_before(double[] two_hours_before) {
        this.two_hours_before = two_hours_before;
    }

    public double[] getOne_hour_after() {
        return one_hour_after;
    }

    public void setOne_hour_after(double[] one_hour_after) {
        this.one_hour_after = one_hour_after;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EdgePredictionItem that = (EdgePredictionItem) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(timepoint, that.timepoint)
                .append(edge_id, that.edge_id)
                .append(two_hours_before, that.two_hours_before)
                .append(one_hour_after, that.one_hour_after)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(edge_id)
                .append(timepoint)
                .append(two_hours_before)
                .append(one_hour_after)
                .toHashCode();
    }
}
