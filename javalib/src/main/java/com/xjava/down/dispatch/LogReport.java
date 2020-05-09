package com.xjava.down.dispatch;

public interface LogReport{
    void error(Exception e);

    void warn(String warn);

    void info(String info);
}
