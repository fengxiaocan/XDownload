package com.xjava.down.impl;

import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.base.IDownloadRequest;

public class DownloadListenerDisposer implements OnDownloadListener{
    final OnDownloadListener downloadListener;

    public DownloadListenerDisposer(OnDownloadListener downloadListener){
        this.downloadListener=downloadListener;
    }

    @Override
    public void onPending(IDownloadRequest task){
        if(downloadListener != null){
            downloadListener.onPending(task);
        }
    }

    @Override
    public void onStart(IDownloadRequest task){
        if(downloadListener != null){
            downloadListener.onStart(task);
        }
    }

    @Override
    public void onPrepare(IDownloadRequest task){
        if(downloadListener != null){
            downloadListener.onPrepare(task);
        }
    }

    @Override
    public void onProgress(float progress){
        if(downloadListener != null){
            downloadListener.onProgress(progress);
        }
    }

    @Override
    public void onPause(IDownloadRequest task){
        if(downloadListener != null){
            downloadListener.onPause(task);
        }
    }

    @Override
    public void onError(IDownloadRequest task,Exception exception){
        if(downloadListener != null){
            downloadListener.onError(task,exception);
        }
    }

    @Override
    public void onRetry(IDownloadRequest task,Exception exception){
        if(downloadListener != null){
            downloadListener.onRetry(task,exception);
        }
    }


    @Override
    public void onComplete(IDownloadRequest task){
        if(downloadListener != null){
            downloadListener.onComplete(task);
        }
    }

    @Override
    public void onDownloadComplete(){
        if(downloadListener != null){
            downloadListener.onDownloadComplete();
        }
    }

    @Override
    public void onDownloadFailure(){
        if(downloadListener != null){
            downloadListener.onDownloadFailure();
        }
    }
}
