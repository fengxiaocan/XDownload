package com.x.down.config;

public interface IConfig {
    int MULTI_THREAD_MAX_DOWNLOAD_SIZE = 100 * 1024;//默认多线程下载的单线程最大下载文件块大小,默认100KB

    int MULTI_THREAD_MIN_DOWNLOAD_SIZE = 50 * 1024;//多线程下载的单线程最小下载文件块大小50KB

    IConfig cacheDir(String cacheDir);

    IConfig saveDir(String dir);

    IConfig sameTimeHttpRequestMaxCount(int sameTimeHttpRequestMaxCount);

    IConfig sameTimeDownloadCount(int sameTimeDownloadCount);

    IConfig userAgent(String userAgent);

    IConfig bufferedSize(int buffSize);

    IConfig sameTimeDownloadMaxCount(int multiThreadCount);

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

    IConfig permitAllSslCertificate(boolean permit);

    IConfig connectTimeOut(int connectTimeOut);

    IConfig iOTimeOut(int iOTimeOut);

    IConfig acquireName(AcquireNameInterceptor interceptor);

}
