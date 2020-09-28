package com.x.down.core;

import com.x.down.data.Headers;
import com.x.down.data.Params;
import com.x.down.dispatch.Schedulers;
import com.x.down.listener.SSLCertificateFactory;

interface IConnect {

    IConnect setTag(String tag);

    IConnect setSSLCertificate(String path);

    IConnect setSSLCertificateFactory(SSLCertificateFactory factory);

    IConnect addParams(String name, String value);

    IConnect addHeader(String name, String value);

    IConnect setParams(Params params);

    IConnect setHeader(Headers header);

    IConnect setUserAgent(String userAgent);

    IConnect setConnectTimeOut(int connectTimeOut);

    IConnect setIOTimeOut(int iOTimeOut);

    IConnect setUseAutoRetry(boolean useAutoRetry);

    IConnect setAutoRetryTimes(int autoRetryTimes);

    IConnect setAutoRetryInterval(int autoRetryInterval);

    IConnect permitAllSslCertificate(boolean wifiRequired);

    IConnect scheduleOn(Schedulers schedulers);

//    IConnect scheduleOnMain();

    String start();
}
