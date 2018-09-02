package org.onosproject.soon.dataset.original.sdhnet;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.dataset.original.Item;

import java.util.Date;

/**
 * 当前告警
 * CREATE TABLE public.cur_alarms
 * (
 *   level alarm_level NOT NULL,
 *   name character varying(50) NOT NULL,
 *   alarm_src character varying(50) NOT NULL,
 *   location character varying(100) NOT NULL,
 *   frequency smallint NOT NULL,
 *   fist_happen timestamp with time zone NOT NULL,
 *   recent_happen timestamp with time zone NOT NULL,
 *   clean_time timestamp with time zone,
 *   confirm_time timestamp with time zone,
 *   clean boolean NOT NULL,
 *   confirm boolean NOT NULL,
 *   path_level character varying(30),
 *   CONSTRAINT cur_alarm_pkey PRIMARY KEY (name, alarm_src, location, recent_happen)
 * )
 * WITH (
 *   OIDS=FALSE
 * );
 * ALTER TABLE public.cur_alarms
 *   OWNER TO postgres;
 */
public class CurrentAlarmItem implements Item {

    private String level;
    private String name;
    private String alarm_src;
    private String location;
    private int frequency;
    private Date fist_happen;
    private Date recent_happen;
    private Date clean_time;
    private Date confirm_time;
    private boolean clean;
    private boolean confirm;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Date getFist_happen() {
        return fist_happen;
    }

    public void setFist_happen(Date fist_happen) {
        this.fist_happen = fist_happen;
    }

    public Date getRecent_happen() {
        return recent_happen;
    }

    public void setRecent_happen(Date recent_happen) {
        this.recent_happen = recent_happen;
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

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
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

        CurrentAlarmItem that = (CurrentAlarmItem) o;

        return new EqualsBuilder()
                .append(frequency, that.frequency)
                .append(clean, that.clean)
                .append(confirm, that.confirm)
                .append(level, that.level)
                .append(name, that.name)
                .append(alarm_src, that.alarm_src)
                .append(location, that.location)
                .append(fist_happen, that.fist_happen)
                .append(recent_happen, that.recent_happen)
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
                .append(location)
                .append(frequency)
                .append(fist_happen)
                .append(recent_happen)
                .append(clean_time)
                .append(confirm_time)
                .append(clean)
                .append(confirm)
                .append(path_level)
                .toHashCode();
    }
}
