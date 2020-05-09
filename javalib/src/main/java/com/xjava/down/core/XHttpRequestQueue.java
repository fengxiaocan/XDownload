package com.xjava.down.core;

import com.xjava.down.task.ThreadTaskFactory;

import java.util.ArrayList;
import java.util.List;

public final class XHttpRequestQueue extends XHttpRequest{
    protected List<String> queue;

    protected XHttpRequestQueue(List<String> queue){
        super("");
        this.queue=queue;
    }

    public List<XHttpRequest> cloneToRequest(){
        List<XHttpRequest> xHttpRequests=new ArrayList<>();
        for(String baseUrl: queue){
            XHttpRequest request=new XHttpRequest(baseUrl);
            request.headers=headers;
            request.params=params;
            request.schedulers=schedulers;
            request.userAgent=userAgent;
            request.isWifiRequired=isWifiRequired;
            request.isUseAutoRetry=isUseAutoRetry;
            request.autoRetryTimes=autoRetryTimes;
            request.autoRetryInterval=autoRetryInterval;
            request.connectTimeOut=connectTimeOut;
            request.mothod=mothod;
            request.requestBody=requestBody;
            request.useCaches=useCaches;
            request.onConnectListeners=onConnectListeners;
            request.onResponseListeners=onResponseListeners;
            xHttpRequests.add(request);
        }
        return xHttpRequests;
    }

    @Override
    public String start(){
        ThreadTaskFactory.createHttpRequestTaskQueue(this);
        return tag;
    }

}
