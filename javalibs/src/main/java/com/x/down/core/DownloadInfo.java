package com.x.down.core;

import android.net.Uri;

/**
 * 下载文件的信息
 */
public class DownloadInfo{
    private long id;//id
    private @IStatus int status;//下载状态
    private String url;//下载的文件地址
    private Uri file;//保存的文件地址
    private long total;//文件总大小
    private long sofar;//已下载的大小
    private String mimeType;//文件的类型

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public Uri getFile(){
        return file;
    }

    public void setFile(Uri file){
        this.file = file;
    }

    public long getTotal(){
        return total;
    }

    public void setTotal(long total){
        this.total = total;
    }

    public long getSofar(){
        return sofar;
    }

    public void setSofar(long sofar){
        this.sofar = sofar;
    }

    public String getMimeType(){
        return mimeType;
    }

    public void setMimeType(String mimeType){
        this.mimeType = mimeType;
    }
}
