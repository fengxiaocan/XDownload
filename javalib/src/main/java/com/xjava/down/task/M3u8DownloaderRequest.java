package com.xjava.down.task;


import com.xjava.down.ExecutorGather;
import com.xjava.down.XDownload;
import com.xjava.down.base.IConnectRequest;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.core.XDownloadRequest;
import com.xjava.down.impl.DownloadListenerDisposer;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnSpeedListener;
import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.made.M3u8Block;
import com.xjava.down.made.M3u8Memory;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

final class M3u8DownloaderRequest extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest{

    protected ThreadPoolExecutor threadPoolExecutor;
    protected final XDownloadRequest httpRequest;
    protected final DownloadListenerDisposer listenerDisposer;
    protected volatile Future taskFuture;

    public M3u8DownloaderRequest(
            XDownloadRequest request,
            OnDownloadConnectListener onConnectListeners,
            OnDownloadListener downloadListeners,
            OnProgressListener onProgressListener,
            OnSpeedListener onSpeedListener)
    {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                                    request.getAutoRetryTimes(),
                                    request.getAutoRetryInterval()),request.getBufferedSize());
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
        //获取之前的下载信息
        M3u8Memory info=XDownload.get().getMemoryHandler().queryM3u8Memory(httpRequest);
        //判断一下文件的长度是否获取得到
        if(info==null||info.getBlockList()==null){
            if(info==null){
                info=new M3u8Memory();
                info.setOriginalUrl(httpRequest.getConnectUrl());
            }
            //m3u8解析地址
            List<String> list=new ArrayList<>();

            if(!redirectResponse(info,list))
                return;

            if(list.isEmpty()){
                //失败重试
                retryToRun();
                return;
            }

            //获取所有的 ts文件的地址
            ArrayList<M3u8Block> m3u8Blocks=new ArrayList<>();
            URL url;
            if(info.getRedirectUrl()!=null){
                url=new URL(info.getRedirectUrl());
            } else{
                url=new URL(info.getOriginalUrl());
            }

            for(String name: list){
                M3u8Block block=new M3u8Block();
                block.setUrl(getRedirectsUrl(url,name));
                block.setName(XDownUtils.getUrlName(block.getUrl()));
                m3u8Blocks.add(block);
            }

            //获取长度的时间太耗时了
//            if(!httpRequest.isIgnoredProgress() && httpRequest.getOnProgressListener() != null){
//                //遍历请求获取所有文件的长度
//                long length=0;
//                for(M3u8Block m3u8Block: m3u8Blocks){
//                    HttpURLConnection http=httpRequest.buildConnect(m3u8Block.getUrl());
//                    //优先获取文件长度再回调
//                    long contentLength=XDownUtils.getContentLength(http);
//                    //回调文件
//                    listenerDisposer.onConnecting(this);
//
//                    m3u8Block.setContentLength(contentLength);
//                    length+=contentLength;
//                    System.out.println(m3u8Block.getUrl()+" ->> length="+contentLength);
//                    XDownUtils.disconnectHttp(http);
//                }
//                sContentLength=length;
//                info.setLength(length);
//            }
            info.setBlockList(m3u8Blocks);
            info.setIdentifier(httpRequest.getIdentifier());
            //保存下来
            XDownload.get().getMemoryHandler().saveM3u8Memory(httpRequest,info);
        }

        //判断下载方式
        if(httpRequest.isUseMultiThread()&&httpRequest.getMultiThreadCount()>1){
            //多线程下载
            multiThreadRun(info.getBlockList());
        } else{
            //单线程下载
            SingleDownloadM3u8Task threadTask=new SingleDownloadM3u8Task(httpRequest,
                                                                         listenerDisposer,
                                                                         info.getBlockList());
            if(!threadTask.checkComplete()){
                Future<?> future=ExecutorGather.executorQueue().submit(threadTask);
                threadTask.setTaskFuture(future);
                XDownload.get().addDownload(httpRequest.getTag(),threadTask);
            }
        }
    }

    /**
     * 解析重定向的数据
     *
     * @param info
     * @param list
     * @return
     * @throws Exception
     */
    private boolean redirectResponse(M3u8Memory info,List<String> list) throws Exception{
        String redirectResponse=info.getRedirectResponse();
        if(redirectResponse!=null){
            splitStringList(list,redirectResponse);
            return true;
        } else{
            if(info.getRedirectUrl()!=null){
                return getResponse(info.getRedirectUrl(),info,list,true);
            } else{
                return originalResponse(info,list);
            }
        }
    }

    /**
     * 解析原始链接的数据
     *
     * @param info
     * @param list
     * @return
     * @throws Exception
     */
    private boolean originalResponse(M3u8Memory info,List<String> list) throws Exception{
        String originalResponse=info.getOriginalResponse();
        if(originalResponse!=null){
            //获取原始的下载信息,分割字符串
            splitStringList(list,originalResponse);
        }
        //如果为空
        if(list.isEmpty()){
            //重新下载原始的下载信息
            boolean response=getResponse(info.getOriginalUrl(),info,list,false);
            if(response){
                //判断是否需要重定向
                if(info.getRedirectUrl()!=null){
                    return redirectResponse(info,list);
                }
            }
            return response;
        }
        return true;//成功!
    }

    /**
     * 分割地址
     *
     * @param list
     * @param redirectResponse
     * @return
     */
    private void splitStringList(List<String> list,String redirectResponse){
        list.clear();
        String[] split=redirectResponse.split("\r?\n");
        for(String key: split){
            if(!key.startsWith("#")){
                list.add(key);
            }
        }
    }

    /**
     * 获取指定地址的响应结果
     *
     * @param url
     * @return false 需要重试
     * @throws Exception
     */
    private boolean getResponse(String url,M3u8Memory info,List<String> list,boolean isRedirect) throws Exception{
        HttpURLConnection http=httpRequest.buildConnect(url);
        int responseCode=http.getResponseCode();
        //判断是否需要重定向
        while(isNeedRedirects(responseCode)){
            http=redirectsConnect(http,httpRequest);
            responseCode=http.getResponseCode();
        }

        if(!isSuccess(responseCode)){
            //获取错误信息
            String stream=readStringStream(http.getErrorStream(),XDownUtils.getInputCharset(http));
            listenerDisposer.onRequestError(this,responseCode,stream);
            //断开请求
            XDownUtils.disconnectHttp(http);
            //重试
            retryToRun();
            return false;//不成功,需要重试
        }
        String response=readStringStream(http.getInputStream(),XDownUtils.getInputCharset(http));
        //重新分割一下字符串
        splitStringList(list,response);
        //原始的下载地址
        if(list.size()==1){
            String redirectUrl=list.get(0);
            if(redirectUrl.endsWith(".m3u8")){
                //设置重定向的地址
                info.setRedirectUrl(getRedirectsUrl(http.getURL(),redirectUrl));
            }
        }
        if(isRedirect){
            info.setRedirectResponse(response);
        } else{
            info.setOriginalResponse(response);
        }
        //断开连接
        XDownUtils.disconnectHttp(http);
        return true;//成功
    }


    private void multiThreadRun(List<M3u8Block> list){
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
        File tempCacheDir=XDownUtils.getTempCacheDir(httpRequest);


        //是否需要删除之前的临时文件
        final boolean isDelectTemp=!httpRequest.isUseBreakpointResume();
        if(isDelectTemp){
            //需要删除之前的临时缓存文件
            XDownUtils.delectDir(tempCacheDir);
        }

        threadPoolExecutor=ExecutorGather.newSubtaskQueue(httpRequest.getMultiThreadCount());

        final MultiM3u8Disposer disposer=new MultiM3u8Disposer(httpRequest,list,listenerDisposer);


        for(int i=0;i<list.size();i++){
            M3u8Block m3u8Block=list.get(i);
            //保存的临时文件
            File tempM3u8=new File(tempCacheDir,m3u8Block.getName());

            MultiDownloadM3u8Task task=new MultiDownloadM3u8Task(httpRequest,
                                                                 m3u8Block,
                                                                 tempM3u8,
                                                                 autoRetryRecorder,
                                                                 i,
                                                                 disposer);
            Future<?> submit=threadPoolExecutor.submit(task);
            XDownload.get().addDownload(httpRequest.getTag(),task);
            task.setTaskFuture(submit);
        }
        threadPoolExecutor.shutdown();
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
        return 0;
    }

    @Override
    public long getSofarLength(){
        return 0;
    }
}
