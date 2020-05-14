package com.xjava.down.core;

import com.xjava.down.XDownload;
import com.xjava.down.data.Headers;
import com.xjava.down.data.Params;
import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;
import com.xjava.down.task.ThreadTaskFactory;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class XDownloadRequest extends BaseRequest implements HttpDownload{
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
    //是否忽略下载的progress回调
    protected boolean ignoredSpeed=XDownload.get().config().isIgnoredSpeed();
    //更新进度条的间隔
    protected int updateProgressTimes=XDownload.get().config().getUpdateProgressTimes();
    //更新下载速度的间隔
    protected int updateSpeedTimes=XDownload.get().config().getUpdateSpeedTimes();
    protected OnDownloadListener onDownloadListener;
    protected OnDownloadConnectListener onDownloadConnectListener;
    protected OnProgressListener onProgressListener;
    protected OnSpeedListener onSpeedListener;

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
    public HttpDownload setIgnoredSpeed(boolean ignoredSpeed){
        this.ignoredSpeed=ignoredSpeed;
        return this;
    }

    @Override
    public HttpDownload setUpdateProgressTimes(int updateProgressTimes){
        this.updateProgressTimes=updateProgressTimes;
        return this;
    }

    @Override
    public HttpDownload setUpdateSpeedTimes(int updateSpeedTimes){
        this.updateSpeedTimes=updateSpeedTimes;
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
    public HttpDownload setDownloadListener(OnDownloadListener listener){
        onDownloadListener=listener;
        return this;
    }

    @Override
    public HttpDownload setConnectListener(OnDownloadConnectListener listener){
        onDownloadConnectListener=listener;
        return this;
    }

    @Override
    public HttpDownload setOnProgressListener(OnProgressListener listener){
        onProgressListener = listener;
        return this;
    }

    @Override
    public HttpDownload setOnSpeedListener(OnSpeedListener listener){
        onSpeedListener = listener;
        return this;
    }

    @Override
    public HttpDownload delect(){
        XDownload.get().cancleDownload(getTag());

        File saveFile=XDownUtils.getSaveFile(this);
        if(saveFile.exists()){
            saveFile.delete();
        }
        File tempFile=XDownUtils.getTempFile(this);
        if(tempFile.exists()){
            tempFile.delete();
        }
        File tempCacheDir=XDownUtils.getTempCacheDir(this);
        XDownUtils.delectDir(tempCacheDir);
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
    public HttpDownload setParams(Params params){
        return (HttpDownload)super.setParams(params);
    }

    @Override
    public HttpDownload setHeader(Headers header){
        return (HttpDownload)super.setHeader(header);
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
    public HttpDownload scheduleOn(Schedulers schedulers){
        return (HttpDownload)super.scheduleOn(schedulers);
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
    public boolean isIgnoredSpeed(){
        return ignoredSpeed;
    }

    public int getUpdateProgressTimes(){
        return updateProgressTimes;
    }

    public int getUpdateSpeedTimes(){
        return updateSpeedTimes;
    }

    public OnDownloadListener getOnDownloadListener(){
        return onDownloadListener;
    }

    public OnDownloadConnectListener getOnDownloadConnectListener(){
        return onDownloadConnectListener;
    }

    public OnProgressListener getOnProgressListener(){
        return onProgressListener;
    }

    public OnSpeedListener getOnSpeedListener(){
        return onSpeedListener;
    }

    @Override
    public String start(){
        if(isUseMultiThread&&multiThreadCount>1){
            ThreadTaskFactory.createDownloadThreadRequest(this);
        } else{
            ThreadTaskFactory.createSingleDownloadTask(this);
        }
        return getTag();
    }

    public HttpURLConnection buildConnect() throws Exception{
        URL url=new URL(getConnectUrl());
        HttpURLConnection http=(HttpURLConnection)url.openConnection();
        http.setRequestMethod("GET");
        //设置http请求头
        http.setRequestProperty("Connection","Keep-Alive");
        if(headers!=null){
            for(String key: headers.keySet()){
                http.setRequestProperty(key,headers.getValue(key));
            }
        }
        if(userAgent!=null){
            http.setRequestProperty("User-Agent",userAgent);
        }
        if(connectTimeOut>0){
            http.setConnectTimeout(connectTimeOut);
            http.setReadTimeout(connectTimeOut);
        }
        http.setUseCaches(false);
        http.setDoInput(true);
        return http;
    }
}
