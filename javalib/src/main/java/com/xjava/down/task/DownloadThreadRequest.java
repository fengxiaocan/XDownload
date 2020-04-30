package com.xjava.down.task;


import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.MultiDisposer;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.made.Block;
import com.xjava.down.ExecutorGather;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.concurrent.ThreadPoolExecutor;

public final class DownloadThreadRequest extends HttpRequestTask{
    private static final String BLOCK_FILE_NAME="block";
    protected ThreadPoolExecutor threadPoolExecutor;
    protected final OnDownloadListener downloadListener;

    public DownloadThreadRequest(XDownloadRequest request,OnDownloadListener downloadListener){
        super(request,);
        this.downloadListener=downloadListener;
    }

    @Override
    protected void httpRequest() throws Exception{
        XDownloadRequest request=(XDownloadRequest)this.httpRequest;
        HttpURLConnection http=request.buildConnect();
        //预备中
        listenerDisposer.onConnecting(this);

        http.getResponseCode();

        long contentLength=0;
        try{
            contentLength=http.getContentLengthLong();
        } catch(Exception e){
            contentLength=http.getContentLength();
        }
        int code=http.getResponseCode();

//        Headers headers=getHeaders(http);


        File file=XDownUtils.getSaveFile(request);
        if(file.exists()){
            if(file.length()==contentLength){
                if(downloadListener!=null){
                    downloadListener.onProgress(1);
                    downloadListener.onDownloadComplete();
                }
                http.disconnect();
                return;
            } else{
                file.delete();
            }
        }

        if(contentLength>0&&request.isUseMultiThread()){
            multiThreadRun(contentLength);
        } else{
            //独立下载
            SingleDownloadThreadTask threadTask=new SingleDownloadThreadTask(request,downloadListener,contentLength);
            if(!threadTask.checkComplete()){
                ExecutorGather.executorQueue().submit(threadTask);
            }
        }
    }

    /**
     * 多线程下载
     *
     * @param contentLength
     */
    private void multiThreadRun(final long contentLength){
        XDownloadRequest request=(XDownloadRequest)this.httpRequest;
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
        File cacheDir=XDownUtils.getTempCacheDir(request);
        File blockFile=new File(cacheDir,BLOCK_FILE_NAME);
        //是否需要删除之前的临时文件
        boolean isDelectTemp=false;
        //每一块的长度
        final long blockLength;
        //需要的执行任务数量
        final int threadCount;
        Block block;
        if(blockFile.exists()){
            block=XDownUtils.readObject(blockFile);
            if(block!=null){
                if(contentLength!=block.getContentLength()){
                    isDelectTemp=true;
                    block=createBlock(blockFile,contentLength);
                }
            } else{
                block=createBlock(blockFile,contentLength);
            }
        } else{
            block=createBlock(blockFile,contentLength);
        }
        blockLength=block.getBlockLength();
        threadCount=block.getThreadCount();
        if(!request.isUseBreakpointResume()){
            isDelectTemp=true;
        }
        threadPoolExecutor=ExecutorGather.newSubtaskQueue(request.getMultiThreadCount());

        final MultiDisposer disposer=new MultiDisposer(request,threadCount,downloadListener);

        long start=0, end=-1;
        for(int index=0;index<threadCount;index++){
            start=end+1;
            if(index==threadCount-1){
                end=contentLength;
            } else{
                end=end+blockLength;
            }
            //保存的临时文件
            File file=new File(cacheDir,request.getIdentifier()+"_temp_"+index);
            if(isDelectTemp&&file.exists()){
                //需要删除之前的临时缓存文件
                file.delete();
            }
            MultiDownloadThreadTask task=new MultiDownloadThreadTask(request,
                                                                     file,
                                                                     autoRetryRecorder,
                                                                     index,
                                                                     contentLength,
                                                                     start,
                                                                     end,
                                                                     disposer);
            disposer.addTask(index,task);
            threadPoolExecutor.execute(task);
        }
        disposer.onProgress(contentLength,0);
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
        XDownloadRequest request=(XDownloadRequest)this.httpRequest;
        final long blockLength;
        //使用的线程数
        final int threadCount;

        final int configThreadCount=request.getMultiThreadCount();
        final int threadMaxSize=request.getMultiThreadMaxDownloadSize();
        final int threadMinSize=request.getMultiThreadMinDownloadSize();
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
}
