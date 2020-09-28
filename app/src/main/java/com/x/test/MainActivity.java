package com.x.test;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.x.down.XDownload;
import com.x.down.base.IDownloadRequest;
import com.x.down.listener.OnDownloadListener;
import com.x.down.listener.OnProgressListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XDownload.download("http://file0204.daimg.com/2020/2009/DAimG_2020091600079451KPKM.rar")
                .setUseMultiThread(true)
                .setMultiThreadCount(20)
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(IDownloadRequest request, float progress) {
                        Log.e("noah","onProgress=" + (int) (100 * progress));
                    }
                })
                .setDownloadListener(new OnDownloadListener() {
                    @Override
                    public void onComplete(IDownloadRequest iDownloadRequest) {
                        Log.e("noah","onComplete");
                    }

                    @Override
                    public void onFailure(IDownloadRequest iDownloadRequest) {
                        Log.e("noah","onFailure");
                    }
                }).start();
    }

}
