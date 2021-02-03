package com.x.down.task;

import com.x.down.ExecutorGather;
import com.x.down.XDownload;
import com.x.down.core.XDownloadRequest;
import com.x.down.core.XHttpRequest;
import com.x.down.core.XHttpRequestQueue;
import com.x.down.impl.DownloadListenerDisposer;

import java.util.List;
import java.util.concurrent.Future;

public final class ThreadTaskFactory {
    public static void createSingleDownloadTask(XDownloadRequest request) {
        final DownloadListenerDisposer disposer = new DownloadListenerDisposer(request.getSchedulers(),
                request.getOnDownloadConnectListener(),
                request.getOnDownloadListener(),
                request.getOnProgressListener(),
                request.getOnSpeedListener());
        SingleDownloadThreadTask requestTask = new SingleDownloadThreadTask(request, disposer, 0);
        Future future = ExecutorGather.executorDownloaderQueue().submit(requestTask);
        XDownload.get().addDownload(request.getTag(), requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createDownloadThreadRequest(XDownloadRequest request) {
        DownloadThreadRequest requestTask = new DownloadThreadRequest(request,
                request.getOnDownloadConnectListener(),
                request.getOnDownloadListener(),
                request.getOnProgressListener(),
                request.getOnSpeedListener());
        Future future = ExecutorGather.executorDownloaderQueue().submit(requestTask);
        XDownload.get().addDownload(request.getTag(), requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createM3u8DownloaderRequest(XDownloadRequest request) {
        M3u8DownloaderRequest requestTask = new M3u8DownloaderRequest(request,
                request.getOnDownloadConnectListener(),
                request.getOnDownloadListener(),
                request.getOnProgressListener(),
                request.getOnSpeedListener());
        Future future = ExecutorGather.executorDownloaderQueue().submit(requestTask);
        XDownload.get().addDownload(request.getTag(), requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createHttpRequestTask(XHttpRequest request) {
        HttpRequestTask requestTask = new HttpRequestTask(request,
                request.getOnConnectListeners(),
                request.getOnResponseListeners());
        Future future = ExecutorGather.executorHttpQueue().submit(requestTask);
        XDownload.get().addRequest(request.getTag(), requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createHttpRequestTaskQueue(XHttpRequestQueue request) {
        List<XHttpRequest> requests = request.cloneToRequest();
        for (XHttpRequest httpRequest : requests) {
            createHttpRequestTask(httpRequest);
        }
    }
}
