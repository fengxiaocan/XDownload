package com.x.down;

import android.util.Log;

import com.xjava.down.dispatch.LogReport;

public class AndroidLogReprot implements LogReport{
    @Override
    public void error(Exception e){
        if(e != null){
            e.printStackTrace();
            Log.e("xdownload",e.getMessage());
        }
    }

    @Override
    public void warn(String warn){
        Log.w("xdownload",warn);
    }

    @Override
    public void info(String info){
        Log.i("xdownload",info);
    }
}
