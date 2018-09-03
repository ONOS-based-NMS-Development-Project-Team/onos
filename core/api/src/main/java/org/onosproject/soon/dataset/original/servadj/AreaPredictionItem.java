package org.onosproject.soon.dataset.original.servadj;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.dataset.original.Item;

/**
 * CREATE TABLE public.area_load
 * (
 *   id serial NOT NULL,
 *   area_id smallint,  -- 区域id
 *   timepoint real, -- 表示一天24小时内的时间，值为时间点除以24
 *   two_hours_before real[], -- timepoint之前两个小时的流量变化。包含30个点，其中每个点表示4分钟的变化
 *   tide smallint, -- 潮汐标识位
 *   one_hour_after real[], -- timepoint之后一个小时内的流量变化。包含15个点，其中每个点表示4分钟的变化
 *   CONSTRAINT area_load_primary_key PRIMARY KEY (id)
 * )
 */
public class AreaPredictionItem implements Item {
    private int id;
    private int area_id;
    private double timepoint;
    private double[] two_hours_before;
    private double tide;
    private double[] one_hour_after;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
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

    public double getTide() {
        return tide;
    }

    public void setTide(double tide) {
        this.tide = tide;
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

        AreaPredictionItem that = (AreaPredictionItem) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(area_id, that.area_id)
                .append(timepoint, that.timepoint)
                .append(tide, that.tide)
                .append(one_hour_after, that.one_hour_after)
                .append(two_hours_before, that.two_hours_before)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(area_id)
                .append(timepoint)
                .append(two_hours_before)
                .append(tide)
                .append(one_hour_after)
                .toHashCode();
    }
}
