package com.xjava.down.task;

import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

abstract class BaseHttpRequest implements Runnable{
    protected final AutoRetryRecorder autoRetryRecorder;

    public BaseHttpRequest(AutoRetryRecorder autoRetryRecorder){
        this.autoRetryRecorder=autoRetryRecorder;
    }

    protected final void runTask(){
        try{
            httpRequest();
        } catch(Exception e){
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

    protected final void retryToRun(){
        if(autoRetryRecorder.isCanRetry()){
            //回调重试
            onRetry();
            //决定是否延迟执行重试
            autoRetryRecorder.sleep();
            //自动重试下载
            runTask();
        }
    }

    protected abstract void httpRequest() throws Exception;

    protected abstract void onRetry();

    protected abstract void onError(Exception e);

    @Override
    public void run(){
        runTask();
    }

    protected boolean isSuccess(int responseCode){
        return responseCode >= 200&&responseCode<400;
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
