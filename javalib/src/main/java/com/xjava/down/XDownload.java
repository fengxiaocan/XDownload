package com.xjava.down;

import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.base.IRequest;
import com.xjava.down.config.XConfig;
import com.xjava.down.core.HttpConnect;
import com.xjava.down.core.HttpDownload;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.core.XHttpRequest;
import com.xjava.down.dispatch.LogReport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class XDownload{

    private static XDownload xDownload;
    private XConfig setting;
    private LogReport logReport;
    private Map<String,IConnectRequest> connectMap=new HashMap<>();
    private Map<String,List<IConnectRequest>> downloadMap=new HashMap<>();
    private int maxThreadCount=30;//最大允许的多线程

    private XDownload(){
    }


    public static synchronized XDownload get(){
        if(xDownload==null){
            xDownload=new XDownload();
        }
        return xDownload;
    }

    int getMaxThreadCount(){
        return maxThreadCount;
    }

    public void setLogReport(LogReport logReport){
        this.logReport=logReport;
    }

    public LogReport log(){
        return logReport;
    }

    public void setMaxThreadCount(int count){
        this.maxThreadCount=count;
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


    public void addRequest(String tag,IConnectRequest connect){
        connectMap.put(tag,connect);
    }

    public IConnectRequest removeRequest(String tag){
        return connectMap.remove(tag);
    }

    public void addDownload(String tag,IConnectRequest download){
        List<IConnectRequest> requestList=downloadMap.get(tag);
        if(requestList!=null){
            requestList.add(download);
        } else{
            requestList=new ArrayList<>();
            requestList.add(download);
            downloadMap.put(tag,requestList);
        }
    }

    public List<IConnectRequest> removeDownload(String tag){
        return downloadMap.remove(tag);
    }
    /**
     * 取消请求
     * @param tag
     * @return
     */
    public boolean cancleRequest(String tag){
        IConnectRequest request=connectMap.remove(tag);
        if(request!=null){
            return request.cancel();
        }
        return false;
    }

    /**
     * 取消下载
     * @param tag
     * @return
     */
    public boolean cancleDownload(String tag){
        List<IConnectRequest> list=downloadMap.remove(tag);
        if(list!=null){
            boolean isCancel=false;
            for(IConnectRequest request: list){
                boolean cancel=request.cancel();
                if(!isCancel){
                    isCancel=cancel;
                }
            }
            return isCancel;
        }
        return false;
    }

    public static HttpConnect request(String baseUrl){
        return XHttpRequest.with(baseUrl);
    }

    public static HttpDownload download(String baseUrl){
        return XDownloadRequest.with(baseUrl);
    }
}