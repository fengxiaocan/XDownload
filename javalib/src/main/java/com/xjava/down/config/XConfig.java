package com.xjava.down.config;


public final class XConfig{
    private String cacheDir;//默认保存路径
    private String userAgent;//默认UA
    private int sameTimeDownloadCount;//同时下载的任务数
    private int multiThreadCount;//默认下载的多线程数
    private int multiThreadMaxDownloadSize;//默认多线程下载的单线程最大下载文件块大小,默认10MB
    private int multiThreadMinDownloadSize;//默认多线程下载的单线程最小下载文件块大小,默认100kB

    private boolean isUseMultiThread;//是否使用多线程下载
    private boolean isUseBreakpointResume;//是否使用断点续传
    private boolean ignoredProgress;//是否忽略下载的progress回调

    private boolean isUseAutoRetry;//是否使用出错自动重试
    private int autoRetryTimes;//自动重试次数
    private int autoRetryInterval;//自动重试间隔

    private int updateProgressTimes;//更新进度条的间隔
    private boolean isWifiRequired;//是否仅在WiFi情况下下载
    private @DefaultName
    int defaultName;//默认的名称
    private int connectTimeOut;//连接超时

    private XConfig(Builder builder){
        cacheDir=builder.cacheDir;
        userAgent=builder.userAgent;
        sameTimeDownloadCount=builder.sameTimeDownloadCount;
        multiThreadCount=builder.multiThreadCount;
        multiThreadMaxDownloadSize=builder.multiThreadMaxDownloadSize;
        multiThreadMinDownloadSize=builder.multiThreadMinDownloadSize;
        isUseMultiThread=builder.isUseMultiThread;
        isUseBreakpointResume=builder.isUseBreakpointResume;
        ignoredProgress=builder.ignoredProgress;
        isUseAutoRetry=builder.isUseAutoRetry;
        autoRetryTimes=builder.autoRetryTimes;
        autoRetryInterval=builder.autoRetryInterval;
        updateProgressTimes=builder.updateProgressTimes;
        defaultName=builder.defaultName;
        isWifiRequired=builder.isWifiRequired;
        connectTimeOut=builder.connectTimeOut;
    }

    public String getCacheDir(){
        return cacheDir;
    }

    public int getSameTimeDownloadCount(){
        return sameTimeDownloadCount;
    }

    public int getMultiThreadCount(){
        return multiThreadCount;
    }

    public int getMultiThreadMaxDownloadSize(){
        return multiThreadMaxDownloadSize;
    }

    public int getMultiThreadMinDownloadSize(){
        return multiThreadMinDownloadSize;
    }

    public boolean isUseMultiThread(){
        return isUseMultiThread;
    }

    public boolean isUseBreakpointResume(){
        return isUseBreakpointResume;
    }

    public boolean isIgnoredProgress(){
        return ignoredProgress;
    }

    public boolean isUseAutoRetry(){
        return isUseAutoRetry;
    }

    public int getAutoRetryTimes(){
        return autoRetryTimes;
    }

    public int getAutoRetryInterval(){
        return autoRetryInterval;
    }

    public int getUpdateProgressTimes(){
        return updateProgressTimes;
    }

    public boolean isWifiRequired(){
        return isWifiRequired;
    }

    public int getConnectTimeOut(){
        return connectTimeOut;
    }

    public @DefaultName
    int getDefaultName(){
        return defaultName;
    }

    public synchronized String getUserAgent(){
        if(userAgent==null){
            userAgent=getDefaultUserAgent();
        }
        return userAgent;
    }

    public static Builder with(String saveDir){
        return new Builder(saveDir);
    }

    public static String getDefaultUserAgent(){
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0"+
               ".1453.93 Safari/537.36";
    }

    public static final class Builder{
        private String cacheDir;//默认保存路径
        private String userAgent="";//默认UA
        private int sameTimeDownloadCount=2;//同时下载的任务数
        private int multiThreadCount=5;//默认下载的多线程数
        private int multiThreadMaxDownloadSize=5*1024*1024;//默认多线程下载的单线程最大下载文件块大小,默认5MB
        private int multiThreadMinDownloadSize=100*1024;//默认多线程下载的单线程最大下载文件块大小,默认100KB
        private boolean isUseMultiThread=true;//是否使用多线程下载
        private boolean isUseBreakpointResume=true;//是否使用断点续传
        private boolean ignoredProgress=false;//是否忽略下载的progress回调
        private boolean isUseAutoRetry=true;//是否使用出错自动重试
        private int autoRetryTimes=10;//自动重试次数
        private int autoRetryInterval=5;//自动重试间隔
        private int updateProgressTimes=1000;//更新进度条的间隔
        private @DefaultName
        int defaultName=DefaultName.MD5;//默认起名名称
        private boolean isWifiRequired=false;//是否仅在WiFi情况下下载
        private int connectTimeOut=60*1000;//连接超时

        public Builder(String cacheDir){
            this.cacheDir=cacheDir;
        }

        public Builder sameTimeDownloadCount(int sameTimeDownloadCount){
            this.sameTimeDownloadCount=sameTimeDownloadCount;
            return this;
        }

        public Builder userAgent(String userAgent){
            this.userAgent=userAgent;
            return this;
        }

        public Builder multiThreadCount(int multiThreadCount){
            this.multiThreadCount=multiThreadCount;
            return this;
        }

        public Builder multiThreadMaxSize(int multiThreadMaxSize){
            this.multiThreadMaxDownloadSize=multiThreadMaxSize;
            return this;
        }

        public Builder multiThreadMinSize(int multiThreadMinSize){
            this.multiThreadMinDownloadSize=multiThreadMinSize;
            return this;
        }

        public Builder isUseMultiThread(boolean isUseMultiThread){
            this.isUseMultiThread=isUseMultiThread;
            return this;
        }

        public Builder isUseBreakpointResume(boolean isUseBreakpointResume){
            this.isUseBreakpointResume=isUseBreakpointResume;
            return this;
        }

        public Builder ignoredProgress(boolean ignoredProgress){
            this.ignoredProgress=ignoredProgress;
            return this;
        }

        public Builder isUseAutoRetry(boolean isUseAutoRetry){
            this.isUseAutoRetry=isUseAutoRetry;
            return this;
        }

        public Builder autoRetryTimes(int autoRetryTimes){
            this.autoRetryTimes=autoRetryTimes;
            return this;
        }

        public Builder autoRetryInterval(int autoRetryInterval){
            this.autoRetryInterval=autoRetryInterval;
            return this;
        }

        public Builder updateProgressTimes(int updateProgressTimes){
            this.updateProgressTimes=updateProgressTimes;
            return this;
        }

        public Builder isWifiRequired(boolean isWifiRequired){
            this.isWifiRequired=isWifiRequired;
            return this;
        }

        public Builder connectTimeOut(int connectTimeOut){
            this.connectTimeOut=connectTimeOut;
            return this;
        }

        public Builder defaultName(@DefaultName int defaultName){
            this.defaultName=defaultName;
            return this;
        }

        public XConfig build(){
            return new XConfig(this);
        }
    }

    public @interface DefaultName{
        int MD5=0;//MD5值
        int TIME=1;//时间
        int ORIGINAL=2;//原名称
    }
}
