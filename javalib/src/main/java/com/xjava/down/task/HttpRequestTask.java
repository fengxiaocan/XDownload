package com.xjava.down.task;


import com.xjava.down.XDownload;
import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IRequest;
import com.xjava.down.base.RequestBody;
import com.xjava.down.core.HttpConnect;
import com.xjava.down.core.XHttpRequest;
import com.xjava.down.data.Headers;
import com.xjava.down.data.MediaType;
import com.xjava.down.data.Response;
import com.xjava.down.impl.RequestListenerDisposer;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.net.HttpURLConnection;
import java.util.concurrent.Future;

class HttpRequestTask extends BaseHttpRequest implements IRequest, IConnectRequest{

    protected final XHttpRequest httpRequest;
    protected final RequestListenerDisposer listenerDisposer;
    protected Future taskFuture;

    public HttpRequestTask(
            XHttpRequest request,OnConnectListener connectListeners,OnResponseListener onResponseListeners)
    {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.listenerDisposer=new RequestListenerDisposer(request.getSchedulers(),connectListeners,onResponseListeners);
        this.httpRequest=request;
        listenerDisposer.onPending(this);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    public void run(){
        listenerDisposer.onStart(this);
        super.run();
        XDownload.get().removeRequest(httpRequest.getTag());
    }

    @Override
    protected void httpRequest() throws Exception{
        HttpURLConnection http=httpRequest.buildConnect();

        listenerDisposer.onConnecting(this);
        //POST请求
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

        int responseCode=http.getResponseCode();
        //是否需要重定向
        if(isNeedRedirects(responseCode)){
            http=redirectsConnect(http,httpRequest);
            if(responseCode==307){
                http.setRequestMethod("GET");
            }
            http.connect();
            listenerDisposer.onConnecting(this);

            if(http.getRequestMethod().equals("POST")){
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
            responseCode=http.getResponseCode();
        }
        //请求头
        Headers headers=getHeaders(http);

        if(isSuccess(responseCode)){
            String stream=readStringStream(http.getInputStream());
            XDownUtils.disconnectHttp(http);
            listenerDisposer.onResponse(this,Response.builderSuccess(stream,responseCode,headers));
        } else{
            String error=readStringStream(http.getErrorStream());
            XDownUtils.disconnectHttp(http);
            listenerDisposer.onResponse(this,Response.builderFailure(responseCode,headers,error));
            retryToRun();
        }
    }

    @Override
    protected void onRetry(){
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e){
        listenerDisposer.onError(this,e);
    }

    @Override
    protected void onCancel(){
        listenerDisposer.onCancel(this);
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
    public boolean cancel(){
        isCancel=true;
        if(taskFuture!=null){
            return taskFuture.cancel(true);
        }
        return false;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
    }
}
