package com.xjava.down;

import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.listener.OnDownloadListener;

import java.io.File;

public class Test{
    public static void main(String[] args){
        XDownload.download("https://hbimg.huabanimg.com/9113f5fa47b052e04585be3390cfdfb237d1eaba2bd35-9vp3qA_fw320")
                 .setCacheDir(new File(System.getProperty("user.dir"),"dasjidasjodiajs").getAbsolutePath())
                 .setSaveFile(new File(System.getProperty("user.dir"),"sdjiadjoasid.jpg").getAbsolutePath())
                 .setUseMultiThread(false)
                 .setDownloadListener(new OnDownloadListener(){
                     @Override
                     public void onComplete(IDownloadRequest request){
                        System.out.println("onComplete");
                     }

                     @Override
                     public void onFailure(IDownloadRequest request){
                         System.out.println("onFailure");
                     }
                 }).start();
    }
}
