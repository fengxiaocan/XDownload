package com.x.down.core;

import com.x.down.data.Headers;
import com.x.down.data.Params;
import com.x.down.dispatch.Schedulers;
import com.x.down.listener.OnConnectListener;
import com.x.down.listener.OnResponseListener;
import com.x.down.listener.SSLCertificateFactory;
import com.x.down.base.RequestBody;

public interface HttpConnect extends IConnect {

    @Override
    HttpConnect setTag(String tag);

    @Override
    HttpConnect setSSLCertificate(String path);

    @Override
    HttpConnect setSSLCertificateFactory(SSLCertificateFactory factory);

    @Override
    HttpConnect addParams(String name, String value);

    @Override
    HttpConnect addHeader(String name, String value);

    @Override
    HttpConnect setParams(Params params);

    @Override
    HttpConnect setHeader(Headers header);

    @Override
    HttpConnect setUserAgent(String userAgent);

    @Override
    HttpConnect setConnectTimeOut(int connectTimeOut);

    @Override
    HttpConnect setIOTimeOut(int iOTimeOut);

    @Override
    HttpConnect setUseAutoRetry(boolean useAutoRetry);

    @Override
    HttpConnect setAutoRetryTimes(int autoRetryTimes);

    @Override
    HttpConnect setAutoRetryInterval(int autoRetryInterval);

    @Override
    HttpConnect permitAllSslCertificate(boolean wifiRequired);

    @Override
    HttpConnect scheduleOn(Schedulers schedulers);

    HttpConnect setUseCaches(boolean useCaches);

    HttpConnect setRequestMethod(Method method);

    HttpConnect setOnResponseListener(OnResponseListener listener);

    HttpConnect setOnConnectListener(OnConnectListener listener);

    HttpConnect post();

    HttpConnect requestBody(RequestBody body);

    String start();

    enum Method {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD"),
        DELETE("DELETE");

        private final String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }
}
