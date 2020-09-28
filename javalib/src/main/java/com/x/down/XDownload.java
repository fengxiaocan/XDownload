package com.x.down;

import com.x.down.base.IConnectRequest;
import com.x.down.config.Config;
import com.x.down.config.XConfig;
import com.x.down.core.HttpConnect;
import com.x.down.core.HttpDownload;
import com.x.down.core.XDownloadRequest;
import com.x.down.core.XHttpRequest;
import com.x.down.core.XHttpRequestQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public final class XDownload {

    private static XDownload xDownload;
    private final Map<String, IConnectRequest> connectMap = new HashMap<>();
    private final Map<String, List<IConnectRequest>> downloadMap = new HashMap<>();

    private XDownload() {
    }

    public static synchronized XDownload get() {
        if (xDownload == null) {
            xDownload = new XDownload();
        }
        return xDownload;
    }

    /**
     * 创建一个http请求
     *
     * @param baseUrl
     * @return
     */
    public static HttpConnect request(String baseUrl) {
        return XHttpRequest.with(baseUrl);
    }

    /**
     * 创建http请求队列
     *
     * @param queue
     * @return
     */
    public static HttpConnect requests(List<String> queue) {
        return XHttpRequestQueue.with(queue);
    }

    /**
     * 创建http请求队列
     *
     * @return
     */
    public static HttpConnect requests() {
        return XHttpRequestQueue.create();
    }

    /**
     * 创建一个下载任务
     *
     * @param baseUrl
     * @return
     */
    public static HttpDownload download(String baseUrl) {
        return XDownloadRequest.with(baseUrl);
    }

    public XDownload config(XConfig setting) {
        Config.initSetting(setting);
        return this;
    }

    public synchronized XConfig config() {
        return Config.config();
    }

    public void addRequest(String tag, IConnectRequest connect) {
        connectMap.put(tag, connect);
    }

    public IConnectRequest removeRequest(String tag) {
        return connectMap.remove(tag);
    }

    public void addDownload(String tag, IConnectRequest download) {
        List<IConnectRequest> requestList = downloadMap.get(tag);
        if (requestList != null) {
            requestList.add(download);
        } else {
            requestList = new ArrayList<>();
            requestList.add(download);
            downloadMap.put(tag, requestList);
        }
    }

    public List<IConnectRequest> removeDownload(String tag) {
        return downloadMap.remove(tag);
    }

    /**
     * 取消请求
     *
     * @param tag
     * @return
     */
    public boolean cancleRequest(String tag) {
        IConnectRequest request = connectMap.remove(tag);
        if (request != null) {
            return request.cancel();
        }
        return false;
    }

    /**
     * 取消下载
     *
     * @param tag
     * @return
     */
    public boolean cancleDownload(String tag) {
        List<IConnectRequest> list = downloadMap.remove(tag);
        if (list != null) {
            boolean isCancel = false;
            for (IConnectRequest request : list) {
                boolean cancel = request.cancel();
                if (!isCancel) {
                    isCancel = cancel;
                }
            }
            return isCancel;
        }
        return false;
    }

    public static ThreadPoolExecutor executorHttpQueue() {
        return ExecutorGather.executorHttpQueue();
    }

    /**
     * 创建多线程下载的子任务线程池队列
     */
    public static ThreadPoolExecutor newSubTaskQueue(int corePoolSize) {
        return ExecutorGather.newSubTaskQueue(corePoolSize);
    }

    /**
     * 创建下载的线程队列
     *
     * @return
     */
    public static ThreadPoolExecutor executorDownloaderQueue() {
        return ExecutorGather.executorDownloaderQueue();
    }

    public static synchronized void recyclerDownloaderQueue() {
        ExecutorGather.recyclerDownloaderQueue();
    }

    public static synchronized void recyclerHttpQueue() {
        ExecutorGather.recyclerHttpQueue();
    }

    public static synchronized void recyclerSingleQueue() {
        ExecutorGather.recyclerSingleQueue();
    }

    public static synchronized void recyclerAllQueue() {
        ExecutorGather.recyclerAllQueue();
    }

}
