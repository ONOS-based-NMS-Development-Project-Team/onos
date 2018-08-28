package org.onosproject.soon.dataset.original.sdhnet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

/**
 * 历史告警
 * CREATE TABLE public.his_alarms
 * (
 *   level alarm_level NOT NULL,
 *   name character varying(50) NOT NULL,
 *   alarm_src character varying(50) NOT NULL,
 *   tp node_type,
 *   location character varying(100) NOT NULL,
 *   happen_time timestamp with time zone NOT NULL,
 *   clean_time timestamp with time zone NOT NULL,
 *   confirm_time timestamp with time zone NOT NULL,
 *   path_level character varying(30),
 *   CONSTRAINT his_alarm_pkey PRIMARY KEY (name, alarm_src, location, happen_time)
 * )
 * WITH (
 *   OIDS=FALSE
 * );
 * ALTER TABLE public.his_alarms
 *   OWNER TO postgres;
 */
public class HistoryAlarmItem {

    private String level;
    private String name;
    private String alarm_src;
    private String tp;
    private String location;
    private Date happen_time;
    private Date clean_time;
    private Date confirm_time;
    private String path_level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlarm_src() {
        return alarm_src;
    }

    public void setAlarm_src(String alarm_src) {
        this.alarm_src = alarm_src;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getHappen_time() {
        return happen_time;
    }

    public void setHappen_time(Date happen_time) {
        this.happen_time = happen_time;
    }

    public Date getClean_time() {
        return clean_time;
    }

    public void setClean_time(Date clean_time) {
        this.clean_time = clean_time;
    }

    public Date getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(Date confirm_time) {
        this.confirm_time = confirm_time;
    }

    public String getPath_level() {
        return path_level;
    }

    public void setPath_level(String path_level) {
        this.path_level = path_level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HistoryAlarmItem that = (HistoryAlarmItem) o;

        return new EqualsBuilder()
                .append(level, that.level)
                .append(name, that.name)
                .append(alarm_src, that.alarm_src)
                .append(tp, that.tp)
                .append(location, that.location)
                .append(happen_time, that.happen_time)
                .append(clean_time, that.clean_time)
                .append(confirm_time, that.confirm_time)
                .append(path_level, that.path_level)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(level)
                .append(name)
                .append(alarm_src)
                .append(tp)
                .append(location)
                .append(happen_time)
                .append(clean_time)
                .append(confirm_time)
                .append(path_level)
                .toHashCode();
    }
}
