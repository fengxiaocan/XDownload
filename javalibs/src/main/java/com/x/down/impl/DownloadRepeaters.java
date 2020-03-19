package com.x.down.impl;

import com.x.down.interfaces.Repeaters;
import com.x.down.interfaces.XHttpInterceptor;
import com.x.down.net.Request;
import com.x.down.net.Response;
import com.x.down.net.XConnection;

import java.io.IOException;

public class DownloadRepeaters implements Repeaters{
    private XHttpInterceptor interceptor;

    public DownloadRepeaters setInterceptor(XHttpInterceptor interceptor){
        this.interceptor = interceptor;
        return this;
    }

    @Override
    public Response proceed(Request request) throws IOException{
        Request realRequest = request;
        if(interceptor != null){
            Request temp = interceptor.request(realRequest);
            if(temp != null){
                realRequest = temp;
            }
        }
        XConnection xConnection = new XConnection(realRequest);
        int responseCode = xConnection.getResponseCode();

        if(responseCode > 200 && responseCode < 300){
            //断点下载返回码为206，而不是200，不同的网站可能有区别，断点请求失败的返回码为416
        } else{
            //断点请求失败
        }

        Response response = new Response();
        if(interceptor != null){
            Response temp = interceptor.response(realRequest,response);
            if(temp != null){
                response = temp;
            }
        }
        return response;
    }

}
