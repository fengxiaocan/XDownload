package com.xjava.down.listener;

import com.xjava.down.base.IDownloadRequest;

public interface OnSpeedListener{
    void onSpeed(IDownloadRequest request,int speed,int time);
}
