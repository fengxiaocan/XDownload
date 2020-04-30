package com.xjava.down.core;

import com.xjava.down.ExecutorGather;
import com.xjava.down.XDownload;
import com.xjava.down.base.HttpConnect;
import com.xjava.down.base.RequestBody;
import com.xjava.down.data.Headers;
import com.xjava.down.data.Params;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;
import com.xjava.down.task.HttpRequestTask;
import com.xjava.down.tool.XDownUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class XHttpRequest implements HttpConnect{
    protected String tag;//标记

    protected String baseUrl;//下载地址
    protected Headers headers;//头部信息
    protected Params params;//参数
    protected Mothod mothod=Mothod.GET;//请求方法
    protected RequestBody requestBody;//请求体,只有POST方法才有
    protected String userAgent=XDownload.get().config().getUserAgent();//默认UA
    protected boolean useCaches=false;//是否使用缓存
    protected List<OnConnectListener> onConnectListeners;
    protected List<OnResponseListener> onResponseListeners;


    protected boolean isWifiRequired=XDownload.get().config().isWifiRequired();//是否仅在WiFi情况下下载
    protected boolean isUseAutoRetry=XDownload.get().config().isUseAutoRetry();//是否使用出错自动重试
    protected int autoRetryTimes=XDownload.get().config().getAutoRetryTimes();//自动重试次数
    protected int autoRetryInterval=XDownload.get().config().getAutoRetryInterval();//自动重试间隔
    protected int connectTimeOut=XDownload.get().config().getConnectTimeOut();//连接超时

    protected volatile String connectUrl;//下载地址
    protected volatile String identifier;


    public static XHttpRequest with(String url){
        return new XHttpRequest(url);
    }

    protected XHttpRequest(String baseUrl){
        this.baseUrl=baseUrl;
    }

    @Override
    public HttpConnect setTag(String tag){
        this.tag=tag;
        return this;
    }

    @Override
    public HttpConnect addParams(String name,String value){
        if(params==null){
            params=new Params();
        }
        params.addParams(name,value);
        this.connectUrl=null;
        this.identifier=null;
        return this;
    }

    @Override
    public HttpConnect addHeader(String name,String value){
        if(headers==null){
            headers=new Headers();
        }
        headers.addHeader(name,value);
        return this;
    }

    @Override
    public HttpConnect setUserAgent(String userAgent){
        this.userAgent=userAgent;
        return this;
    }

    @Override
    public HttpConnect setConnectTimeOut(int connectTimeOut){
        this.connectTimeOut=connectTimeOut;
        return this;
    }

    @Override
    public HttpConnect setUseCaches(boolean useCaches){
        this.useCaches=useCaches;
        return this;
    }

    @Override
    public HttpConnect setUseAutoRetry(boolean useAutoRetry){
        this.isUseAutoRetry=useAutoRetry;
        return this;
    }

    @Override
    public HttpConnect setAutoRetryTimes(int autoRetryTimes){
        this.autoRetryTimes=autoRetryTimes;
        return this;
    }

    @Override
    public HttpConnect setAutoRetryInterval(int autoRetryInterval){
        this.autoRetryInterval=autoRetryInterval;
        return this;
    }

    @Override
    public HttpConnect setWifiRequired(boolean wifiRequired){
        this.isWifiRequired=wifiRequired;
        return this;
    }

    @Override
    public HttpConnect setRequestMothod(Mothod mothod){
        this.mothod=mothod;
        return this;
    }

    @Override
    public HttpConnect addOnResponseListener(OnResponseListener listener){
        if(onResponseListeners==null){
            onResponseListeners=new ArrayList<>();
        }
        onResponseListeners.add(listener);
        return this;
    }

    @Override
    public HttpConnect addOnConnectListener(OnConnectListener listener){
        if(onConnectListeners==null){
            onConnectListeners=new ArrayList<>();
        }
        onConnectListeners.add(listener);
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

    public String getConnectUrl(){
        if(connectUrl!=null){
            return connectUrl;
        }
        identifier=null;
        if(params==null||params.size()==0){
            return baseUrl;
        }
        StringBuilder builder=new StringBuilder(baseUrl);
        if(baseUrl.indexOf("?")>0){
            builder.append("&");
            params.toString(builder);
        } else{
            builder.append("?");
            params.toString(builder);
        }
        return connectUrl=builder.toString();
    }

    public String getIdentifier(){
        if(identifier!=null){
            return identifier;
        }
        final String rUrl=getConnectUrl();
        return identifier=XDownUtils.getMd5(rUrl);
    }

    @Override
    public String start(){
        HttpRequestTask requestTask=new HttpRequestTask(this,onConnectListeners,onResponseListeners);
        Future future=ExecutorGather.newThreadExecutor().submit(requestTask);
        requestTask.setTaskFuture(future);
        return getTag();
    }

    public String getTag(){
        if(tag==null){
            tag=getIdentifier();
        }
        return tag;
    }

    public String getUserAgent(){
        return userAgent;
    }

    public RequestBody getRequestBody(){
        return requestBody;
    }

    public boolean isPost(){
        return mothod==Mothod.POST;
    }

    public boolean isWifiRequired(){
        return isWifiRequired;
    }

    public boolean isUseAutoRetry(){
        return isUseAutoRetry;
    }

    public int getAutoRetryTimes(){
        return autoRetryTimes;
    }

    public int getAutoRetryInterval(){
        return autoRetryInterval;
    }

    public int getConnectTimeOut(){
        return connectTimeOut;
    }

    public HttpURLConnection buildConnect() throws Exception{
        URL url=new URL(getConnectUrl());
        HttpURLConnection http=(HttpURLConnection)url.openConnection();
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
        if(connectTimeOut>0){
            http.setConnectTimeout(connectTimeOut);
            http.setReadTimeout(connectTimeOut);
        }
        http.setUseCaches(useCaches);
        http.setDoInput(true);
        http.setDoOutput(mothod==Mothod.POST);
        return http;
    }
}
