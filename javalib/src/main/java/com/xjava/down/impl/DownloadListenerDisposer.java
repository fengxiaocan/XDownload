package com.xjava.down.impl;

import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;

public class DownloadListenerDisposer implements OnDownloadConnectListener, OnDownloadListener{
    private Schedulers schedulers;
    private OnDownloadListener onDownloadListener;
    private OnDownloadConnectListener onConnectListener;

    public DownloadListenerDisposer(
            Schedulers schedulers,OnDownloadConnectListener onConnectListener,OnDownloadListener onDownloadListener)
    {
        this.schedulers=schedulers;
        this.onConnectListener=onConnectListener;
        this.onDownloadListener=onDownloadListener;
    }

    @Override
    public void onPending(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onPending(request);
                }
            });
        } else{
            onConnectListener.onPending(request);
        }
    }

    @Override
    public void onStart(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onStart(request);
                }
            });
        } else{
            onConnectListener.onStart(request);
        }
    }

    @Override
    public void onConnecting(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onConnecting(request);
                }
            });
        } else{
            onConnectListener.onConnecting(request);
        }
    }

    @Override
    public void onCancel(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onCancel(request);
                }
            });
        } else{
            onConnectListener.onCancel(request);
        }
    }

    @Override
    public void onRetry(final IDownloadRequest request){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onRetry(request);
                }
            });
        } else{
            onConnectListener.onRetry(request);
        }
    }

    @Override
    public void onProgress(final IDownloadRequest request,final float progress,final int speed){
        if(onDownloadListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onDownloadListener.onProgress(request,progress,speed);
                }
            });
        } else{
            onDownloadListener.onProgress(request,progress,speed);
        }
    }

    @Override
    public void onComplete(final IDownloadRequest request){
        if(onDownloadListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onDownloadListener.onComplete(request);
                }
            });
        } else{
            onDownloadListener.onComplete(request);
        }
    }

    @Override
    public void onFailure(final IDownloadRequest request){
        if(onDownloadListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onDownloadListener.onFailure(request);
                }
            });
        } else{
            onDownloadListener.onFailure(request);
        }
    }
}
