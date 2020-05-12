package com.xjava.down.listener;

import com.xjava.down.base.IDownloadRequest;

public interface OnProgressListener{
    void onProgress(IDownloadRequest request,float progress);
}
