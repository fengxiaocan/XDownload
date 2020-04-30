package com.xjava.down;

import com.xjava.down.base.HttpConnect;
import com.xjava.down.base.HttpDownload;
import com.xjava.down.config.XConfig;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.core.XHttpRequest;

import java.io.File;

public final class XDownload{

    private static XDownload xDownload;
    private XConfig setting;

    private XDownload(){
    }

    public static synchronized XDownload get(){
        if(xDownload==null){
            xDownload=new XDownload();
        }
        return xDownload;
    }

    public XDownload config(XConfig setting){
        this.setting=setting;
        return this;
    }

    public synchronized XConfig config(){
        if(setting==null){
            String xDownload=new File(System.getProperty("user.dir"),"xDownload").getAbsolutePath();
            setting=XConfig.with(xDownload).build();
        }
        return setting;
    }

     static void getRequest(String tag){

    }

     static void getDownload(String tag){

    }

    public static HttpConnect request(String baseUrl){
        return XHttpRequest.with(baseUrl);
    }

    public static HttpDownload download(String baseUrl){
        return XDownloadRequest.with(baseUrl);
    }
}
