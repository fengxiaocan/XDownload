package com.x.test;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.x.down.AndroidDownload;
import com.xjava.down.base.IDownloadRequest;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidDownload.init(this);

        AndroidDownload.download("http://s1.dgtle.com/dgtle_img/ins/2020/05/14/22eae202005141151212323_1800_500.png").setDownloadListener(new com.xjava.down.listener.OnDownloadListener(){
            @Override
            public void onComplete(IDownloadRequest iDownloadRequest){
                Log.e("noah","onComplete");
            }

            @Override
            public void onFailure(IDownloadRequest iDownloadRequest){
                Log.e("noah","onFailure");
            }
        }).start();

    }

}
