package com.x.down.listener;

import com.x.down.base.IDownloadRequest;

public interface OnDownloadListener {
    void onComplete(IDownloadRequest request);

    void onFailure(IDownloadRequest request);
}
