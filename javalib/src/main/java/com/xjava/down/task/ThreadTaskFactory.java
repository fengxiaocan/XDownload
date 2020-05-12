package com.xjava.down.task;

import com.xjava.down.ExecutorGather;
import com.xjava.down.XDownload;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.core.XHttpRequest;
import com.xjava.down.core.XHttpRequestQueue;
import com.xjava.down.impl.DownloadListenerDisposer;

import java.util.List;
import java.util.concurrent.Future;

public final class ThreadTaskFactory{
    public static void createSingleDownloadTask(XDownloadRequest request){
        final DownloadListenerDisposer disposer=new DownloadListenerDisposer(request.getSchedulers(),
                                                                             request.getOnDownloadConnectListener(),
                                                                             request.getOnDownloadListener(),
                                                                             request.getOnProgressListener(),
                                                                             request.getOnSpeedListener());
        SingleDownloadThreadTask requestTask=new SingleDownloadThreadTask(request,disposer,0);
        Future future=ExecutorGather.executorQueue().submit(requestTask);
        XDownload.get().addDownload(request.getTag(),requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createDownloadThreadRequest(XDownloadRequest request){
        DownloadThreadRequest requestTask=new DownloadThreadRequest(request,
                                                                    request.getOnDownloadConnectListener(),
                                                                    request.getOnDownloadListener(),
                                                                    request.getOnProgressListener(),
                                                                    request.getOnSpeedListener());
        Future future=ExecutorGather.newThreadExecutor().submit(requestTask);
        XDownload.get().addDownload(request.getTag(),requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createHttpRequestTask(XHttpRequest request){
        HttpRequestTask requestTask=new HttpRequestTask(request,
                                                        request.getOnConnectListeners(),
                                                        request.getOnResponseListeners());
        Future future=ExecutorGather.newThreadExecutor().submit(requestTask);
        XDownload.get().addRequest(request.getTag(),requestTask);
        requestTask.setTaskFuture(future);
    }

    public static void createHttpRequestTaskQueue(XHttpRequestQueue request){
        List<XHttpRequest> requests=request.cloneToRequest();
        for(XHttpRequest httpRequest: requests){
            createHttpRequestTask(httpRequest);
        }
    }
}
