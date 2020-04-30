package com.xjava.down.base;

import java.io.File;

public interface MultiDownloadTask extends IDownloadRequest{
    int blockIndex();//下载的线程任务位置

    long blockStart();//获取下载块的起点

    long blockEnd();//获取下载块的终点

    long blockSofarLength();//获取块已下载的长度

    File blockFile();//获取块的文件地址
}
