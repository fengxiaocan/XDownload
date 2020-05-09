package com.xjava.down.base;

import com.xjava.down.core.HttpConnect;

public interface IRequest{
    HttpConnect request();//获取请求

    String tag();//获取tag

    String url();//获取Url

    int retryCount();//重试次数
}
