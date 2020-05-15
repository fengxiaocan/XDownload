package com.xjava.down.listener;

import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.made.DownloaderBlock;
import com.xjava.down.made.DownloaderMemory;
import com.xjava.down.made.M3u8Memory;

/**
 * 保存下载请求信息的处理器
 */
public interface OnDownloaderMemoryHandler{

    void saveRequestInfo(XDownloadRequest request,DownloaderMemory info);

    DownloaderMemory queryDownloaderInfo(XDownloadRequest request);

    void saveDownloaderBlock(XDownloadRequest request,DownloaderBlock block);

    DownloaderBlock queryDownloaderBlock(XDownloadRequest request);

    void saveM3u8Memory(XDownloadRequest request,M3u8Memory block);

    M3u8Memory queryM3u8Memory(XDownloadRequest request);
}
