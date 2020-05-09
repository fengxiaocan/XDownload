package com.xjava.down.base;

import com.xjava.down.core.XDownloadRequest;

public interface IDownloadRequest{

    String tag();//获取tag

    String url();//获取Url

    int retryCount();//重试次数

    XDownloadRequest request();

    String getFilePath();//获取下载文件地址

    long getTotalLength();//获取文件总长度

    long getSofarLength();//获取文件已下载长度
}
