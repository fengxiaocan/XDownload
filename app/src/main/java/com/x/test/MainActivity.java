package com.x.test;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.x.down.AndroidDownload;
import com.xjava.down.base.IDownloadRequest;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidDownload.init(this);

        AndroidDownload.download("").setDownloadListener(new com.xjava.down.listener.OnDownloadListener(){
            @Override
            public void onComplete(IDownloadRequest iDownloadRequest){

            }

            @Override
            public void onFailure(IDownloadRequest iDownloadRequest){

            }
        }).start();

    }

}
