package com.x.test;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.x.down.AndroidDownload;
import com.xjava.down.base.IRequest;
import com.xjava.down.data.Response;
import com.xjava.down.listener.OnResponseListener;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidDownload.init(this);

        AndroidDownload.request("https://")
                       .setOnResponseListener(new OnResponseListener(){
                           @Override
                           public void onResponse(IRequest request,Response response){
                               if(response.isSuccess()){
                                   Log.e("noah",response.result());
                               } else{
                                   Log.e("noah",response.error());
                               }
                           }

                           @Override
                           public void onError(IRequest request,Exception exception){
                               Log.e("noah","onError="+exception.getMessage());
                           }
                       })
                       .start();

    }

}
