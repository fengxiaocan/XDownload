package com.xjava.down.base;

public interface IRequest{

    HttpConnect request();//获取请求

    String tag();//获取tag

    String url();//获取Url

    int status();//获取状态

    int retryCount();//重试次数
}
