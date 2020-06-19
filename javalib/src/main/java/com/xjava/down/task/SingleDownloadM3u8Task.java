package com.xjava.down.task;


import com.xjava.down.XDownload;
import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.impl.ProgressDisposer;
import com.xjava.down.impl.SpeedDisposer;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.made.M3u8Block;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

final class SingleDownloadM3u8Task extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest{
    private volatile XDownloadRequest request;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final SpeedDisposer speedDisposer;
    private final ArrayList<M3u8Block> m3u8Blocks;

    private volatile int downloadIndex=0;
    private volatile Future taskFuture;
    private volatile int speedLength=0;

    public SingleDownloadM3u8Task(XDownloadRequest request,DownloadListenerDisposer listener,ArrayList<M3u8Block> list){
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()),request.getBufferedSize());
        this.request=request;
        this.m3u8Blocks=list;
        this.listenerDisposer=listener;
        this.progressDisposer=new ProgressDisposer(request.isIgnoredProgress(),
                                                   request.getUpdateProgressTimes(),
                                                   listener);
        this.speedDisposer=new SpeedDisposer(request.isIgnoredSpeed(),request.getUpdateSpeedTimes(),listener);
        this.listenerDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    public void run(){
        listenerDisposer.onStart(this);
        super.run();
        XDownload.get().removeDownload(request.getTag());
    }

    /**
     * 检测是否已经下载完成
     *
     * @return
     */
    public boolean checkComplete(){
        File saveFile=XDownUtils.getSaveFile(request);
        if(saveFile.exists()&&saveFile.length()>0){
            return true;
        }
        return false;
    }

    @Override
    protected void httpRequest() throws Exception{
        //判断之前下载的文件是否存在或完成
        File tempCacheDir=XDownUtils.getTempCacheDir(request);
        if(!request.isUseBreakpointResume()){
            XDownUtils.delectDir(tempCacheDir);
        }

        for(int i=0;i<m3u8Blocks.size();i++){
            downloadIndex=i;
            M3u8Block block=m3u8Blocks.get(i);
            File tempM3u8=new File(tempCacheDir,block.getName());

            long start=0;
            long length=block.getContentLength();
            if(length>0){
                if(tempM3u8.exists()){
                    if(tempM3u8.length()==length){
                        continue;
                    } else if(tempM3u8.length()>length){
                        tempM3u8.delete();
                        start=0;
                    } else{
                        start=tempM3u8.length();
                    }
                }
            } else{
                length=downloadLong(block);
                block.setContentLength(length);
                if(tempM3u8.exists()){
                    if(tempM3u8.length()==length){
                        continue;
                    } else if(tempM3u8.length()>length){
                        tempM3u8.delete();
                        start=0;
                    } else{
                        start=tempM3u8.length();
                    }
                }
            }

            HttpURLConnection http=request.buildConnect(block.getUrl());
            if(start>0){
                http.setRequestProperty("Range",XDownUtils.jsonString("bytes=",start,"-",length));
            }
            listenerDisposer.onConnecting(this);

            int responseCode=http.getResponseCode();


            //重新判断
            if(!isSuccess(responseCode)){
                onResponseError(http,responseCode);
                return;
            }

            //重新下载
            if(!downReadInput(http,tempM3u8)){
                return;
            }
        }
        //处理最后的进度
        if(!progressDisposer.isIgnoredProgress()){
            listenerDisposer.onProgress(this,1);
        }
        //合并下载完成的文件
        m3u8Merge(XDownUtils.getSaveFile(request),tempCacheDir,m3u8Blocks);

        //处理最后的速度
        if(!speedDisposer.isIgnoredSpeed()){
            speedDisposer.onSpeed(this,speedLength);
        }
        speedLength=0;
        //完成回调
        listenerDisposer.onComplete(this);
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
        //连接中
        listenerDisposer.onConnecting(this);

        if(contentLength<=0){
            //长度获取不到的时候重新连接 获取不到长度则要求http请求不要gzip压缩
            XDownUtils.disconnectHttp(http);
            http=request.buildConnect();
            http.setRequestProperty("Accept-Encoding","identity");
            http.connect();

            contentLength=XDownUtils.getContentLength(http);
            //连接中
        }
        listenerDisposer.onConnecting(this);

        XDownUtils.disconnectHttp(http);
        return contentLength;
    }

    private boolean downReadInput(HttpURLConnection http,File file) throws IOException{
        try{
            FileOutputStream os=new FileOutputStream(file,true);
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
        String stream=readStringStream(http.getErrorStream(),XDownUtils.getInputCharset(http));
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
        speedLength+=length;
        if(progressDisposer.isCallProgress()){
            progressDisposer.onProgress(this,m3u8Blocks.size(),downloadIndex);
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
        return 0;
    }

    @Override
    public long getSofarLength(){
        return 0;
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

    public static void m3u8Merge(File file,File dir,List<M3u8Block> list) throws Exception{
        byte[] bytes=new byte[1024*8];
        FileOutputStream outputStream=null;
        try{
            outputStream=new FileOutputStream(file);
            for(int i=0;i<list.size();i++){
                M3u8Block m3u8Block=list.get(i);
                FileInputStream inputStream=null;
                try{
                    File tempFile=new File(dir,m3u8Block.getName());
                    inputStream=new FileInputStream(tempFile);
                    int length;
                    while((length=inputStream.read(bytes))>0){
                        outputStream.write(bytes,0,length);
                    }
                    tempFile.delete();
                } finally{
                    XDownUtils.closeIo(inputStream);
                }
            }
        } finally{
            XDownUtils.closeIo(outputStream);
        }
    }
}
