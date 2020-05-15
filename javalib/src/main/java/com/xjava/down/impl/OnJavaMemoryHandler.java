package com.xjava.down.impl;

import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.listener.OnDownloaderMemoryHandler;
import com.xjava.down.made.DownloaderBlock;
import com.xjava.down.made.DownloaderMemory;
import com.xjava.down.made.M3u8Memory;
import com.xjava.down.tool.XDownUtils;

import java.io.File;

public class OnJavaMemoryHandler implements OnDownloaderMemoryHandler{
    private static final String INFO_NAME="MEMORY";
    private static final String BLOCK_NAME="BLOCK";

    @Override
    public void saveRequestInfo(XDownloadRequest request,DownloaderMemory info){
        File cacheDir=XDownUtils.getTempCacheDir(request);
        File file=new File(cacheDir,INFO_NAME);
        XDownUtils.writeObject(file,info);
    }

    @Override
    public DownloaderMemory queryDownloaderInfo(XDownloadRequest request){
        File cacheDir=XDownUtils.getTempCacheDir(request);
        if(cacheDir.exists()){
            File file=new File(cacheDir,INFO_NAME);
            if(file.exists()){
                return XDownUtils.readObject(file);
            }
        }
        return null;
    }

    @Override
    public void saveDownloaderBlock(XDownloadRequest request,DownloaderBlock block){
        File cacheDir=XDownUtils.getTempCacheDir(request);
        File file=new File(cacheDir,BLOCK_NAME);
        XDownUtils.writeObject(file,block);
    }

    @Override
    public DownloaderBlock queryDownloaderBlock(XDownloadRequest request){
        File cacheDir=XDownUtils.getTempCacheDir(request);
        if(cacheDir.exists()){
            File file=new File(cacheDir,BLOCK_NAME);
            if(file.exists()){
                return XDownUtils.readObject(file);
            }
        }
        return null;
    }

    @Override
    public void saveM3u8Memory(XDownloadRequest request,M3u8Memory block){

    }

    @Override
    public M3u8Memory queryM3u8Memory(XDownloadRequest request){
        return null;
    }
}
