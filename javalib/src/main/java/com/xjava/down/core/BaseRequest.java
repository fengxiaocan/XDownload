package com.xjava.down.core;

import com.xjava.down.XDownload;
import com.xjava.down.data.Headers;
import com.xjava.down.data.Params;
import com.xjava.down.dispatch.Schedulers;
import com.xjava.down.tool.XDownUtils;

abstract class BaseRequest implements IConnect{
    protected String tag;//标记

    protected String baseUrl;//下载地址
    protected Headers headers;//头部信息
    protected Params params;//参数
    protected Schedulers schedulers;//调度器
    protected String userAgent=XDownload.get().config().getUserAgent();//默认UA

    protected boolean isWifiRequired=XDownload.get().config().isWifiRequired();//是否仅在WiFi情况下下载
    protected boolean isUseAutoRetry=XDownload.get().config().isUseAutoRetry();//是否使用出错自动重试
    protected int autoRetryTimes=XDownload.get().config().getAutoRetryTimes();//自动重试次数
    protected int autoRetryInterval=XDownload.get().config().getAutoRetryInterval();//自动重试间隔
    protected int connectTimeOut=XDownload.get().config().getConnectTimeOut();//连接超时

    protected volatile String connectUrl;//下载地址
    protected volatile String identifier;//下载标志

    protected BaseRequest(String baseUrl){
        this.baseUrl=baseUrl;
    }

    @Override
    public IConnect setTag(String tag){
        this.tag=tag;
        return this;
    }

    @Override
    public IConnect addParams(String name,String value){
        if(params==null){
            params=new Params();
        }
        params.addParams(name,value);
        this.connectUrl=null;
        this.identifier=null;
        return this;
    }

    @Override
    public IConnect addHeader(String name,String value){
        if(headers==null){
            headers=new Headers();
        }
        headers.addHeader(name,value);
        return this;
    }

    @Override
    public IConnect setParams(Params params){
        this.params = params;
        return this;
    }

    @Override
    public IConnect setHeader(Headers header){
        this.headers = header;
        return this;
    }

    @Override
    public IConnect setUserAgent(String userAgent){
        this.userAgent=userAgent;
        return this;
    }

    @Override
    public IConnect setConnectTimeOut(int connectTimeOut){
        this.connectTimeOut=connectTimeOut;
        return this;
    }

    @Override
    public IConnect setUseAutoRetry(boolean useAutoRetry){
        this.isUseAutoRetry=useAutoRetry;
        return this;
    }

    @Override
    public IConnect setAutoRetryTimes(int autoRetryTimes){
        this.autoRetryTimes=autoRetryTimes;
        return this;
    }

    @Override
    public IConnect setAutoRetryInterval(int autoRetryInterval){
        this.autoRetryInterval=autoRetryInterval;
        return this;
    }

    @Override
    public IConnect setWifiRequired(boolean wifiRequired){
        this.isWifiRequired=wifiRequired;
        return this;
    }

    @Override
    public IConnect scheduleOn(Schedulers schedulers){
        this.schedulers=schedulers;
        return this;
    }

    public Schedulers getSchedulers(){
        return schedulers;
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


    public String getTag(){
        if(tag==null){
            tag=getIdentifier();
        }
        return tag;
    }

    public String getUserAgent(){
        return userAgent;
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

}
