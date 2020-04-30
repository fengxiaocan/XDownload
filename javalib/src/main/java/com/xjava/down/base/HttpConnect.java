package com.xjava.down.base;

import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;

public interface HttpConnect{

    HttpConnect setTag(String tag);

    HttpConnect addParams(String name,String value);

    HttpConnect addHeader(String name,String value);

    HttpConnect setUserAgent(String userAgent);

    HttpConnect setConnectTimeOut(int connectTimeOut);

    HttpConnect setUseCaches(boolean useCaches);

    HttpConnect setUseAutoRetry(boolean useAutoRetry);

    HttpConnect setAutoRetryTimes(int autoRetryTimes);

    HttpConnect setAutoRetryInterval(int autoRetryInterval);

    HttpConnect setWifiRequired(boolean wifiRequired);

    HttpConnect setRequestMothod(Mothod mothod);

    HttpConnect addOnResponseListener(OnResponseListener listener);

    HttpConnect addOnConnectListener(OnConnectListener listener);

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
