package com.xjava.down.listener;

import com.xjava.down.base.IDownloadRequest;

public interface OnDownloadListener{

    void onProgress(IDownloadRequest request,float progress,int speed);

    void onComplete(IDownloadRequest request);

    void onFailure(IDownloadRequest request);
}
