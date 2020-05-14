package com.xjava.down.task;


import com.xjava.down.XDownload;
import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.impl.ProgressDisposer;
import com.xjava.down.impl.SpeedDisposer;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Future;

final class SingleDownloadThreadTask extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest{
    private volatile XDownloadRequest request;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final SpeedDisposer speedDisposer;
    private final File cacheFile;

    private volatile long sContentLength;
    private volatile long sSofarLength=0;
    private volatile Future taskFuture;
    private volatile int speedLength=0;

    public SingleDownloadThreadTask(XDownloadRequest request,DownloadListenerDisposer listener,long contentLength){
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.request=request;
        this.sContentLength=contentLength;
        this.listenerDisposer=listener;
        this.progressDisposer=new ProgressDisposer(request.isIgnoredProgress(),
                                                   request.getUpdateProgressTimes(),
                                                   listener);
        this.speedDisposer=new SpeedDisposer(request.isIgnoredSpeed(),request.getUpdateSpeedTimes(),listener);
        this.cacheFile=XDownUtils.getTempFile(request);
        listenerDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    /**
     * 检测是否已经下载完成
     *
     * @return
     */
    public boolean checkComplete(){
        File file=XDownUtils.getSaveFile(request);
        if(sContentLength>0){
            if(file.exists()){
                if(file.length()==sContentLength){
                    listenerDisposer.onComplete(this);
                    return true;
                } else{
                    file.delete();
                }
            }
        }
        return false;
    }

    @Override
    public void run(){
        listenerDisposer.onStart(this);
        super.run();
        XDownload.get().removeDownload(request.getTag());
    }

    @Override
    protected void httpRequest() throws Exception{
        if(checkComplete()){
            return;
        }
        HttpURLConnection http=request.buildConnect();

        sContentLength=XDownUtils.getContentLength(http);
        //连接中
        listenerDisposer.onConnecting(this);

        if(sContentLength<=0){
            //长度获取不到的时候重新连接 获取不到长度则要求http请求不要gzip压缩
            http.setRequestProperty("Accept-Encoding","identity");
            http.connect();
            //重新获取长度
            sContentLength=XDownUtils.getContentLength(http);
            //连接中
            listenerDisposer.onConnecting(this);
        }

        int responseCode=http.getResponseCode();

        if(responseCode >= 200&&responseCode<400){
            final boolean isBreakPointResume;//是否断点续传

            if(cacheFile.exists()){
                if(sContentLength>0){
                    if(cacheFile.length()==sContentLength){
                        sSofarLength=sContentLength;
                        //复制临时文件到保存文件中
                        copyFile(cacheFile,XDownUtils.getSaveFile(request),true);
                        //下载完成
                        speedLength=0;
                        listenerDisposer.onComplete(this);
                        return;
                    } else if(cacheFile.length()>sContentLength){
                        cacheFile.delete();
                        sSofarLength=0;
                        isBreakPointResume=false;
                    } else{
                        sSofarLength=cacheFile.length();
                        isBreakPointResume=request.isUseBreakpointResume();
                    }
                } else{
                    cacheFile.delete();
                    sSofarLength=0;
                    isBreakPointResume=false;
                }
            } else{
                cacheFile.getParentFile().mkdirs();
                sSofarLength=0;
                isBreakPointResume=false;
            }
            if(isBreakPointResume){

                long start=cacheFile.length();
                http.setRequestProperty("Range","bytes="+start+"-"+sContentLength);
                //重新连接
                http.connect();

                responseCode=http.getResponseCode();
                //重新判断
                if(responseCode >= 200&&responseCode<400){
                    onResponseError(http,responseCode);
                    return;
                }
            }

            //重新下载
            if(!downReadInput(http,isBreakPointResume)){
                onCancel();
                return;
            }
            //复制下载完成的文件
            copyFile(cacheFile,XDownUtils.getSaveFile(request),true);

            //处理最后的进度
            if(!progressDisposer.isIgnoredProgress()){
                listenerDisposer.onProgress(this,1);
            }
            //处理最后的速度
            if(!speedDisposer.isIgnoredSpeed()){
                speedDisposer.onSpeed(this,speedLength);
            }
            speedLength=0;
            //完成回调
            listenerDisposer.onComplete(this);
        } else{
            onResponseError(http,responseCode);
        }
    }

    private boolean downReadInput(HttpURLConnection http,boolean append) throws IOException{
        try{
            FileOutputStream os=new FileOutputStream(cacheFile,append);
            return readInputStream(http.getInputStream(),os);
        } finally{
            XDownUtils.disconnectHttp(http);
        }
    }

    /**
     * 处理失败的回调
     *
     * @param http
     * @param responseCode
     * @throws IOException
     */
    private void onResponseError(HttpURLConnection http,int responseCode) throws IOException{
        String stream=readStringStream(http.getErrorStream());
        listenerDisposer.onRequestError(this,responseCode,stream);

        XDownUtils.disconnectHttp(http);
        retryToRun();
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
    protected void onProgress(int length){
        sSofarLength+=length;
        speedLength+=length;
        if(progressDisposer.isCallProgress()){
            progressDisposer.onProgress(this,getTotalLength(),getSofarLength());
        }
        if(speedDisposer.isCallSpeed()){
            speedDisposer.onSpeed(this,speedLength);
            speedLength=0;
        }
    }


    @Override
    public String getFilePath(){
        return XDownUtils.getSaveFile(request).getAbsolutePath();
    }

    @Override
    public long getTotalLength(){
        return sContentLength;
    }

    @Override
    public long getSofarLength(){
        return sSofarLength;
    }

    @Override
    public String tag(){
        return request.getIdentifier();
    }

    @Override
    public String url(){
        return request.getConnectUrl();
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
        return request;
    }
}
