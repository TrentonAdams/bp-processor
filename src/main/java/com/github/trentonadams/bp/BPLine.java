package com.github.trentonadams.bp;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  25/05/14 4:40 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@CsvDataType
public class BPLine implements Cloneable
{
    @CsvField(pos = 1, format = "yyyy-MM-dd HH:mm")
    private Date date;

    @CsvField(pos = 2)
    private int hr;

    @CsvField(pos = 3)
    private int systolic;

    @CsvField(pos = 4)
    private int diastolic;

    @CsvField(pos = 5)
    private String tags;

    public Date getDate()
    {
        return date;
    }

    public Calendar getCalendar()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getHr()
    {
        return hr;
    }

    public void setHr(int hr)
    {
        this.hr = hr;
    }

    public int getSystolic()
    {
        return systolic;
    }

    public void setSystolic(int systolic)
    {
        this.systolic = systolic;
    }

    public int getDiastolic()
    {
        return diastolic;
    }

    public void setDiastolic(int diastolic)
    {
        this.diastolic = diastolic;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
