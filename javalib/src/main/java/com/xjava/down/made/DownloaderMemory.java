package com.xjava.down.made;

import java.io.Serializable;

public final class DownloaderMemory implements Serializable{
    protected String url;//基本地址
    protected String identifier;//标记
    protected long length;//下载的文件长度

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public String getIdentifier(){
        return identifier;
    }

    public void setIdentifier(String identifier){
        this.identifier=identifier;
    }

    public long getLength(){
        return length;
    }

    public void setLength(long length){
        this.length=length;
    }
}
