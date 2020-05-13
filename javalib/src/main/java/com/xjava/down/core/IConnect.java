package com.xjava.down.core;

import com.xjava.down.data.Headers;
import com.xjava.down.data.Params;
import com.xjava.down.dispatch.Schedulers;

interface IConnect{

    IConnect setTag(String tag);

    IConnect addParams(String name,String value);

    IConnect addHeader(String name,String value);

    IConnect setParams(Params params);

    IConnect setHeader(Headers header);

    IConnect setUserAgent(String userAgent);

    IConnect setConnectTimeOut(int connectTimeOut);

    IConnect setUseAutoRetry(boolean useAutoRetry);

    IConnect setAutoRetryTimes(int autoRetryTimes);

    IConnect setAutoRetryInterval(int autoRetryInterval);

    IConnect setWifiRequired(boolean wifiRequired);

    IConnect scheduleOn(Schedulers schedulers);

    String start();
}
