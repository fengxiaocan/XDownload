package com.x.down.listener;

import com.x.down.base.IDownloadRequest;

public interface OnDownloadListener {
    /**
     * 下载完成
     * @param request
     */
    void onComplete(IDownloadRequest request);

    /**
     * 下载失败
     * @param request
     */
    void onFailure(IDownloadRequest request);
}
