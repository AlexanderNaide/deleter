package org.satal.deleter.model;

import java.util.Date;

public class Interval {
    private Date date;
    private String interval;

    public Interval(Date date, String interval) {
        this.date = date;
        this.interval = interval;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @Override
    public String toString()  {
        return this.interval;
    }
}
