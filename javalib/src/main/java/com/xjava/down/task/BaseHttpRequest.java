package com.xjava.down.task;

import com.xjava.down.data.Headers;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

abstract class BaseHttpRequest implements Runnable{
    protected final AutoRetryRecorder autoRetryRecorder;
    protected volatile boolean isCancel = false;

    public BaseHttpRequest(AutoRetryRecorder autoRetryRecorder){
        this.autoRetryRecorder=autoRetryRecorder;
    }

    protected final void runTask(){
        try{
            if(isCancel){
                onCancel();
                return;
            }
            httpRequest();
        } catch(Exception e){
            if(isCancel){
                onCancel();
            }else{
                if(autoRetryRecorder.isCanRetry()){
                    //回调重试
                    onRetry();
                    //决定是否延迟执行重试
                    autoRetryRecorder.sleep();
                    //自动重试下载
                    runTask();
                } else{
                    onError(e);
                }
            }
        }
    }

    protected final void retryToRun(){
        if(isCancel){
            onCancel();
        }else{
            if(autoRetryRecorder.isCanRetry()){
                //回调重试
                onRetry();
                //决定是否延迟执行重试
                autoRetryRecorder.sleep();
                //自动重试下载
                runTask();
            }
        }
    }

    protected abstract void httpRequest() throws Exception;

    protected abstract void onRetry();

    protected abstract void onError(Exception e);

    protected abstract void onCancel();

    @Override
    public void run(){
        runTask();
    }

    protected boolean isSuccess(int responseCode){
        return responseCode >= 200&&responseCode<400;
    }


    protected Headers getHeaders(HttpURLConnection http){
        Headers headers=new Headers();
        Map<String,List<String>> map=http.getHeaderFields();
        for(String key: map.keySet()){
            headers.addHeaders(key,map.get(key));
        }
        return headers;
    }

    protected String readStringStream(InputStream is) throws IOException{
        BufferedReader reader=null;
        try{
            reader=new BufferedReader(new InputStreamReader(is));

            StringBuilder builder=new StringBuilder();
            String temp;
            while((temp=reader.readLine())!=null){
                builder.append(temp);
            }
            return builder.toString();
        } finally{
            XDownUtils.closeIo(is);
            XDownUtils.closeIo(reader);
        }
    }
}
