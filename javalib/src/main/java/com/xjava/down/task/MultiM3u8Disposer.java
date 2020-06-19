package com.xjava.down.task;

import com.xjava.down.XDownload;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.impl.ProgressDisposer;
import com.xjava.down.impl.SpeedDisposer;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.made.M3u8Block;
import com.xjava.down.tool.XDownUtils;

import java.util.List;

import static com.xjava.down.task.SingleDownloadM3u8Task.m3u8Merge;


class MultiM3u8Disposer implements OnDownloadConnectListener{

    private final XDownloadRequest request;
    private final int blockCount;
    private final DownloadListenerDisposer listenerDisposer;
    private final ProgressDisposer progressDisposer;
    private final SpeedDisposer speedDisposer;
    private volatile int successIndex=0;//成功位置
    private volatile int bolckIndex=0;//指针位置
    private volatile int speedLength=0;
    private final List<M3u8Block> taskList;

    public MultiM3u8Disposer(XDownloadRequest request,List<M3u8Block> taskList,DownloadListenerDisposer disposer){
        this.request=request;
        this.blockCount=taskList.size();
        this.listenerDisposer=disposer;
        this.taskList=taskList;
        final boolean ignoredProgress=request.isIgnoredProgress();
        final int updateProgressTimes=request.getUpdateProgressTimes();
        this.progressDisposer=new ProgressDisposer(ignoredProgress,updateProgressTimes,disposer);
        final boolean ignoredSpeed=request.isIgnoredSpeed();
        final int updateSpeedTimes=request.getUpdateSpeedTimes();
        this.speedDisposer=new SpeedDisposer(ignoredSpeed,updateSpeedTimes,disposer);
    }

    @Override
    public void onPending(IDownloadRequest task){
        listenerDisposer.onPending(task);
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

    public void onProgress(IDownloadRequest request,int length){
        speedLength+=length;

        if(progressDisposer.isCallProgress()){
            progressDisposer.onProgress(request,blockCount,successIndex);
        }

        if(speedDisposer.isCallSpeed()){
            speedDisposer.onSpeed(request,speedLength);
            speedLength=0;
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

                try{
                    //合并下载完成的文件
                    m3u8Merge(XDownUtils.getSaveFile(request),XDownUtils.getTempCacheDir(request),taskList);
                    listenerDisposer.onComplete(task);
                    XDownload.get().removeDownload(request.getTag());
                } catch(Exception e){
                    onFailure(task);
                }
            } else{
                onFailure(task);
            }
        }
    }


}
