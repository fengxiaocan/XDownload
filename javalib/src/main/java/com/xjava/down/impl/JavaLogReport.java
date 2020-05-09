package com.xjava.down.impl;

import com.xjava.down.dispatch.LogReport;

public class JavaLogReport implements LogReport{
    @Override
    public void error(Exception e){
        if(e!=null){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void warn(String warn){
        System.out.println(warn);
    }

    @Override
    public void info(String info){
        System.out.println(info);
    }
}
