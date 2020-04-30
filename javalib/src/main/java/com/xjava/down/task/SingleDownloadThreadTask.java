package com.xjava.down.task;


import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.impl.ProgressDisposer;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;

class SingleDownloadThreadTask extends HttpDownloadRequest implements IDownloadRequest{
    private volatile XDownloadRequest request;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final File cacheFile;
    private final long contentLength;

    private volatile long sSofarLength=0;
    private volatile int sResonseCode=0;

    public SingleDownloadThreadTask(XDownloadRequest request,OnDownloadListener listener,long contentLength){
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.request=request;
        this.contentLength=contentLength;
        this.listenerDisposer=new DownloadListenerDisposer(listener);
        this.progressDisposer=new ProgressDisposer(request.isIgnoredProgress(),
                                                   request.getUpdateProgressTimes(),
                                                   listener);
        this.cacheFile=XDownUtils.getTempFile(request);
        listenerDisposer.onPending(this);
    }

    /**
     * 检测是否已经下载完成
     * @return
     */
    public boolean checkComplete(){
        File file=XDownUtils.getSaveFile(request);
        if(file.exists()){
            if(file.length()==contentLength&&contentLength>0){
                listenerDisposer.onProgress(1);
                listenerDisposer.onComplete(this);
                listenerDisposer.onDownloadComplete();
                return true;
            } else{
                file.delete();
            }
        }
        return false;
    }

    @Override
    public void run(){
        listenerDisposer.onStart(this);
        super.run();
    }

    @Override
    protected void httpRequest() throws Exception{
        if(checkComplete()){
            return;
        }
        sResonseCode=0;
        HttpURLConnection http=request.buildConnect();
        //预备中
        listenerDisposer.onPrepare(this);

        sResonseCode=http.getResponseCode();

        if(sResonseCode >= 200&&sResonseCode<400){
            final boolean isBreakPointResume;

            if(cacheFile.exists()){
                if(cacheFile.length()==contentLength){
                    sSofarLength=contentLength;
                    //复制临时文件到保存文件中
                    copyFile(cacheFile,XDownUtils.getSaveFile(request),true);
                    //下载完成
                    listenerDisposer.onProgress(1);
                    listenerDisposer.onComplete(this);
                    listenerDisposer.onDownloadComplete();
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

                sResonseCode=urlConnection.getResponseCode();
                if(sResonseCode >= 200&&sResonseCode<400){
                    FileOutputStream os=new FileOutputStream(cacheFile,true);
                    readInputStream(urlConnection.getInputStream(),os);
                    XDownUtils.disconnectHttp(urlConnection);
                } else{
                    String stream=readStringStream(urlConnection.getErrorStream());
                    XDownUtils.disconnectHttp(urlConnection);
                    throw new ConnectException(stream);
                }
            } else{
                sSofarLength=0;
                //重新下载
                FileOutputStream os=new FileOutputStream(cacheFile,false);
                readInputStream(http.getInputStream(),os);
                XDownUtils.disconnectHttp(http);
            }
            copyFile(cacheFile,XDownUtils.getSaveFile(request),true);

            listenerDisposer.onProgress(1);
            listenerDisposer.onComplete(this);
            listenerDisposer.onDownloadComplete();
        } else{
            String stream=readStringStream(http.getErrorStream());
            XDownUtils.disconnectHttp(http);
            throw new ConnectException(stream);
        }
    }

    @Override
    protected void onRetry(Exception e){
        listenerDisposer.onRetry(this,e);
    }

    @Override
    protected void onError(Exception e){
        listenerDisposer.onError(this,e);
    }


    @Override
    protected void onProgress(int length){
        sSofarLength+=length;
        if(progressDisposer.isCallProgress()){
            progressDisposer.onProgress(getTotalLength(),getSofarLength());
        }
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
        return false;
    }

    @Override
    public int getStatus(){
        return 0;
    }

    @Override
    public String getTag(){
        return request.getIdentifier();
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
        return sSofarLength;
    }

    @Override
    public int responseCode(){
        return sResonseCode;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
    }
}
