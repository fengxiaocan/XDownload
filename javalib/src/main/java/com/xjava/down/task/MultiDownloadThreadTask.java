package com.xjava.down.task;


import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.MultiDownloadTask;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.MultiDisposer;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.Future;

final class MultiDownloadThreadTask extends HttpDownloadRequest implements MultiDownloadTask, IConnectRequest{
    private final MultiDisposer multiDisposer;
    private final XDownloadRequest request;
    private final File tempFile;
    private final int index;
    private final long contentLength;
    private final long blockStart;
    private final long blockEnd;
    private volatile Future taskFuture;

    public MultiDownloadThreadTask(
            XDownloadRequest request,
            File tempFile,
            AutoRetryRecorder recorder,
            int index,
            long contentLength,
            long blockStart,
            long blockEnd,
            MultiDisposer listener)
    {
        super(recorder);
        this.request=request;
        this.tempFile=tempFile;
        this.index=index;
        this.contentLength=contentLength;
        this.blockStart=blockStart;
        this.blockEnd=blockEnd;
        this.multiDisposer=listener;
        pending();
        multiDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    private void pending(){
        if(tempFile.exists()){
            //是否使用断点续传
            if(!request.isUseBreakpointResume()){
                tempFile.delete();
            } else if(tempFile.length()>blockEnd-blockStart){
                tempFile.delete();
            }
        }
    }

    @Override
    public void run(){
        multiDisposer.onStart(this);
        super.run();
    }

    @Override
    protected void httpRequest() throws Exception{

        //是否使用断点续传
        final long start;
        if(tempFile.exists()){
            final long tempLength=blockEnd-blockStart;
            if(tempFile.length()==tempLength){
                multiDisposer.onComplete(this);
                return;
            } else if(tempFile.length()>tempLength){
                tempFile.delete();
                start=blockStart;
            } else{
                start=blockStart+tempFile.length();
            }
        } else{
            start=blockStart;
        }

        HttpURLConnection http=request.buildConnect();
        http.setRequestProperty("Range","bytes="+start+"-"+blockEnd);
        http.connect();
        multiDisposer.onConnecting(this);

        int responseCode=http.getResponseCode();
        if(responseCode >= 200&&responseCode<300){
            FileOutputStream os=new FileOutputStream(tempFile,true);
            readInputStream(http.getInputStream(),os);

            multiDisposer.onComplete(this);
            XDownUtils.disconnectHttp(http);
        } else{
            String stream=readStringStream(http.getErrorStream());
            multiDisposer.onRequestError(this,responseCode,stream);

            XDownUtils.disconnectHttp(http);
            retryToRun();
        }
    }

    @Override
    protected void onRetry(){
        multiDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e){
        multiDisposer.onFailure(this);
    }

    @Override
    protected void onCancel(){
        multiDisposer.onCancel(this);
    }

    @Override
    protected void onProgress(int length){
        multiDisposer.onProgress(this,contentLength,length);
    }

    @Override
    public int blockIndex(){
        return index;
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
        return multiDisposer.getSofarLength();
    }

    @Override
    public String tag(){
        return request.getTag();
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

    @Override
    public long blockStart(){
        return blockStart;
    }

    @Override
    public long blockEnd(){
        return blockEnd;
    }

    @Override
    public long blockSofarLength(){
        return tempFile.length();
    }

    @Override
    public File blockFile(){
        return tempFile;
    }
}
