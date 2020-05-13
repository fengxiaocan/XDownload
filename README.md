# XDownload

使用纯Java写的多线程下载器,可以在各平台上使用,在Android中有额外扩展

使用:

Add it in your root build.gradle at the end of repositories:

	  allprojects {
	  	repositories {
	  		maven { url 'https://jitpack.io' }
	  	}
	  }
  
Step 2. Add the dependency

	  dependencies {
	          implementation 'com.github.fengxiaocan:XDownload:v0.1.3'
	  }
  
jar包下载:[Downloader.jar](./javalib/javalib.jar)

JavaGUI各平台可视化操作界面[DownloaderGUI.jar](./DownloaderGUI.jar)

Java中使用方法:
	
	//初始化配置
	XDownload.get()
		.setMaxThreadCount(30);//构建预下载请求任务队列的最大线程数,非下载数
		.config(XConfig
                .with(System.getProperty("user.dir"))
                .defaultName(XConfig.DefaultName.MD5)//默认起名名称,除非自己声明保存文件名
                // MD5:默认以下载URL MD5起名  TIME:默认以时间戳来命名  ORIGINAL:默认根据下载链接的名称来命名
                .connectTimeOut(60*1000)//连接超时
                .isUseAutoRetry(true)//是否使用出错自动重试
                .autoRetryTimes(500)//自动重试次数
                .autoRetryInterval(5000)//自动重试间隔毫秒
                .userAgent("UA")//默认UA
                .isUseMultiThread(true)//是否使用多线程下载
                .isUseBreakpointResume(true)//是否使用断点续传
                .sameTimeDownloadCount(2)//同时下载的任务数
                .multiThreadCount(5)//默认下载单个文件的多线程数
                .multiThreadMaxSize(5*1024*1024)//默认多线程下载的单线程最大下载文件块大小,默认5MB
                .multiThreadMinSize(100*1024)//默认多线程下载的单线程最大下载文件块大小,默认100KB
                .updateProgressTimes(1000)//更新进度条的间隔
                .updateSpeedTimes(1000)//更新下载速度的间隔
                .isWifiRequired(false)//是否仅在WiFi情况下下载,暂不可用
                .ignoredSpeed(false)//是否忽略下载的速度回调
                .ignoredProgress(false)//是否忽略下载的progress回调
                .build());
		
	//发起请求
	String tag = XDownload.download(url)
               .setTag(tag)//设置tag,不设置
               .setSaveFile(saveFile)//设置保存文件路径
               .setCacheDir(cacheDir)//设置下载临时文件的保存地址
               .setDownloadListener()//设置下载完成或失败的监听
               .setConnectListener()//设置连接请求的监听
               .setOnProgressListener()//设置下载进度监听
               .setOnSpeedListener()//设置下载速度监听
               .delect()//删除之前下载的文件
               .addParams(参数名,参数值)//在URL后面添加参数
               .addHeader(请求头名,请求头值)//添加请求头
               .setUserAgent(ua)//UA
               .scheduleOn(Schedulers)//需要异步回调的在这里处理,实现Schedulers
               .start();
	       
        //取消下载=暂停下载,恢复下载再走一遍请求逻辑
        XDownload.get().cancleDownload(tag);
	
	    //OnSpeedListener:下载速度监听器
	    //request:下载任务请求 speed:下载速度 time:距离上次回调的时间间隔
	    void onSpeed(IDownloadRequest request,int speed,int time)

	    //OnProgressListener:下载进度监听器
	    void onProgress(IDownloadRequest request,float progress);
	
	    //OnDownloadConnectListener:请求连接监听器
	    //预备下载
	    void onPending(IDownloadRequest request);
	    //开始下载
    	void onStart(IDownloadRequest request);
	    //连接上请求
    	void onConnecting(IDownloadRequest request);
	    //请求失败,code为返回码,error为服务器返回错误信息
    	void onRequestError(IDownloadRequest request,int code,String error);
	    //下载取消,多线程下载会有多次回调
    	void onCancel(IDownloadRequest request);
	    //下载出错,正在重试
    	void onRetry(IDownloadRequest request);
	
	    //OnDownloadListener:下载结果监听器
	    //下载完成
	    void onComplete(IDownloadRequest request);
	    //下载失败
    	void onFailure(IDownloadRequest request);


Android中使用方式(其他方法跟Java一致):

	//安卓初始化必须
	AndroidDownload.init(context.getApplicationContext());
	
	AndroidDownload.download(url)
		.scheduleOn(AndroidSchedulers.mainThread())//异步回调
		.start();
	
