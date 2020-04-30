package com.xjava.down.core;

import com.xjava.down.ExecutorGather;
import com.xjava.down.XDownload;
import com.xjava.down.base.HttpDownload;
import com.xjava.down.base.RequestBody;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;
import com.xjava.down.task.DownloadThreadRequest;
import com.xjava.down.tool.XDownUtils;

import java.util.concurrent.Future;

public final class XDownloadRequest extends XHttpRequest implements HttpDownload{
    protected String saveFile;//文件保存位置
    protected String cacheDir=XDownload.get().config().getCacheDir();//默认UA
    //默认下载的多线程数
    protected int multiThreadCount=XDownload.get().config().getMultiThreadCount();
    //默认多线程下载的单线程最大下载文件块大小,默认10MB
    protected int multiThreadMaxDownloadSize=XDownload.get().config().getMultiThreadMaxDownloadSize();
    //默认多线程下载的单线程最小下载文件块大小,默认100KB
    protected int multiThreadMinDownloadSize=XDownload.get().config().getMultiThreadMinDownloadSize();
    //是否使用多线程下载
    protected boolean isUseMultiThread=XDownload.get().config().isUseMultiThread();
    //是否使用断点续传
    protected boolean isUseBreakpointResume=XDownload.get().config().isUseBreakpointResume();
    //是否忽略下载的progress回调
    protected boolean ignoredProgress=XDownload.get().config().isIgnoredProgress();
    //更新进度条的间隔
    protected int updateProgressTimes=XDownload.get().config().getUpdateProgressTimes();
    protected OnDownloadListener downloadListener;

    public static XDownloadRequest with(String url){
        return new XDownloadRequest(url);
    }

    protected XDownloadRequest(String baseUrl){
        super(baseUrl);
    }

    public String getSaveFile(){
        return saveFile;
    }

    public String getCacheDir(){
        if(XDownUtils.isStringEmpty(cacheDir)){
            return XDownload.get().config().getCacheDir();
        }
        return cacheDir;
    }

    @Override
    public HttpDownload setCacheDir(String cacheDir){
        this.cacheDir=cacheDir;
        return this;
    }

    @Override
    public HttpDownload setIgnoredProgress(boolean ignoredProgress){
        this.ignoredProgress=ignoredProgress;
        return this;
    }

    @Override
    public HttpDownload setUpdateProgressTimes(int updateProgressTimes){
        this.updateProgressTimes=updateProgressTimes;
        return this;
    }

    @Override
    public HttpDownload setUseMultiThread(boolean useMultiThread){
        this.isUseMultiThread=useMultiThread;
        return this;
    }

    @Override
    public HttpDownload setMultiThreadCount(int multiThreadCount){
        this.multiThreadCount=multiThreadCount;
        return this;
    }

    @Override
    public HttpDownload setMultiThreadMaxDownloadSize(int multiThreadMaxDownloadSize){
        this.multiThreadMaxDownloadSize=multiThreadMaxDownloadSize;
        return this;
    }

    @Override
    public HttpDownload setMultiThreadMinDownloadSize(int multiThreadMinDownloadSize){
        this.multiThreadMinDownloadSize=multiThreadMinDownloadSize;
        return this;
    }

    @Override
    public HttpDownload setUseBreakpointResume(boolean useBreakpointResume){
        this.isUseBreakpointResume=useBreakpointResume;
        return this;
    }

    @Override
    public HttpDownload setDownListener(OnDownloadListener listener){
        this.downloadListener=listener;
        return this;
    }

    @Override
    public HttpDownload setTag(String tag){
        return (HttpDownload)super.setTag(tag);
    }

    @Override
    public HttpDownload setSaveFile(String saveFile){
        this.saveFile=saveFile;
        return this;
    }

    @Override
    public HttpDownload addParams(String name,String value){
        return (HttpDownload)super.addParams(name,value);
    }

    @Override
    public HttpDownload addHeader(String name,String value){
        return (HttpDownload)super.addHeader(name,value);
    }

    @Override
    public HttpDownload setUserAgent(String userAgent){
        return (HttpDownload)super.setUserAgent(userAgent);
    }

    @Override
    public HttpDownload setConnectTimeOut(int connectTimeOut){
        return (HttpDownload)super.setConnectTimeOut(connectTimeOut);
    }

    @Override
    public HttpDownload setUseCaches(boolean useCaches){
        return this;
    }

    @Override
    public HttpDownload setUseAutoRetry(boolean useAutoRetry){
        return (HttpDownload)super.setUseAutoRetry(useAutoRetry);
    }

    @Override
    public HttpDownload setAutoRetryTimes(int autoRetryTimes){
        return (HttpDownload)super.setAutoRetryTimes(autoRetryTimes);
    }

    @Override
    public HttpDownload setAutoRetryInterval(int autoRetryInterval){
        return (HttpDownload)super.setAutoRetryInterval(autoRetryInterval);
    }

    @Override
    public HttpDownload setWifiRequired(boolean wifiRequired){
        return (HttpDownload)super.setWifiRequired(wifiRequired);
    }

    @Override
    public HttpDownload addOnResponseListener(OnResponseListener listener){
        return (HttpDownload)super.addOnResponseListener(listener);
    }

    @Override
    public HttpDownload addOnConnectListener(OnConnectListener listener){
        return (HttpDownload)super.addOnConnectListener(listener);
    }

    @Override
    public HttpDownload post(){
        return (HttpDownload)super.post();
    }

    @Override
    public HttpDownload setRequestMothod(Mothod mothod){
        return (HttpDownload)super.setRequestMothod(mothod);
    }

    @Override
    public HttpDownload requestBody(RequestBody body){
        return (HttpDownload)super.requestBody(body);
    }

    public int getMultiThreadCount(){
        return multiThreadCount;
    }

    public int getMultiThreadMaxDownloadSize(){
        return multiThreadMaxDownloadSize;
    }

    public int getMultiThreadMinDownloadSize(){
        return multiThreadMinDownloadSize;
    }

    public boolean isUseMultiThread(){
        return isUseMultiThread;
    }

    public boolean isUseBreakpointResume(){
        return isUseBreakpointResume;
    }

    public boolean isIgnoredProgress(){
        return ignoredProgress;
    }

    public int getUpdateProgressTimes(){
        return updateProgressTimes;
    }

    @Override
    public String start(){
        DownloadThreadRequest requestTask=new DownloadThreadRequest(this,onConnectListeners,onResponseListeners,downloadListener);
        Future future=ExecutorGather.newThreadExecutor().submit(requestTask);
        requestTask.setTaskFuture(future);
        return getTag();
    }
}
