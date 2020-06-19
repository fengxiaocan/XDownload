package com.xjava.down.made;

import java.util.ArrayList;

public class M3u8Memory{
    protected String identifier;//标记
    protected String originalUrl;//原始地址
    protected String redirectUrl;//重定向地址
    protected String originalResponse;//原始地址响应报文
    protected String redirectResponse;//重定向地址响应报文

    protected ArrayList<M3u8Block> blockList;//下载列表地址
//    protected long length;//下载的文件长度

    public String getOriginalUrl(){
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl){
        this.originalUrl=originalUrl;
    }

    public String getRedirectUrl(){
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl){
        this.redirectUrl=redirectUrl;
    }

    public String getIdentifier(){
        return identifier;
    }

    public void setIdentifier(String identifier){
        this.identifier=identifier;
    }


    public ArrayList<M3u8Block> getBlockList(){
        return blockList;
    }

    public void setBlockList(ArrayList<M3u8Block> blockList){
        this.blockList=blockList;
    }

    public String getOriginalResponse(){
        return originalResponse;
    }

    public void setOriginalResponse(String originalResponse){
        this.originalResponse=originalResponse;
    }

    public String getRedirectResponse(){
        return redirectResponse;
    }

    public void setRedirectResponse(String redirectResponse){
        this.redirectResponse=redirectResponse;
    }
}
