package com.xjava.down.made;

import java.util.ArrayList;

public class M3u8Memory{
    protected String originalUrl;//原始地址
    protected String readUrl;//重定向地址
    protected String identifier;//标记
    protected String response;//响应报文
    protected long length;//下载的文件长度
    protected ArrayList<M3u8Block> blockList;

    public String getOriginalUrl(){
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl){
        this.originalUrl=originalUrl;
    }

    public String getReadUrl(){
        return readUrl;
    }

    public void setReadUrl(String readUrl){
        this.readUrl=readUrl;
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

    public ArrayList<M3u8Block> getBlockList(){
        return blockList;
    }

    public void setBlockList(ArrayList<M3u8Block> blockList){
        this.blockList=blockList;
    }

    public String getResponse(){
        return response;
    }

    public void setResponse(String response){
        this.response=response;
    }
}
