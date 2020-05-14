package com.xjava.down.config;

public interface IConfig{
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
