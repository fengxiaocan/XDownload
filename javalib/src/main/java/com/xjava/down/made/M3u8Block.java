package com.xjava.down.made;

import java.io.Serializable;

public class M3u8Block implements Serializable{
    private long contentLength;//文件大小
    private String url;//下载地址

    public M3u8Block(){
    }

    public M3u8Block(long contentLength,String url){
        this.contentLength=contentLength;
        this.url=url;
    }

    public long getContentLength(){
        return contentLength;
    }

    public void setContentLength(long contentLength){
        this.contentLength=contentLength;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url=url;
    }
}
