package com.x.down.config;

public final class XConfig{
    public static final XConfig defaultConfig(){
        return new XConfig(new Builder());
    }

    //同时下载任务数
    private int coreTaskSize;
    //下载线程数
    private int threadSize;
    //保持活跃的线程数
    private int activeThreadSize;
    //保持活跃的时间
    private int keepAliveTime;
    //重试次数
    private int retryTime;
    //出错重试
    private boolean errorRetry;
    //下载速度限制
    private int downSpeed;
    //上传限制速度
    private int uploadSpeed;
    //读取超时时间
    private int readTimeout;
    //请求超时时间
    private int requestTimeout;
    //预先下载
    private boolean advance;
    //使用代理
    private boolean useProxy;
    //预先创建空文件
    private boolean reserveEmptyFile;
    //仅WiFi状态下下载
    private boolean justWifi;
    //可重复下载
    private boolean repeatable;
    //是否使用通知回调
    private boolean useReceiver;
    //限制速度
    private boolean limitSpeed;
    //是否使用后台服务
    private boolean useService;
    //是否启用定时
    private boolean useTiming;

    private XConfig(Builder builder){
        coreTaskSize = builder.coreTaskSize;
        threadSize = builder.threadSize;
        activeThreadSize = builder.activeThreadSize;
        keepAliveTime = builder.keepAliveTime;
        retryTime = builder.retryTime;
        errorRetry = builder.errorRetry;
        downSpeed = builder.downSpeed;
        uploadSpeed = builder.uploadSpeed;
        readTimeout = builder.readTimeout;
        requestTimeout = builder.requestTimeout;
        advance = builder.advance;
        useProxy = builder.useProxy;
        reserveEmptyFile = builder.reserveEmptyFile;
        justWifi = builder.justWifi;
        repeatable = builder.repeatable;
        useReceiver = builder.useReceiver;
        limitSpeed = builder.limitSpeed;
        useService = builder.useService;
        useTiming = builder.useTiming;
    }

    public int getCoreTaskSize(){
        return coreTaskSize;
    }

    public int getThreadSize(){
        return threadSize;
    }

    public int getActiveThreadSize(){
        return activeThreadSize;
    }

    public int getKeepAliveTime(){
        return keepAliveTime;
    }

    public int getRetryTime(){
        return retryTime;
    }

    public boolean isErrorRetry(){
        return errorRetry;
    }

    public int getDownSpeed(){
        return downSpeed;
    }

    public int getUploadSpeed(){
        return uploadSpeed;
    }

    public int getReadTimeout(){
        return readTimeout;
    }

    public int getRequestTimeout(){
        return requestTimeout;
    }

    public boolean isAdvance(){
        return advance;
    }

    public boolean isUseProxy(){
        return useProxy;
    }

    public boolean isReserveEmptyFile(){
        return reserveEmptyFile;
    }

    public boolean isJustWifi(){
        return justWifi;
    }

    public boolean isRepeatable(){
        return repeatable;
    }

    public boolean isUseReceiver(){
        return useReceiver;
    }

    public boolean isLimitSpeed(){
        return limitSpeed;
    }

    public boolean isUseService(){
        return useService;
    }

    public boolean isUseTiming(){
        return useTiming;
    }

    public static final Builder builder(){
        return new Builder();
    }

    public static final class Builder{
        private int coreTaskSize = 3;
        private int threadSize = 5;
        private int activeThreadSize = 2;
        private int keepAliveTime = 60 * 1000;
        private int retryTime = 20;
        private boolean errorRetry = true;
        private boolean limitSpeed = false;
        private int downSpeed = Integer.MAX_VALUE;
        private int uploadSpeed = Integer.MAX_VALUE;
        private int readTimeout = 10 * 1000;
        private int requestTimeout = 10 * 1000;
        private boolean advance = true;
        private boolean useProxy = false;
        private boolean reserveEmptyFile = true;
        private boolean justWifi = false;
        private boolean repeatable = false;
        private boolean useReceiver = false;
        private boolean useService = false;
        private boolean useTiming = false;

        public Builder(){
        }

        public Builder coreTaskSize(int coreTaskSize){
            this.coreTaskSize = coreTaskSize;
            return this;
        }

        public Builder threadSize(int threadSize){
            this.threadSize = threadSize;
            return this;
        }

        public Builder activeThreadSize(int activeThreadSize){
            this.activeThreadSize = activeThreadSize;
            return this;
        }

        public Builder keepAliveTime(int keepAliveTime){
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder retryTime(int retryTime){
            this.retryTime = retryTime;
            return this;
        }

        public Builder errorRetry(boolean errorRetry){
            this.errorRetry = errorRetry;
            return this;
        }

        public Builder downSpeed(int downSpeed){
            this.downSpeed = downSpeed;
            return this;
        }

        public Builder uploadSpeed(int uploadSpeed){
            this.uploadSpeed = uploadSpeed;
            return this;
        }

        public Builder readTimeout(int readTimeout){
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder requestTimeout(int requestTimeout){
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder advance(boolean advance){
            this.advance = advance;
            return this;
        }

        public Builder useProxy(boolean useProxy){
            this.useProxy = useProxy;
            return this;
        }

        public Builder reserveEmptyFile(boolean reserveEmptyFile){
            this.reserveEmptyFile = reserveEmptyFile;
            return this;
        }

        public Builder justWifi(boolean justWifi){
            this.justWifi = justWifi;
            return this;
        }

        public Builder repeatable(boolean repeatable){
            this.repeatable = repeatable;
            return this;
        }

        public Builder useReceiver(boolean use){
            this.useReceiver = use;
            return this;
        }

        public Builder useService(boolean useService){
            this.useService = useService;
            return this;
        }

        public Builder limitSpeed(boolean limitSpeed){
            this.limitSpeed = limitSpeed;
            return this;
        }

        public Builder useTiming(boolean useTiming){
            this.useTiming = useTiming;
            return this;
        }

        public XConfig build(){
            return new XConfig(this);
        }
    }
}
