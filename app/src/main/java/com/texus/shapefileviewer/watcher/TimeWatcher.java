package com.texus.shapefileviewer.watcher;

import java.util.Calendar;

/**
 * Created by sandeep on 31/3/16.
 */
public class TimeWatcher {

    long startTime = 0;
    long endTime = 0;


    public TimeWatcher() {

    }

    public void setStartTime() {
        startTime = Calendar.getInstance().getTimeInMillis();
    }

    public void setEndTime() {
        endTime = Calendar.getInstance().getTimeInMillis();
    }

    public long getWhatsUpTo() {
        if(startTime ==  0) return  0;
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return currentTime - startTime;
    }


    public String getTime( long time) {
        if(time <=  0) return  "";
        int sec = (int)(time /1000);
        int min = sec / 60;
        int milliSecond = (int)(time - ( sec * 1000));
        sec = sec - min * 60;

        return min + " Minutes " + sec + " Seconds " + milliSecond + " Milliseconds";

    }

    public String getTotalTimeTaken() {
        return getTime(endTime - startTime);
    }

}
