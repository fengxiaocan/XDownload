package com.xjava.down.task;


import com.xjava.down.ExecutorGather;
import com.xjava.down.XDownload;
import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.data.Headers;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.impl.MultiDisposer;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.made.Block;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

final class DownloadThreadRequest extends BaseHttpRequest implements IDownloadRequest, IConnectRequest{
    private static final String BLOCK_FILE_NAME="block";

    protected ThreadPoolExecutor threadPoolExecutor;
    protected final XDownloadRequest httpRequest;
    protected final DownloadListenerDisposer listenerDisposer;
    protected volatile Future taskFuture;
    protected volatile long sContentLength;

    public DownloadThreadRequest(
            XDownloadRequest request,
            OnDownloadConnectListener onConnectListeners,
            OnDownloadListener downloadListeners,
            OnProgressListener onProgressListener,
            OnSpeedListener onSpeedListener)
    {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()));
        this.httpRequest=request;
        this.listenerDisposer=new DownloadListenerDisposer(request.getSchedulers(),
                                                           onConnectListeners,
                                                           downloadListeners,
                                                           onProgressListener,
                                                           onSpeedListener);
    }

    public final void setTaskFuture(Future taskFuture){
        this.taskFuture=taskFuture;
    }

    @Override
    protected void httpRequest() throws Exception{
        HttpURLConnection http=httpRequest.buildConnect();
        //优先获取文件长度再回调
        sContentLength=XDownUtils.getContentLength(http);
        //连接中
        listenerDisposer.onConnecting(this);

        if(sContentLength<=0){
            //长度获取不到的时候重新连接 获取不到长度则要求http请求不要gzip压缩
            http.setRequestProperty("Accept-Encoding","identity");
            http.connect();
            sContentLength=XDownUtils.getContentLength(http);
            //连接中
            listenerDisposer.onConnecting(this);
        }

        int code=http.getResponseCode();

        if(code<200||code >= 400){
            //获取错误信息
            String stream=readStringStream(http.getErrorStream());
            listenerDisposer.onRequestError(this,code,stream);
            //断开请求
            XDownUtils.disconnectHttp(http);
            //重试
            retryToRun();
        } else{
            //先断开请求
            XDownUtils.disconnectHttp(http);

            if(sContentLength>0){
                File file=XDownUtils.getSaveFile(httpRequest);
                if(file.exists()){
                    if(file.length()==sContentLength){
                        listenerDisposer.onComplete(this);
                        return;
                    } else{
                        file.delete();
                    }
                }
            }

            if(sContentLength>0&&httpRequest.isUseMultiThread()){
                multiThreadRun(sContentLength);
            } else{
                //独立下载
                SingleDownloadThreadTask threadTask=new SingleDownloadThreadTask(httpRequest,
                                                                                 listenerDisposer,
                                                                                 sContentLength);
                if(!threadTask.checkComplete()){
                    Future<?> future=ExecutorGather.executorQueue().submit(threadTask);
                    threadTask.setTaskFuture(future);
                    XDownload.get().addDownload(httpRequest.getTag(),threadTask);
                }
            }
        }
    }


    @Override
    protected void onRetry(){
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e){
        listenerDisposer.onFailure(this);
    }

    @Override
    protected void onCancel(){
        listenerDisposer.onCancel(this);
    }

    /**
     * 多线程下载
     *
     * @param contentLength
     */
    private void multiThreadRun(final long contentLength){
        if(threadPoolExecutor!=null){
            //isShutDown：当调用shutdown()或shutdownNow()方法后返回为true。 
            //isTerminated：当调用shutdown()方法后，并且所有提交的任务完成后返回为true;
            //isTerminated：当调用shutdownNow()方法后，成功停止后返回为true;
            if(threadPoolExecutor.isShutdown()){
                //线程已经开始
                return;
            }
            if(!threadPoolExecutor.isTerminated()){
                //线程已开始并且还没完成
                return;
            }
        }
        //获取上次配置,决定断点下载不出错
        File cacheDir=XDownUtils.getTempCacheDir(httpRequest);

        File blockFile=new File(cacheDir,BLOCK_FILE_NAME);
        //是否需要删除之前的临时文件
        final boolean isDelectTemp=!httpRequest.isUseBreakpointResume();
        if(isDelectTemp){
            //需要删除之前的临时缓存文件
            XDownUtils.delectDir(cacheDir);
        }

        //每一块的长度
        final long blockLength;
        //需要的执行任务数量
        final int threadCount;
        Block block;
        if(blockFile.exists()){
            block=XDownUtils.readObject(blockFile);
            if(block==null){
                block=createBlock(blockFile,contentLength);
            }
        } else{
            block=createBlock(blockFile,contentLength);
        }
        blockLength=block.getBlockLength();
        threadCount=block.getThreadCount();

        threadPoolExecutor=ExecutorGather.newSubtaskQueue(httpRequest.getMultiThreadCount());

        final MultiDisposer disposer=new MultiDisposer(httpRequest,threadCount,listenerDisposer);

        synchronized(Object.class){
            long start=0, end=-1;

            for(int index=0;index<threadCount;index++){
                start=end+1;
                final long fileLength;
                if(index==threadCount-1){
                    end=contentLength;
                    fileLength=contentLength-start;
                } else{
                    end=start+blockLength;
                    fileLength=blockLength+1;
                }

                //保存的临时文件
                File file=new File(cacheDir,httpRequest.getIdentifier()+"_temp_"+index);

                MultiDownloadThreadTask task=new MultiDownloadThreadTask(httpRequest,
                                                                         file,
                                                                         autoRetryRecorder,
                                                                         index,
                                                                         contentLength,
                                                                         fileLength,
                                                                         start,
                                                                         end,
                                                                         disposer);
                disposer.addTask(index,task);
                Future<?> submit=threadPoolExecutor.submit(task);
                XDownload.get().addDownload(httpRequest.getTag(),task);
                task.setTaskFuture(submit);
            }
        }
        disposer.onProgress(this,contentLength,0);
        threadPoolExecutor.shutdown();
    }

    /**
     * 创建块
     *
     * @param blockFile
     * @param contentLength
     * @return
     */
    protected Block createBlock(File blockFile,final long contentLength){
        final long blockLength;
        //使用的线程数
        final int threadCount;

        final int configThreadCount=httpRequest.getMultiThreadCount();
        final int threadMaxSize=httpRequest.getMultiThreadMaxDownloadSize();
        final int threadMinSize=httpRequest.getMultiThreadMinDownloadSize();
        //最大的数量
        final long maxLength=configThreadCount*threadMaxSize;
        final long minLength=configThreadCount*threadMinSize;
        //智能计算执行任务的数量
        if(contentLength>maxLength){
            //如果文件过大,设定的线程不足够
            blockLength=threadMaxSize;
            threadCount=(int)(contentLength/blockLength);
        } else{
            //如果文件过小,设定的线程有浪费,控制线程的创建少于设定的线程
            if(contentLength<minLength){
                blockLength=minLength;
                threadCount=(int)(contentLength/blockLength);
            } else{
                //正常的线程
                blockLength=contentLength/configThreadCount;
                threadCount=configThreadCount;
            }
        }
        Block block=new Block(contentLength,blockLength,threadCount);
        XDownUtils.writeObject(blockFile,block);
        return block;
    }

    @Override
    public String tag(){
        return httpRequest.getTag();
    }

    @Override
    public String url(){
        return httpRequest.getConnectUrl();
    }

    @Override
    public boolean cancel(){
        isCancel=true;
        if(taskFuture!=null){
            return taskFuture.cancel(true);
        }
        return false;
    }

    @Override
    public int retryCount(){
        return autoRetryRecorder.getRetryCount();
    }

    @Override
    public XDownloadRequest request(){
        return httpRequest;
    }

    @Override
    public String getFilePath(){
        return XDownUtils.getSaveFile(httpRequest).getAbsolutePath();
    }

    @Override
    public long getTotalLength(){
        return sContentLength;
    }

    @Override
    public long getSofarLength(){
        return 0;
    }
}
