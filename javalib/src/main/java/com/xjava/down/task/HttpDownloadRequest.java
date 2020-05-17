package com.xjava.down.task;

import com.xjava.down.XDownload;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.made.DownloaderMemory;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class HttpDownloadRequest extends BaseHttpRequest{

    protected final int byteArraySize;

    public HttpDownloadRequest(AutoRetryRecorder autoRetryRecorder,int byteSize){
        super(autoRetryRecorder);
        byteArraySize=Math.max(2048,byteSize);
    }

    //进度回调
    protected void onProgress(int length){ }

    //连接中
    protected void onConnecting(long length){}

    //获取文件长度
    protected HttpURLConnection getDownloaderLong(XDownloadRequest request) throws Exception{
        HttpURLConnection http=request.buildConnect();
        int responseCode=http.getResponseCode();

        while (isNeedRedirects(responseCode)){
            http = redirectsConnect(http,request);
            responseCode=http.getResponseCode();
        }
        //优先获取文件长度再回调
        long contentLength=XDownUtils.getContentLength(http);
        //连接中
        onConnecting(contentLength);

        if(contentLength<=0){
            //长度获取不到的时候重新连接 获取不到长度则要求http请求不要gzip压缩
            XDownUtils.disconnectHttp(http);
            http=request.buildConnect();
            http.setRequestProperty("Accept-Encoding","identity");
            http.connect();

            contentLength=XDownUtils.getContentLength(http);
            //连接中
            onConnecting(contentLength);
        }
        saveRequest(request,http,contentLength);

        return http;
    }

    /**
     * 保存请求信息
     * @param request
     * @param http
     * @param contentLength
     */
    private void saveRequest(XDownloadRequest request,HttpURLConnection http,long contentLength){
        try{
            DownloaderMemory information=new DownloaderMemory();
            information.setUrl(request.getConnectUrl());
            information.setIdentifier(request.getIdentifier());
            information.setLength(contentLength);
            XDownload.get().getMemoryHandler().saveRequestInfo(request,information);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    protected final boolean readInputStream(InputStream is,OutputStream os) throws IOException{
        try{
            byte[] bytes=new byte[byteArraySize];
            int length;
            while((length=is.read(bytes))>0){
                if(isCancel){
                    onCancel();
                    return false;
                }
                os.write(bytes,0,length);
                os.flush();
                onProgress(length);
            }
            return true;
        } finally{
            XDownUtils.closeIo(is);
            XDownUtils.closeIo(os);
        }
    }


    protected final boolean copyFile(File inputFile,File outputFile,boolean isDelect) throws IOException{
        if(inputFile==null||outputFile==null){
            return false;
        }
        if(!inputFile.exists()){
            return false;
        }
        if(inputFile==outputFile){
            return true;
        }
        if(inputFile.getAbsolutePath().equals(outputFile.getAbsolutePath())){
            return true;
        }
        if(outputFile.exists()&&outputFile.length()==inputFile.length()){
            if(isDelect){inputFile.delete();}
            return true;
        } else{
            inputFile.delete();
            FileOutputStream output=null;
            FileInputStream input=null;
            try{
                input=new FileInputStream(inputFile);
                output=new FileOutputStream(outputFile);

                byte[] bytes=new byte[1024*8];
                int length;
                while((length=input.read(bytes))>0){
                    output.write(bytes,0,length);
                    output.flush();
                }
                return true;
            } finally{
                XDownUtils.closeIo(input);
                XDownUtils.closeIo(output);
            }
        }
    }

}
