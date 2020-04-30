package com.xjava.down.base;

import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;

public interface HttpDownload extends HttpConnect{
    HttpDownload setTag(String tag);

    HttpDownload setSaveFile(String saveFile);

    HttpDownload setCacheDir(String cacheDir);

    HttpDownload setIgnoredProgress(boolean ignoredProgress);

    HttpDownload setUpdateProgressTimes(int updateProgressTimes);

    HttpDownload setUseMultiThread(boolean useMultiThread);

    HttpDownload setMultiThreadCount(int multiThreadCount);

    HttpDownload setMultiThreadMaxDownloadSize(int multiThreadMaxDownloadSize);

    HttpDownload setMultiThreadMinDownloadSize(int multiThreadMinDownloadSize);

    HttpDownload setUseBreakpointResume(boolean useBreakpointResume);

    HttpDownload setDownListener(OnDownloadListener listener);

    @Override
    HttpDownload addParams(String name,String value);

    @Override
    HttpDownload addHeader(String name,String value);

    @Override
    HttpDownload setUserAgent(String userAgent);

    @Override
    HttpDownload setConnectTimeOut(int connectTimeOut);

    @Override
    HttpDownload setUseCaches(boolean useCaches);

    @Override
    HttpDownload setUseAutoRetry(boolean useAutoRetry);

    @Override
    HttpDownload setAutoRetryTimes(int autoRetryTimes);

    @Override
    HttpDownload setAutoRetryInterval(int autoRetryInterval);

    @Override
    HttpDownload setWifiRequired(boolean wifiRequired);

    @Override
    HttpDownload setRequestMothod(Mothod mothod);

    @Override
    HttpDownload requestBody(RequestBody body);

    @Override
    HttpDownload addOnResponseListener(OnResponseListener listener);

    @Override
    HttpDownload addOnConnectListener(OnConnectListener listener);

    @Override
    HttpDownload post();
}
