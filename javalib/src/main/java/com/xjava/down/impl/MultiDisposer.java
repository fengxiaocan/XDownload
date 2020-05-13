package com.xjava.down.impl;

import com.xjava.down.XDownload;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.base.MultiDownloadTask;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MultiDisposer implements OnDownloadConnectListener{

    private final XDownloadRequest request;
    private final int blockCount;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final SpeedDisposer speedDisposer;
    private volatile int successIndex=0;//成功位置
    private volatile int bolckIndex=0;//指针位置
    private volatile int speedLength=0;
    private final HashMap<Integer,MultiDownloadTask> taskMaps=new HashMap<>();

    public MultiDisposer(XDownloadRequest request,int blockCount,DownloadListenerDisposer disposer){
        this.request=request;
        this.blockCount=blockCount;
        this.listenerDisposer=disposer;
        this.progressDisposer=new ProgressDisposer(request.isIgnoredProgress(),
                                                   request.getUpdateProgressTimes(),
                                                   disposer);
        this.speedDisposer=new SpeedDisposer(request.isIgnoredSpeed(),request.getUpdateSpeedTimes(),disposer);
    }

    @Override
    public void onPending(IDownloadRequest task){
        listenerDisposer.onPending(task);
    }

    public void addTask(int index,MultiDownloadTask task){
        taskMaps.put(index,task);
    }

    @Override
    public void onStart(IDownloadRequest task){
        listenerDisposer.onStart(task);
    }

    @Override
    public void onConnecting(IDownloadRequest request){
        listenerDisposer.onConnecting(request);
    }

    @Override
    public void onRequestError(IDownloadRequest request,int code,String error){
        listenerDisposer.onRequestError(request,code,error);
    }

    public void onProgress(IDownloadRequest request,long contentLength,int length){
        speedLength+=length;

        if(progressDisposer.isCallProgress()){
            progressDisposer.onProgress(request,contentLength,getSofarLength());
        }

        if(speedDisposer.isCallSpeed()){
            speedDisposer.onSpeed(request,speedLength);
            speedLength=0;
        }
    }

    public long getSofarLength(){
        synchronized(Object.class){
            long length=0;
            for(Integer index: taskMaps.keySet()){
                length+=taskMaps.get(index).blockSofarLength();
            }
            return length;
        }
    }

    @Override
    public void onCancel(IDownloadRequest task){
        listenerDisposer.onCancel(task);
    }

    @Override
    public void onRetry(IDownloadRequest request){
        listenerDisposer.onRetry(request);
    }

    public void onFailure(IDownloadRequest task){
        bolckIndex++;
        listenerDisposer.onFailure(task);
        if(bolckIndex >= blockCount){
            listenerDisposer.onFailure(task);
            XDownload.get().removeDownload(request.getTag());
        }
    }

    public void onComplete(IDownloadRequest task){
        bolckIndex++;
        successIndex++;

        if(bolckIndex==blockCount){
            if(successIndex==blockCount){
                if(!progressDisposer.isIgnoredProgress()){
                    listenerDisposer.onProgress(task,1);
                }

                if(!speedDisposer.isIgnoredSpeed()){
                    speedDisposer.onSpeed(task,speedLength);
                }
                speedLength=0;
                File file=new File(task.getFilePath());
                byte[] bytes=new byte[1024*8];
                FileOutputStream outputStream=null;
                try{
                    File cacheDir=null;
                    outputStream=new FileOutputStream(file);
                    for(int i=0;i<blockCount;i++){
                        FileInputStream inputStream=null;
                        try{
                            File tempFile=taskMaps.get(i).blockFile();
                            inputStream=new FileInputStream(tempFile);
                            if(cacheDir==null){
                                cacheDir=tempFile.getParentFile();
                            }
                            int length;
                            while((length=inputStream.read(bytes))>0){
                                outputStream.write(bytes,0,length);
                            }
                            tempFile.delete();
                        } finally{
                            XDownUtils.closeIo(inputStream);
                        }
                    }
                    XDownUtils.delectDir(cacheDir);

                    listenerDisposer.onComplete(task);
                    XDownload.get().removeDownload(request.getTag());
                } catch(Exception e){
                    onFailure(task);
                } finally{
                    XDownUtils.closeIo(outputStream);
                }
            } else{
                onFailure(task);
            }
        }
    }


}
