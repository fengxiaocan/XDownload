package com.xjava.down.core;

import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;

public interface HttpDownload extends IConnect{

    HttpDownload setSaveFile(String saveFile);

    HttpDownload setCacheDir(String cacheDir);

    HttpDownload setIgnoredProgress(boolean ignoredProgress);

    HttpDownload setUpdateProgressTimes(int updateProgressTimes);

    HttpDownload setIgnoredSpeed(boolean ignoredSpeed);

    HttpDownload setUpdateSpeedTimes(int updateSpeedTimes);

    HttpDownload setUseMultiThread(boolean useMultiThread);

    HttpDownload setMultiThreadCount(int multiThreadCount);

    HttpDownload setMultiThreadMaxDownloadSize(int multiThreadMaxDownloadSize);

    HttpDownload setMultiThreadMinDownloadSize(int multiThreadMinDownloadSize);

    HttpDownload setUseBreakpointResume(boolean useBreakpointResume);

    HttpDownload setDownloadListener(OnDownloadListener listener);

    HttpDownload setConnectListener(OnDownloadConnectListener listener);

    HttpDownload setOnProgressListener(OnProgressListener listener);

    HttpDownload setOnSpeedListener(OnSpeedListener listener);

    @Override
    HttpDownload setTag(String tag);

    @Override
    HttpDownload addParams(String name,String value);

    @Override
    HttpDownload addHeader(String name,String value);

    @Override
    HttpDownload setUserAgent(String userAgent);

    @Override
    HttpDownload setConnectTimeOut(int connectTimeOut);

    @Override
    HttpDownload setUseAutoRetry(boolean useAutoRetry);

    @Override
    HttpDownload setAutoRetryTimes(int autoRetryTimes);

    @Override
    HttpDownload setAutoRetryInterval(int autoRetryInterval);

    @Override
    HttpDownload setWifiRequired(boolean wifiRequired);

    @Override
    HttpDownload scheduleOn(Schedulers schedulers);
}
