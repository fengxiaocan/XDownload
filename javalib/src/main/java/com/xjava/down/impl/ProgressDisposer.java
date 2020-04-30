package com.xjava.down.impl;

import com.xjava.down.listener.OnDownloadListener;

public final class ProgressDisposer{
    private volatile long lastTime;
    private final boolean ignoredProgress;
    private final long updateProgressTimes;
    private final OnDownloadListener listener;

    public ProgressDisposer(
            boolean ignoredProgress,long updateProgressTimes,OnDownloadListener listener)
    {
        this.ignoredProgress=ignoredProgress;
        this.updateProgressTimes=updateProgressTimes;
        this.listener=listener;
    }

    public boolean isCallProgress(){
        if(ignoredProgress||updateProgressTimes<=0){
            return false;
        }
        synchronized(Object.class){
            final long l=System.currentTimeMillis()-lastTime;
            return l >= updateProgressTimes;
        }
    }

    public void onProgress(final long totalLength,final long sofarLength){
        if(totalLength>0){
            lastTime=System.currentTimeMillis();
            listener.onProgress(sofarLength*1F/totalLength);
        }
    }
}
