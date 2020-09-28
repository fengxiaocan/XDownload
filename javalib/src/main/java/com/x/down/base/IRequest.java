package com.x.down.base;

import com.x.down.core.HttpConnect;

public interface IRequest {
    HttpConnect request();//获取请求

    String tag();//获取tag

    String url();//获取Url

    int retryCount();//重试次数
}
