package com.xjava.down.task;


import com.xjava.down.base.MultiDownloadTask;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.MultiDisposer;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;

class MultiDownloadThreadTask extends HttpDownloadRequest implements MultiDownloadTask{
    private final MultiDisposer multiDisposer;
    private final XDownloadRequest request;
    private final File tempFile;
    private final int index;
    private final long contentLength;
    private final long blockStart;
    private final long blockEnd;
    private volatile int responseCode=0;

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
        multiDisposer.onPrepare(this);
        responseCode=0;
        //是否使用断点续传
        final long start;
        if(tempFile.exists()){
            final long tempLength=blockEnd-blockStart;
            if(tempFile.length()==tempLength){
                responseCode=200;
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
        responseCode=http.getResponseCode();
        if(responseCode >= 200&&responseCode<300){
            FileOutputStream os=new FileOutputStream(tempFile,true);
            readInputStream(http.getInputStream(),os);

            multiDisposer.onComplete(this);
            http.disconnect();
        } else{
            String stream=readStringStream(http.getErrorStream());
            http.disconnect();
            retryToRun(stream);
        }
    }

    @Override
    protected void onRetry(Exception e){
        multiDisposer.onRetry(this,e);
    }

    @Override
    protected void onError(Exception e){
        multiDisposer.onError(this,e);
    }

    @Override
    protected void onProgress(int length){
        multiDisposer.onProgress(contentLength,length);
    }

    @Override
    public boolean start(){
        return false;
    }

    @Override
    public boolean ready(){
        return false;
    }

    @Override
    public boolean pause(){
        return false;
    }

    @Override
    public boolean cancel(){
        return false;
    }

    @Override
    public boolean isContinue(){
        return false;
    }

    @Override
    public boolean isMultiThread(){
        return true;
    }

    @Override
    public int getStatus(){
        return 0;
    }

    @Override
    public int blockIndex(){
        return index;
    }

    @Override
    public String getTag(){
        return request.getTag();
    }

    @Override
    public String getUrl(){
        return request.getConnectUrl();
    }

    @Override
    public File getFilePath(){
        return XDownUtils.getSaveFile(request);
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
    public int responseCode(){
        return responseCode;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
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
