package com.x.down.core;

import com.x.down.data.Headers;
import com.x.down.data.Params;
import com.x.down.dispatch.Schedulers;
import com.x.down.listener.OnDownloadConnectListener;
import com.x.down.listener.OnDownloadListener;
import com.x.down.listener.OnProgressListener;
import com.x.down.listener.OnSpeedListener;
import com.x.down.listener.SSLCertificateFactory;

import java.io.File;

public interface HttpDownload extends IConnect {

    HttpDownload setFileName(String name);

    HttpDownload setFileDir(String dir);

    HttpDownload setFileDir(File dir);

    HttpDownload setSaveFile(String saveFile);

    HttpDownload setSaveFile(File saveFile);

    HttpDownload setCacheDir(String cacheDir);

    HttpDownload setIgnoredProgress(boolean ignoredProgress);

    HttpDownload setUpdateProgressTimes(int updateProgressTimes);

    HttpDownload setIgnoredSpeed(boolean ignoredSpeed);

    HttpDownload setUpdateSpeedTimes(int updateSpeedTimes);

    HttpDownload setUseMultiThread(boolean useMultiThread);

    HttpDownload setBufferedSize(int bufferedSize);

    HttpDownload setMultiThreadCount(int multiThreadCount);

    HttpDownload setMultiThreadMaxDownloadSize(int multiThreadMaxDownloadSize);

    HttpDownload setMultiThreadMinDownloadSize(int multiThreadMinDownloadSize);

    HttpDownload setUseBreakpointResume(boolean useBreakpointResume);

    HttpDownload setDownloadListener(OnDownloadListener listener);

    HttpDownload setConnectListener(OnDownloadConnectListener listener);

    HttpDownload setOnProgressListener(OnProgressListener listener);

    HttpDownload setOnSpeedListener(OnSpeedListener listener);

    HttpDownload delete();

    @Override
    HttpDownload setTag(String tag);

    @Override
    HttpDownload setSSLCertificate(String path);

    @Override
    HttpDownload setSSLCertificateFactory(SSLCertificateFactory factory);

    @Override
    HttpDownload addParams(String name, String value);

    @Override
    HttpDownload addHeader(String name, String value);

    @Override
    HttpDownload setParams(Params params);

    @Override
    HttpDownload setHeader(Headers header);

    @Override
    HttpDownload setUserAgent(String userAgent);

    @Override
    HttpDownload setConnectTimeOut(int connectTimeOut);

    @Override
    HttpDownload setIOTimeOut(int iOTimeOut);

    @Override
    HttpDownload setUseAutoRetry(boolean useAutoRetry);

    @Override
    HttpDownload setAutoRetryTimes(int autoRetryTimes);

    @Override
    HttpDownload setAutoRetryInterval(int autoRetryInterval);

    @Override
    HttpDownload permitAllSslCertificate(boolean wifiRequired);

    @Override
    HttpDownload scheduleOn(Schedulers schedulers);
}
