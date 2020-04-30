package com.x.test;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.x.down.AndroidDownload;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.listener.OnDownloadListener;

public class MainActivity extends AppCompatActivity{
    public static class MyHandler extends Handler{

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidDownload.init(this);


//        AndroidDownload.request("https://opser.api.dgtle.com/v1/video/emotion")
//                       .post()
//                       .setUserAgent("Dgtle/4.3 OkHttp/Android 10 Xiaomi MI 9/QKQ1.190825.002")
//                       .addHeader("Authorization",
//                                  "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvb3BzZXIuYXBpLmRndGxlLmNvbSIsImF1ZCI6Imh0dHBzOlwvXC9vcHNlci5hcGkuZGd0bGUuY29tIiwiaWF0IjoxNTg3OTY4ODUzLCJuYmYiOjE1ODc5Njg4NTMsImV4cCI6MTU5MDU2MDg1MiwicmZ0IjoxNTkwNTYwODUyLCJqdGkiOiI0Mm5qcTgzNCJ9.sD0kKHy_RecgZMwFNPaIkNBT_Axnh33GAdLZHNc09xE")
////                       .requestBody(new FormBody().addFormData("vid","15").addFormData("emotion","1"))
//                       .requestBody(new MultipartBody(MediaType.FORM)
//                                            .addPart(MultipartBody.Part.createFormData("vid","15"))
//                                            .addPart(MultipartBody.Part.createFormData("emotion","1")))
//                       .setRequestListener(new RequestListener(){
//                           @Override
//                           public void onPending(IRequest task){
//                               Log.e("noah","onPending");
//                           }
//
//                           @Override
//                           public void onStart(IRequest task){
//                               Log.e("noah","onStart");
//                           }
//
//                           @Override
//                           public void onConnecting(IRequest task){
//                               Log.e("noah","onConnecting");
//                           }
//
//                           @Override
//                           public void onError(IRequest task,Exception exception){
//                               Log.e("noah","onError");
//                           }
//
//                           @Override
//                           public void onRetry(IRequest task,Exception exception){
//                               Log.e("noah","onRetry="+task.retryCount());
//                           }
//
//                           @Override
//                           public void onSuccess(IRequest task,String result){
//                               Log.e("noah","onSuccess="+result);
//                           }
//
//                           @Override
//                           public void onFailure(IRequest task,String result){
//                               Log.e("noah","onFailure="+result);
//                           }
//                       })
//                       .start();
        AndroidDownload.download("https://download.jetbrains.8686c.com/idea/ideaIC-2020.1.dmg")
                       .setDownListener(new OnDownloadListener(){
                           @Override
                           public void onPending(IDownloadRequest task){
                               Log.e("noah","onPending");
                           }

                           @Override
                           public void onStart(IDownloadRequest task){
                               Log.e("noah","onStart");
                           }

                           @Override
                           public void onPrepare(IDownloadRequest task){
                               Log.e("noah","onPrepare");
                           }

                           @Override
                           public void onProgress(float progress){
                               Log.e("noah","onProgress="+(int)(progress*100));
                           }

                           @Override
                           public void onPause(IDownloadRequest task){
                               Log.e("noah","onPause");
                           }

                           @Override
                           public void onError(IDownloadRequest task,Exception exception){
                               Log.e("noah","onError");
                           }

                           @Override
                           public void onRetry(IDownloadRequest task,Exception exception){
                               Log.e("noah","onRetry");
                           }

                           @Override
                           public void onComplete(IDownloadRequest task){
                               Log.e("noah","onComplete");
                           }

                           @Override
                           public void onDownloadComplete(){
                               Log.e("noah","onDownloadComplete");
                           }

                           @Override
                           public void onDownloadFailure(){
                               Log.e("noah","onDownloadFailure");
                           }
                       })
                       .start();
//        String size=Formatter.formatFileSize(this,1024*1024*100);
    }

}
