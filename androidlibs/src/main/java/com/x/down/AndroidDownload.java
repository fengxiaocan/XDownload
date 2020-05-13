package com.x.down;

import android.content.Context;
import android.os.Build;

import com.xjava.down.XDownload;
import com.xjava.down.config.XConfig;
import com.xjava.down.core.HttpConnect;
import com.xjava.down.core.HttpDownload;

public final class AndroidDownload{

    public static XConfig init(Context context){
        XConfig config=XConfig.with(context.getExternalCacheDir().getAbsolutePath())
                              .userAgent(getDefaultUserAgent())
                              .build();
        XDownload.get().config(config);
        return config;
    }

    public static void init(XConfig xConfig){
        XDownload.get().config(xConfig);
    }

    public static HttpConnect request(String baseUrl){
        return XDownload.request(baseUrl);
    }

    public static HttpDownload download(String baseUrl){
        return XDownload.download(baseUrl);
    }

    public static String getDefaultUserAgent(){
        StringBuilder result=new StringBuilder("Mozilla/5.0 (");
        result.append(System.getProperty("os.name"));
        result.append("; Android ");
        result.append(Build.VERSION.RELEASE);
        result.append("; ");
        result.append(Build.MANUFACTURER);
        result.append(" ");

        if("REL".equals(Build.VERSION.CODENAME)){
            String model=Build.MODEL;
            if(model.length()>0){
                result.append(model);
            }
        }
        result.append("; ");
        String id=Build.ID;
        if(id.length()>0){
            result.append("Build/");
            result.append(id);
        }
        result.append(")");

        return result.toString();
    }

    /**
     * 取消请求
     *
     * @param tag
     * @return
     */
    public static boolean cancleRequest(String tag){
        return XDownload.get().cancleRequest(tag);
    }

    /**
     * 取消下载
     *
     * @param tag
     * @return
     */
    public boolean cancleDownload(String tag){
        return XDownload.get().cancleDownload(tag);
    }

    public void setMaxThreadCount(int count){
         XDownload.get().setMaxThreadCount(count);
    }

}
