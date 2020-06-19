package com.xjava.down.made;

import java.io.Serializable;

public class M3u8Block implements Serializable{
    private long contentLength;//文件大小
    private String name;//名称
    private String url;//下载地址

    public M3u8Block(){
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
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
