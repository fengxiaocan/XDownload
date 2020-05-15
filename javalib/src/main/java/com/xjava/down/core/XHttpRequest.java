package com.xjava.down.core;

import com.xjava.down.base.RequestBody;
import com.xjava.down.data.Headers;
import com.xjava.down.data.Params;
import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;
import com.xjava.down.listener.SSLCertificateFactory;
import com.xjava.down.task.ThreadTaskFactory;
import com.xjava.down.tool.XDownUtils;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class XHttpRequest extends BaseRequest implements HttpConnect,BuilderURLConnection{
    protected Mothod mothod=Mothod.GET;//请求方法
    protected RequestBody requestBody;//请求体,只有POST方法才有
    protected boolean useCaches=false;//是否使用缓存
    protected OnConnectListener onConnectListeners;
    protected OnResponseListener onResponseListeners;

    public OnConnectListener getOnConnectListeners(){
        return onConnectListeners;
    }

    public OnResponseListener getOnResponseListeners(){
        return onResponseListeners;
    }

    public static XHttpRequest with(String url){
        return new XHttpRequest(url);
    }

    protected XHttpRequest(String baseUrl){
        super(baseUrl);
    }

    @Override
    public HttpConnect setUseCaches(boolean useCaches){
        this.useCaches=useCaches;
        return this;
    }

    @Override
    public HttpConnect setRequestMothod(Mothod mothod){
        this.mothod=mothod;
        return this;
    }

    @Override
    public HttpConnect setOnResponseListener(OnResponseListener listener){
        onResponseListeners=listener;
        return this;
    }

    @Override
    public HttpConnect setOnConnectListener(OnConnectListener listener){
        onConnectListeners=listener;
        return this;
    }


    @Override
    public HttpConnect post(){
        this.mothod=Mothod.POST;
        return this;
    }

    @Override
    public HttpConnect requestBody(RequestBody body){
        this.requestBody=body;
        return this;
    }

    public RequestBody getRequestBody(){
        return requestBody;
    }

    public boolean isPost(){
        return mothod==Mothod.POST;
    }

    public boolean isUseCaches(){
        return useCaches;
    }

    public Mothod getMothod(){
        return mothod;
    }

    @Override
    public HttpConnect setTag(String tag){
        return (HttpConnect)super.setTag(tag);
    }

    @Override
    public HttpConnect setSSLCertificate(String path){
        return (HttpConnect)super.setSSLCertificate(path);
    }

    @Override
    public HttpConnect setSSLCertificateFactory(SSLCertificateFactory factory){
        return (HttpConnect)super.setSSLCertificateFactory(factory);
    }

    @Override
    public HttpConnect addParams(String name,String value){
        return (HttpConnect)super.addParams(name,value);
    }

    @Override
    public HttpConnect addHeader(String name,String value){
        return (HttpConnect)super.addHeader(name,value);
    }

    @Override
    public HttpConnect setParams(Params params){
        return (HttpConnect)super.setParams(params);
    }

    @Override
    public HttpConnect setHeader(Headers header){
        return (HttpConnect)super.setHeader(header);
    }

    @Override
    public HttpConnect setUserAgent(String userAgent){
        return (HttpConnect)super.setUserAgent(userAgent);
    }

    @Override
    public HttpConnect setConnectTimeOut(int connectTimeOut){
        return (HttpConnect)super.setConnectTimeOut(connectTimeOut);
    }

    @Override
    public HttpConnect setIOTimeOut(int iOTimeOut){
        return (HttpConnect)super.setIOTimeOut(iOTimeOut);
    }

    @Override
    public HttpConnect setUseAutoRetry(boolean useAutoRetry){
        return (HttpConnect)super.setUseAutoRetry(useAutoRetry);
    }

    @Override
    public HttpConnect setAutoRetryTimes(int autoRetryTimes){
        return (HttpConnect)super.setAutoRetryTimes(autoRetryTimes);
    }

    @Override
    public HttpConnect setAutoRetryInterval(int autoRetryInterval){
        return (HttpConnect)super.setAutoRetryInterval(autoRetryInterval);
    }

    @Override
    public HttpConnect permitAllSslCertificate(boolean wifiRequired){
        return (HttpConnect)super.permitAllSslCertificate(wifiRequired);
    }

    @Override
    public HttpConnect scheduleOn(Schedulers schedulers){
        return (HttpConnect)super.scheduleOn(schedulers);
    }

    @Override
    public String start(){
        ThreadTaskFactory.createHttpRequestTask(this);
        return getTag();
    }

    @Override
    public HttpURLConnection buildConnect(String connectUrl) throws Exception{
        URL url=new URL(connectUrl);
        HttpURLConnection http=(HttpURLConnection)url.openConnection();

        if(http instanceof HttpsURLConnection){
            HttpsURLConnection https=(HttpsURLConnection)http;
            //处理https证书
            SSLSocketFactory certificate=null;
            if(sslCertificateFactory!=null){
                certificate=sslCertificateFactory.createCertificate();
            } else if(certificatePath!=null){
                certificate=XDownUtils.getCertificate(certificatePath);
            } else if(permitAllSslCertificate){
                //允许所有的https证书
                certificate=XDownUtils.getUnSafeCertificate();
            }
            if(certificate!=null){
                https.setSSLSocketFactory(certificate);
            }
        }

        http.setRequestMethod(mothod.getMothod());
        //设置http请求头
        http.setRequestProperty("Connection","Keep-Alive");
        if(headers!=null){
            for(String key: headers.keySet()){
                http.setRequestProperty(key,headers.getValue(key));
            }
        }
        if(userAgent!=null){
            http.setRequestProperty("User-Agent",userAgent);
        }
        http.setConnectTimeout(Math.max(connectTimeOut,5*1000));
        http.setReadTimeout(Math.max(iOTimeOut,5*1000));
        //本次链接是否处理重定向
        http.setInstanceFollowRedirects(false);

        http.setUseCaches(useCaches);
        http.setDoInput(true);
        http.setDoOutput(mothod==Mothod.POST);
        return http;
    }

    @Override
    public HttpURLConnection buildConnect() throws Exception{
        return buildConnect(getConnectUrl());
    }
}
