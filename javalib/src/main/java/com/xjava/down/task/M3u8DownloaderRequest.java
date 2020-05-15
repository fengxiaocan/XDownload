package com.xjava.down.task;


import com.xjava.down.ExecutorGather;
import com.xjava.down.XDownload;
import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.impl.MultiDisposer;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.made.DownloaderBlock;
import com.xjava.down.made.DownloaderMemory;
import com.xjava.down.made.M3u8Memory;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

final class M3u8DownloaderRequest extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest{

    protected ThreadPoolExecutor threadPoolExecutor;
    protected final XDownloadRequest httpRequest;
    protected final DownloadListenerDisposer listenerDisposer;
    protected volatile Future taskFuture;
    protected volatile long sContentLength;

    public M3u8DownloaderRequest(
            XDownloadRequest request,
            OnDownloadConnectListener onConnectListeners,
            OnDownloadListener downloadListeners,
            OnProgressListener onProgressListener,
            OnSpeedListener onSpeedListener)
    {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()),request.getBufferedSize());
        this.httpRequest=request;
        this.listenerDisposer=new DownloadListenerDisposer(request.getSchedulers(),
                                                           onConnectListeners,
                                                           downloadListeners,
                                                           onProgressListener,
                                                           onSpeedListener);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    protected void onConnecting(long length){
        sContentLength=length;
        listenerDisposer.onConnecting(this);
    }

    @Override
    protected void httpRequest() throws Exception{
        //获取之前的下载信息
        M3u8Memory info=XDownload.get().getMemoryHandler().queryM3u8Memory(httpRequest);
        if(info!=null){
            sContentLength=info.getLength();
        }
        //判断一下文件的长度是否获取得到
        if(sContentLength<=0 || info.getBlockList() == null){
            if(info == null){
                info = new M3u8Memory();
            }
            info.setOriginalUrl(httpRequest.getConnectUrl());
            if(info.getResponse() == null){
                HttpURLConnection http=httpRequest.buildConnect();
                int responseCode=http.getResponseCode();
                if(isNeedRedirects(responseCode)){
                    http=redirectsConnect(http,httpRequest);
                }

                responseCode=http.getResponseCode();

                if(!isSuccess(responseCode)){
                    //获取错误信息
                    String stream=readStringStream(http.getErrorStream());
                    listenerDisposer.onRequestError(this,responseCode,stream);
                    //断开请求
                    XDownUtils.disconnectHttp(http);
                    //重试
                    retryToRun();
                    return;
                }

                String response=readStringStream(http.getInputStream());
                info.setResponse(response);
                //断开连接
                XDownUtils.disconnectHttp(http);
            }
            String response=info.getResponse();
        }

        //判断之前有没有下载完成文件
        if(sContentLength>0){
            File file=XDownUtils.getSaveFile(httpRequest);
            if(file.exists()){
                if(file.length()==sContentLength){
                    listenerDisposer.onComplete(this);
                    return;
                } else{
                    file.delete();
                }
            }
        }
    }

    @Override
    protected HttpURLConnection getDownloaderLong(XDownloadRequest request) throws Exception{
        return super.getDownloaderLong(request);
    }

    @Override
    protected void onRetry(){
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e){
        listenerDisposer.onFailure(this);
    }

    @Override
    protected void onCancel(){
        listenerDisposer.onCancel(this);
    }

    @Override
    public String tag(){
        return httpRequest.getTag();
    }

    @Override
    public String url(){
        return httpRequest.getConnectUrl();
    }

    @Override
    public boolean cancel(){
        isCancel=true;
        if(taskFuture!=null){
            return taskFuture.cancel(true);
        }
        return false;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
    }

    @Override
    public XDownloadRequest request(){
        return httpRequest;
    }

    @Override
    public String getFilePath(){
        return XDownUtils.getSaveFile(httpRequest).getAbsolutePath();
    }

    @Override
    public long getTotalLength(){
        return sContentLength;
    }

    @Override
    public long getSofarLength(){
        return 0;
    }
}
