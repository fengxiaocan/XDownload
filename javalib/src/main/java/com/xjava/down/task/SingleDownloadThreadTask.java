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
import java.net.HttpURLConnection;
import java.util.concurrent.Future;

final class SingleDownloadThreadTask extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest{
    private volatile XDownloadRequest request;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final SpeedDisposer speedDisposer;
    private final File cacheFile;

    private volatile long contentLength;
    private volatile long sSofarLength=0;
    private volatile Future taskFuture;
    private volatile int speedLength=0;

    public SingleDownloadThreadTask(XDownloadRequest request,DownloadListenerDisposer listener,long contentLength){
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.request=request;
        this.contentLength=contentLength;
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
        if(contentLength>0){
            if(file.exists()){
                if(file.length()==contentLength){
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
        //预备中
        listenerDisposer.onConnecting(this);
//        if(contentLength<=0){
        try{
            contentLength=http.getContentLengthLong();
        } catch(Exception e){
            contentLength=http.getContentLength();
        }
//        }

//        Headers headers=getHeaders(http);
//        String contentType=headers.getValue("Content-Type");

        int responseCode=http.getResponseCode();

        if(responseCode >= 200&&responseCode<400){
            final boolean isBreakPointResume;

            if(cacheFile.exists()){
                if(cacheFile.length()==contentLength){
                    sSofarLength=contentLength;
                    //复制临时文件到保存文件中
                    copyFile(cacheFile,XDownUtils.getSaveFile(request),true);
                    //下载完成
                    speedLength=0;
                    listenerDisposer.onComplete(this);
                    return;
                } else if(cacheFile.length()>contentLength){
                    cacheFile.delete();
                    isBreakPointResume=false;
                } else{
                    isBreakPointResume=request.isUseBreakpointResume();
                }
            } else{
                cacheFile.getParentFile().mkdirs();
                isBreakPointResume=false;
            }
            if(isBreakPointResume){
                XDownUtils.disconnectHttp(http);

                //创建一个新的http请求,准备断点下载
                HttpURLConnection urlConnection=request.buildConnect();
                long start=contentLength-cacheFile.length();

                sSofarLength=start;

                urlConnection.setRequestProperty("Range","bytes="+start+"-"+contentLength);
                urlConnection.connect();

                responseCode=urlConnection.getResponseCode();
                if(responseCode >= 200&&responseCode<400){
                    FileOutputStream os=new FileOutputStream(cacheFile,true);
                    readInputStream(urlConnection.getInputStream(),os);
                    XDownUtils.disconnectHttp(urlConnection);
                } else{
                    String stream=readStringStream(urlConnection.getErrorStream());
                    listenerDisposer.onRequestError(this,responseCode,stream);
                    XDownUtils.disconnectHttp(urlConnection);

                    retryToRun();
                }
            } else{
                sSofarLength=0;
                //重新下载
                FileOutputStream os=new FileOutputStream(cacheFile,false);
                readInputStream(http.getInputStream(),os);
                XDownUtils.disconnectHttp(http);
            }
            copyFile(cacheFile,XDownUtils.getSaveFile(request),true);


            if(!progressDisposer.isIgnoredProgress()){
                listenerDisposer.onProgress(this,1);
            }

            if(!speedDisposer.isIgnoredSpeed()){
                speedDisposer.onSpeed(this,speedLength);
            }
            speedLength=0;
            listenerDisposer.onComplete(this);
        } else{
            String stream=readStringStream(http.getErrorStream());
            listenerDisposer.onRequestError(this,responseCode,stream);

            XDownUtils.disconnectHttp(http);
            retryToRun();
        }
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
        return contentLength;
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
