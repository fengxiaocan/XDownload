package com.xjava.down.core;

import com.xjava.down.base.RequestBody;
import com.xjava.down.data.Headers;
import com.xjava.down.data.Params;
import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;

public interface HttpConnect extends IConnect{

    @Override
    HttpConnect setTag(String tag);

    @Override
    HttpConnect addParams(String name,String value);

    @Override
    HttpConnect addHeader(String name,String value);

    @Override
    HttpConnect setParams(Params params);

    @Override
    HttpConnect setHeader(Headers header);

    @Override
    HttpConnect setUserAgent(String userAgent);

    @Override
    HttpConnect setConnectTimeOut(int connectTimeOut);

    @Override
    HttpConnect setUseAutoRetry(boolean useAutoRetry);

    @Override
    HttpConnect setAutoRetryTimes(int autoRetryTimes);

    @Override
    HttpConnect setAutoRetryInterval(int autoRetryInterval);

    @Override
    HttpConnect setWifiRequired(boolean wifiRequired);

    @Override
    HttpConnect scheduleOn(Schedulers schedulers);

    HttpConnect setUseCaches(boolean useCaches);

    HttpConnect setRequestMothod(Mothod mothod);

    HttpConnect setOnResponseListener(OnResponseListener listener);

    HttpConnect setOnConnectListener(OnConnectListener listener);

    HttpConnect post();

    HttpConnect requestBody(RequestBody body);

    enum Mothod{
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD"),
        DELETE("DELETE");

        private String mothod;

        Mothod(String mothod){
            this.mothod=mothod;
        }

        public String getMothod(){
            return mothod;
        }
    }

    String start();
}
