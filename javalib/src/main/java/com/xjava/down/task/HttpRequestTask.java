package com.xjava.down.task;


import com.xjava.down.base.HttpConnect;
import com.xjava.down.base.IRequest;
import com.xjava.down.base.RequestBody;
import com.xjava.down.base.RequestStatus;
import com.xjava.down.core.XHttpRequest;
import com.xjava.down.data.Headers;
import com.xjava.down.data.MediaType;
import com.xjava.down.data.Response;
import com.xjava.down.impl.RequestListenerDisposer;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;
import com.xjava.down.made.AutoRetryRecorder;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public class HttpRequestTask extends BaseHttpRequest implements IRequest{

    protected final XHttpRequest httpRequest;
    protected final RequestListenerDisposer listenerDisposer;
    protected volatile int status=0;
    protected Future taskFuture;

    public HttpRequestTask(
            XHttpRequest request,List<OnConnectListener> connectListeners,List<OnResponseListener> onResponseListeners)
    {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.listenerDisposer=new RequestListenerDisposer(connectListeners,onResponseListeners);
        this.httpRequest=request;
        status=RequestStatus.PENDING;
        listenerDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    public void run(){
        status=RequestStatus.STARTED;
        listenerDisposer.onStart(this);
        super.run();
    }

    @Override
    protected void httpRequest() throws Exception{
        HttpURLConnection http=httpRequest.buildConnect();
        status=RequestStatus.CONNECTED;
        //预备中
        listenerDisposer.onConnecting(this);

        if(httpRequest.isPost()){
            RequestBody body=httpRequest.getRequestBody();

            if(body!=null){
                MediaType mediaType=body.contentType();
                if(mediaType.getType()!=null){
                    http.setRequestProperty("Content-Type",mediaType.getType());
                }
                if(body.contentLength()!=-1){
                    http.setRequestProperty("Content-Length",String.valueOf(body.contentLength()));
                }
                HttpIoSink ioSink=new HttpIoSink(http.getOutputStream());
                body.writeTo(ioSink);
            }
        }

        int code=http.getResponseCode();

        Headers headers=getHeaders(http);

        if(isSuccess(code)){
            String stream=readStringStream(http.getInputStream());
            http.disconnect();
            status=RequestStatus.COMPLETED;
            listenerDisposer.onResponse(this,Response.builderSuccess(stream,code,headers));
        } else{
            String error=readStringStream(http.getErrorStream());
            http.disconnect();
            status=RequestStatus.WARN;
            listenerDisposer.onResponse(this,Response.builderFailure(code,headers,error));
            retryToRun();
        }
    }

    protected Headers getHeaders(HttpURLConnection http){
        Headers headers=new Headers();
        Map<String,List<String>> map=http.getHeaderFields();
//        System.out.println("显示响应Header信息...");
        Set<String> set=map.keySet();
        for(String key: set){
            List<String> list=map.get(key);
            if(list!=null&&list.size()>0){
                if(list.size()==1){
                    headers.addHeader(key,list.get(0));
                } else{
                    headers.addHeader(key,list.toString());
                }
            } else{
                headers.addHeader(key,"");
            }
        }
        return headers;
    }

    @Override
    protected void onRetry(){
        status=RequestStatus.RETRY;
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e){
        status=RequestStatus.ERROR;
        listenerDisposer.onError(this,e);
    }


    @Override
    public HttpConnect request(){
        return httpRequest;
    }

    @Override
    public String tag(){
        return httpRequest.getTag();
    }

    @Override
    public String url(){
        return httpRequest.getConnectUrl();
    }

    @Override
    public int status(){
        return 0;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
    }
}
