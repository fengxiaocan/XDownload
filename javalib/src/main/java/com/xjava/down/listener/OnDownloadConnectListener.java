package com.xjava.down.listener;

import com.xjava.down.base.IDownloadRequest;

public interface OnDownloadConnectListener{
    void onPending(IDownloadRequest request);

    void onStart(IDownloadRequest request);

    void onConnecting(IDownloadRequest request);

    void onCancel(IDownloadRequest request);

    void onRetry(IDownloadRequest request);
}
