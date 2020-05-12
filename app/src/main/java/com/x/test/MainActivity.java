package com.x.test;


import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.x.down.AndroidDownload;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidDownload.init(this);

//        AndroidDownload.request("https://kan.jinbaozy.com/vodplayhtml/153-1-25.html")
//                       .setOnResponseListener(new OnResponseListener(){
//                           @Override
//                           public void onResponse(IRequest request,Response response){
//                               if(response.isSuccess()){
//                                   Log.e("noah",response.result());
//                               }else {
//                                   Log.e("noah",response.error());
//                               }
//                           }
//
//                           @Override
//                           public void onError(IRequest request,Exception exception){
//                               Log.e("noah","onError="+exception.getMessage());
//                           }
//                       })
//                       .start();
        AndroidDownload.download("http://down.dgtle.com/app/dgtle_4_3.apk")
                       .setUseMultiThread(false)
                       .setAutoRetryInterval(5000)
                       .setUpdateSpeedTimes(1000)
                       .setOnProgressListener(new OnProgressListener(){
                           @Override
                           public void onProgress(IDownloadRequest request,float progress){
                               Log.e("noah","progress="+(progress*100));
                           }
                       })
                       .setOnSpeedListener(new OnSpeedListener(){
                           @Override
                           public void onSpeed(IDownloadRequest request,int speed,int time){
                               Log.e("noah","onSpeed="+Formatter.formatFileSize(getBaseContext(),speed)+" time="+time);
                           }
                       })
                       .setDownloadListener(new OnDownloadListener(){
                           @Override
                           public void onComplete(IDownloadRequest request){
                               Log.e("noah","下载完成");
                           }

                           @Override
                           public void onFailure(IDownloadRequest request){
                               Log.e("noah","下载失败");
                           }
                       })
                       .setConnectListener(new OnDownloadConnectListener(){
                           @Override
                           public void onPending(IDownloadRequest request){
                               Log.e("noah","onPending");
                           }

                           @Override
                           public void onStart(IDownloadRequest request){
                               Log.e("noah","onStart");
                           }

                           @Override
                           public void onConnecting(IDownloadRequest request){
                               Log.e("noah","onConnecting");
                           }

                           @Override
                           public void onRequestError(IDownloadRequest request,int code,String error){
                               Log.e("noah","onRequestError code="+code+" error="+error);
                           }

                           @Override
                           public void onCancel(IDownloadRequest request){
                               Log.e("noah","onCancel");
                           }

                           @Override
                           public void onRetry(IDownloadRequest request){
                               Log.e("noah","onRetry");
                           }
                       })
                       .start();
    }

}
