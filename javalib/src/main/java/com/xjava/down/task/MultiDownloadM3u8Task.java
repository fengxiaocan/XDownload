package com.xjava.down.task;


import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.MultiDownloadTask;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.made.M3u8Block;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.Future;

final class MultiDownloadM3u8Task extends HttpDownloadRequest implements MultiDownloadTask, IConnectRequest{
    private final MultiM3u8Disposer multiDisposer;
    private final XDownloadRequest request;
    private final M3u8Block m3u8Block;
    private final File tempFile;
    private final int index;
    private volatile Future taskFuture;

    public MultiDownloadM3u8Task(
            XDownloadRequest request,
            M3u8Block m3u8Block,
            File tempFile,
            AutoRetryRecorder recorder,
            int index,
            MultiM3u8Disposer listener)
    {
        super(recorder,request.getBufferedSize());
        this.request=request;
        this.m3u8Block=m3u8Block;
        this.tempFile=tempFile;
        this.index=index;
        this.multiDisposer=listener;
        multiDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    public void run(){
        multiDisposer.onStart(this);
        super.run();
    }

    @Override
    protected void httpRequest() throws Exception{
        long start=0;
        long length=m3u8Block.getContentLength();


        if(length>0){
            if(tempFile.exists()){
                if(tempFile.length()==length){
                    multiDisposer.onComplete(this);
                    return;
                } else if(tempFile.length()>length){
                    tempFile.delete();
                    start=0;
                } else{
                    start=tempFile.length();
                }
            }
        } else{
            length=downloadLong(m3u8Block);
            m3u8Block.setContentLength(length);
            if(tempFile.exists()){
                if(tempFile.length()==length){
                    multiDisposer.onComplete(this);
                    return;
                } else if(tempFile.length()>length){
                    tempFile.delete();
                    start=0;
                } else{
                    start=tempFile.length();
                }
            }
        }

        HttpURLConnection http=request.buildConnect(m3u8Block.getUrl());
        if(start>0){
            http.setRequestProperty("Range",XDownUtils.jsonString("bytes=",start,"-",length));
        }
        multiDisposer.onConnecting(this);

        int responseCode=http.getResponseCode();

        if(isSuccess(responseCode)){
            FileOutputStream os=new FileOutputStream(tempFile,true);
            if(!readInputStream(http.getInputStream(),os)){
                return;
            }
            multiDisposer.onComplete(this);

            XDownUtils.disconnectHttp(http);
        } else{
            String stream=readStringStream(http.getErrorStream(),XDownUtils.getInputCharset(http));
            multiDisposer.onRequestError(this,responseCode,stream);

            XDownUtils.disconnectHttp(http);
            retryToRun();
        }
    }

    /**
     * 获取片段的长度
     *
     * @param block
     * @return
     * @throws Exception
     */
    private long downloadLong(M3u8Block block) throws Exception{
        HttpURLConnection http=request.buildConnect(block.getUrl());
        int responseCode=http.getResponseCode();

        while(isNeedRedirects(responseCode)){
            http=redirectsConnect(http,request);
            responseCode=http.getResponseCode();
        }
        //优先获取文件长度再回调
        long contentLength=XDownUtils.getContentLength(http);

        multiDisposer.onConnecting(this);

        //连接中
        if(contentLength<=0){
            //长度获取不到的时候重新连接 获取不到长度则要求http请求不要gzip压缩
            XDownUtils.disconnectHttp(http);
            http=request.buildConnect();
            http.setRequestProperty("Accept-Encoding","identity");
            http.connect();

            multiDisposer.onConnecting(this);

            contentLength=XDownUtils.getContentLength(http);
            //连接中
        }
        XDownUtils.disconnectHttp(http);
        return contentLength;
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
        multiDisposer.onProgress(this,length);
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
        return 0;
    }

    @Override
    public long getSofarLength(){
        return 0;
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
        return 0;
    }

    @Override
    public long blockEnd(){
        return 0;
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
