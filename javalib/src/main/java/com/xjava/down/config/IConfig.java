package com.xjava.down.config;

public interface IConfig{
    int MULTI_THREAD_MAX_DOWNLOAD_SIZE=100*1024;//默认多线程下载的单线程最大下载文件块大小,默认100KB

    int MULTI_THREAD_MIN_DOWNLOAD_SIZE=50*1024;//多线程下载的单线程最大下载文件块大小50KB

    IConfig cacheDir(String cacheDir);

    IConfig sameTimeDownloadCount(int sameTimeDownloadCount);

    IConfig userAgent(String userAgent);

    IConfig multiThreadCount(int multiThreadCount);

    IConfig multiThreadMaxSize(int multiThreadMaxSize);

    IConfig multiThreadMinSize(int multiThreadMinSize);

    IConfig isUseMultiThread(boolean isUseMultiThread);

    IConfig isUseBreakpointResume(boolean isUseBreakpointResume);

    IConfig ignoredProgress(boolean ignoredProgress);

    IConfig ignoredSpeed(boolean ignoredSpeed);

    IConfig isUseAutoRetry(boolean isUseAutoRetry);

    IConfig autoRetryTimes(int autoRetryTimes);

    IConfig autoRetryInterval(int autoRetryInterval);

    IConfig updateProgressTimes(int updateProgressTimes);

    IConfig updateSpeedTimes(int updateSpeedTimes);

    IConfig isWifiRequired(boolean isWifiRequired);

    IConfig connectTimeOut(int connectTimeOut);

    IConfig defaultName(@DefaultName int defaultName);

    @interface DefaultName{
        int MD5=0;//MD5值
        int TIME=1;//时间
        int ORIGINAL=2;//原名称
    }
}
