package com.x.down;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

 class MultiThreadService extends IntentService{

    public MultiThreadService(){
        super("MultiThreadService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        String url=intent.getStringExtra("baseUrl");
        String path=intent.getStringExtra("file");
        HttpURLConnection urlConnection=null;
        try{
            // 统一资源
            URL _URL=new URL(url);
            // 连接类的父类，抽象类
            // http的连接类
            HttpURLConnection httpURLConnection=(HttpURLConnection)_URL.openConnection();
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset","UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();
            //设置超时时间
            httpURLConnection.setConnectTimeout(60  * 1000);
            //设置读取超时时间
            httpURLConnection.setReadTimeout(60  * 1000);

            BufferedInputStream bin=new BufferedInputStream(httpURLConnection.getInputStream());

            File file=new File(path);
            File parentFile=file.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            OutputStream out=new FileOutputStream(file);
            int size=0;
            byte[] buf=new byte[1024];
            while((size=bin.read(buf))!=-1){
                out.write(buf,0,size);
            }
            bin.close();
            out.close();
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }
    }
}
