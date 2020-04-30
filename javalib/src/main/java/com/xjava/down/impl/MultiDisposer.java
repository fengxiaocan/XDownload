package com.xjava.down.impl;

import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.base.MultiDownloadTask;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MultiDisposer implements OnDownloadListener{

    private final int threadCount;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private volatile int successIndex=0;//成功位置
    private volatile int bolckIndex=0;//指针位置
    private final HashMap<Integer,MultiDownloadTask> taskMaps=new HashMap<>();

    public MultiDisposer(XDownloadRequest request,int threadCount,OnDownloadListener listener){
        this.threadCount=threadCount;
        this.listenerDisposer=new DownloadListenerDisposer(listener);
        this.progressDisposer=new ProgressDisposer(request.isIgnoredProgress(),
                                                   request.getUpdateProgressTimes(),
                                                   listener);
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
    public void onPrepare(IDownloadRequest task){
        listenerDisposer.onPrepare(task);
    }

    @Override
    public void onProgress(float progress){
    }

    public void onProgress(long contentLength,int length){
        if(progressDisposer.isCallProgress()){
            progressDisposer.onProgress(contentLength,getSofarLength());
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
    public void onPause(IDownloadRequest task){
        listenerDisposer.onPause(task);
    }

    @Override
    public void onError(IDownloadRequest task,Exception exception){
        bolckIndex++;
        listenerDisposer.onError(task,exception);
        if(bolckIndex==threadCount){
            onDownloadFailure();
        }
    }

    @Override
    public void onRetry(IDownloadRequest task,Exception exception){
        listenerDisposer.onRetry(task,exception);
    }

    @Override
    public void onComplete(IDownloadRequest task){
        bolckIndex++;
        successIndex++;

        //下载块完成监听
        listenerDisposer.onComplete(task);

        if(bolckIndex==threadCount){
            if(successIndex==threadCount){
                listenerDisposer.onProgress(1);
                File file=task.getFilePath();
                byte[] bytes=new byte[1024*8];
                FileOutputStream outputStream=null;
                try{
                    File cacheDir=null;
                    outputStream=new FileOutputStream(file);
                    for(int i=0;i<threadCount;i++){
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
                    delectDir(cacheDir);

                    onDownloadComplete();
                } catch(Exception e){
                    onDownloadFailure();
                } finally{
                    XDownUtils.closeIo(outputStream);
                }
            } else{
                onDownloadFailure();
            }
        }
    }

    private void delectDir(File dir){
        if(dir==null){
            return;
        }
        File[] files=dir.listFiles();
        if(files!=null){
            for(File file1: files){
                if(file1.isDirectory()){
                    delectDir(file1);
                }
                file1.delete();
            }
        }
        dir.delete();
    }

    @Override
    public void onDownloadComplete(){
        listenerDisposer.onDownloadComplete();
    }

    @Override
    public void onDownloadFailure(){
        listenerDisposer.onDownloadFailure();
    }
}
