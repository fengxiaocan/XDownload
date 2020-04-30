package com.xjava.down.listener;

import com.xjava.down.base.IDownloadRequest;

public interface OnDownloadListener{
    void onProgress(float progress);

    void onComplete(IDownloadRequest task);

    void onDownloadComplete();

    void onDownloadFailure();
}
