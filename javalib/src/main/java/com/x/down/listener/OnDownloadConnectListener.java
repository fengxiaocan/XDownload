package com.x.down.listener;

import com.x.down.base.IDownloadRequest;

public interface OnDownloadConnectListener {
    void onPending(IDownloadRequest request);

    void onStart(IDownloadRequest request);

    void onConnecting(IDownloadRequest request);

    void onRequestError(IDownloadRequest request, int code, String error);

    void onCancel(IDownloadRequest request);

    void onRetry(IDownloadRequest request);
}
