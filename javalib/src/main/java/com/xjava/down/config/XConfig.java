package com.xjava.down.config;


public class XConfig implements IConfig{
    private String cacheDir;//默认保存路径
    private String userAgent="";//默认UA
    private int sameTimeDownloadCount=2;//同时下载的任务数
    private int multiThreadCount=5;//默认下载的多线程数
    private int bufferedSize=10*1024;//写文件buff大小，该数值大小不能小于2048，数值变小，下载速度会变慢,默认10kB
    private int multiThreadMaxDownloadSize=5*1024*1024;//默认多线程下载的单线程最大下载文件块大小,默认5MB
    private int multiThreadMinDownloadSize=100*1024;//默认多线程下载的单线程最大下载文件块大小,默认100KB
    private boolean isUseMultiThread=true;//是否使用多线程下载
    private boolean isUseBreakpointResume=true;//是否使用断点续传
    private boolean ignoredProgress=false;//是否忽略下载的progress回调
    private boolean ignoredSpeed=false;//是否忽略下载的速度回调
    private boolean isUseAutoRetry=true;//是否使用出错自动重试
    private int autoRetryTimes=10;//自动重试次数
    private int autoRetryInterval=5;//自动重试间隔
    private int updateProgressTimes=1000;//更新进度条的间隔
    private int updateSpeedTimes=1000;//更新速度的间隔
    private @IConfig.DefaultName
    int defaultName=IConfig.DefaultName.MD5;//默认起名名称
    private boolean permitAllSslCertificate=true;//是否允许所有的SSL证书运行,即可以下载所有的https的连接
    private int connectTimeOut=30*1000;//连接超时单位为毫秒，默认30秒，该时间不能少于5秒
    private int iOTimeOut=20*1000;//设置IO流读取时间，单位为毫秒，默认20秒，该时间不能少于5秒

    public XConfig(String cacheDir){
        this.cacheDir=cacheDir;
    }

    @Override
    public XConfig cacheDir(String cacheDir){
        this.cacheDir=cacheDir;
        return this;
    }

    @Override
    public XConfig sameTimeDownloadCount(int sameTimeDownloadCount){
        this.sameTimeDownloadCount=sameTimeDownloadCount;
        return this;
    }

    @Override
    public XConfig userAgent(String userAgent){
        this.userAgent=userAgent;
        return this;
    }

    @Override
    public XConfig bufferedSize(int buffSize){
        this.bufferedSize=buffSize;
        return this;
    }

    @Override
    public XConfig multiThreadCount(int multiThreadCount){
        this.multiThreadCount=multiThreadCount;
        return this;
    }

    @Override
    public XConfig multiThreadMaxSize(int multiThreadMaxSize){
        this.multiThreadMaxDownloadSize=Math.max(multiThreadMaxSize,MULTI_THREAD_MAX_DOWNLOAD_SIZE);
        return this;
    }

    @Override
    public XConfig multiThreadMinSize(int multiThreadMinSize){
        this.multiThreadMinDownloadSize=Math.max(multiThreadMinSize,MULTI_THREAD_MIN_DOWNLOAD_SIZE);
        return this;
    }

    @Override
    public XConfig isUseMultiThread(boolean isUseMultiThread){
        this.isUseMultiThread=isUseMultiThread;
        return this;
    }

    @Override
    public XConfig isUseBreakpointResume(boolean isUseBreakpointResume){
        this.isUseBreakpointResume=isUseBreakpointResume;
        return this;
    }

    @Override
    public XConfig ignoredProgress(boolean ignoredProgress){
        this.ignoredProgress=ignoredProgress;
        return this;
    }

    @Override
    public XConfig ignoredSpeed(boolean ignoredSpeed){
        this.ignoredSpeed=ignoredSpeed;
        return this;
    }

    @Override
    public XConfig isUseAutoRetry(boolean isUseAutoRetry){
        this.isUseAutoRetry=isUseAutoRetry;
        return this;
    }

    @Override
    public XConfig autoRetryTimes(int autoRetryTimes){
        this.autoRetryTimes=autoRetryTimes;
        return this;
    }

    @Override
    public XConfig autoRetryInterval(int autoRetryInterval){
        this.autoRetryInterval=autoRetryInterval;
        return this;
    }

    @Override
    public XConfig updateProgressTimes(int updateProgressTimes){
        this.updateProgressTimes=updateProgressTimes;
        return this;
    }

    @Override
    public XConfig updateSpeedTimes(int updateSpeedTimes){
        this.updateSpeedTimes=updateSpeedTimes;
        return this;
    }

    @Override
    public XConfig permitAllSslCertificate(boolean permitAllSslCertificate){
        this.permitAllSslCertificate=permitAllSslCertificate;
        return this;
    }

    @Override
    public XConfig connectTimeOut(int connectTimeOut){
        this.connectTimeOut=connectTimeOut;
        return this;
    }

    @Override
    public IConfig iOTimeOut(int iOTimeOut){
        this.iOTimeOut=iOTimeOut;
        return this;
    }

    @Override
    public XConfig defaultName(@IConfig.DefaultName int defaultName){
        this.defaultName=defaultName;
        return this;
    }


    public String getCacheDir(){
        return cacheDir;
    }

    public int getBufferedSize(){
        return bufferedSize;
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

    public boolean isPermitAllSslCertificate(){
        return permitAllSslCertificate;
    }

    public int getConnectTimeOut(){
        return connectTimeOut;
    }

    public int getiOTimeOut(){
        return iOTimeOut;
    }

    public boolean isIgnoredSpeed(){
        return ignoredSpeed;
    }

    public int getUpdateSpeedTimes(){
        return updateSpeedTimes;
    }

    public @IConfig.DefaultName
    int getDefaultName(){
        return defaultName;
    }

    public synchronized String getUserAgent(){
        if(userAgent==null){
            userAgent=getDefaultUserAgent();
        }
        return userAgent;
    }

    public static String getDefaultUserAgent(){
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0"+
               ".1453.93 Safari/537.36";
    }

}
