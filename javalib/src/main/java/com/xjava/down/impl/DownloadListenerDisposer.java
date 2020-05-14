package com.xjava.down.impl;

import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;

public class DownloadListenerDisposer
        implements OnDownloadConnectListener, OnDownloadListener, OnProgressListener, OnSpeedListener{
    private Schedulers schedulers;
    private OnDownloadListener onDownloadListener;
    private OnProgressListener onProgressListener;
    private OnSpeedListener onSpeedListener;
    private OnDownloadConnectListener onConnectListener;

    public DownloadListenerDisposer(
            Schedulers schedulers,
            OnDownloadConnectListener onConnectListener,
            OnDownloadListener onDownloadListener,
            OnProgressListener onProgressListener,
            OnSpeedListener onSpeedListener)
    {
        this.schedulers=schedulers;
        this.onConnectListener=onConnectListener;
        this.onDownloadListener=onDownloadListener;
        this.onProgressListener=onProgressListener;
        this.onSpeedListener=onSpeedListener;
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
    public void onRequestError(final IDownloadRequest request,final int code,final String error){
        if(onConnectListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onConnectListener.onRequestError(request,code,error);
                }
            });
        } else{
            onConnectListener.onRequestError(request,code,error);
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

    @Override
    public void onProgress(final IDownloadRequest request,final float progress){
        if(onProgressListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onProgressListener.onProgress(request,progress);
                }
            });
        } else{
            onProgressListener.onProgress(request,progress);
        }
    }

    @Override
    public void onSpeed(final IDownloadRequest request,final int speed,final int time){
        if(onSpeedListener==null){
            return;
        }
        if(schedulers!=null){
            schedulers.schedule(new Runnable(){
                @Override
                public void run(){
                    onSpeedListener.onSpeed(request,speed,time);
                }
            });
        } else{
            onSpeedListener.onSpeed(request,speed,time);
        }
    }
}
