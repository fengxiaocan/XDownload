package com.x.down;

import com.x.down.config.XConfig;

public class XDownload{
    private static XDownload instance;
    private XConfig xConfig;

    public static synchronized XDownload getInstance(){
        if(instance == null){
            instance = new XDownload();
        }
        return instance;
    }

    public XDownload setConfig(XConfig xConfig){
        this.xConfig = xConfig;
        return this;
    }

    public synchronized XConfig getConfig(){
        if(xConfig == null){
            xConfig = XConfig.defaultConfig();
        }
        return xConfig;
    }
}
