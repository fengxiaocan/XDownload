package com.x.down.task;


import com.x.down.ExecutorGather;
import com.x.down.XDownload;
import com.x.down.base.IConnectRequest;
import com.x.down.base.IDownloadRequest;
import com.x.down.core.XDownloadRequest;
import com.x.down.impl.DownloadListenerDisposer;
import com.x.down.listener.OnDownloadConnectListener;
import com.x.down.listener.OnDownloadListener;
import com.x.down.listener.OnProgressListener;
import com.x.down.listener.OnSpeedListener;
import com.x.down.made.AutoRetryRecorder;
import com.x.down.made.M3u8DownloaderBlock;
import com.x.down.made.M3u8DownloaderInfo;
import com.x.down.tool.XDownUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

final class M3u8DownloaderRequest extends HttpDownloadRequest implements IDownloadRequest, IConnectRequest {

    protected final XDownloadRequest httpRequest;
    protected final DownloadListenerDisposer listenerDisposer;
    protected ThreadPoolExecutor threadPoolExecutor;
    protected volatile Future taskFuture;

    public M3u8DownloaderRequest(
            XDownloadRequest request,
            OnDownloadConnectListener onConnectListeners,
            OnDownloadListener downloadListeners,
            OnProgressListener onProgressListener,
            OnSpeedListener onSpeedListener) {
        super(new AutoRetryRecorder(request.isUseAutoRetry(),
                request.getAutoRetryTimes(),
                request.getAutoRetryInterval()), request.getBufferedSize());
        this.httpRequest = request;
        this.listenerDisposer = new DownloadListenerDisposer(request.getSchedulers(),
                onConnectListeners,
                downloadListeners,
                onProgressListener,
                onSpeedListener);
    }

    public final void setTaskFuture(Future taskFuture) {
        this.taskFuture = taskFuture;
    }

    @Override
    protected void httpRequest() throws Exception {
        //???????????????????????????
        M3u8DownloaderInfo info = InfoSerializeProxy.readM3u8DownloadInfo(httpRequest);

        //?????????????????????????????????????????????
        if (info == null || info.getBlockList() == null) {
            if (info == null) {
                info = new M3u8DownloaderInfo();
                info.setOriginalUrl(httpRequest.getConnectUrl());
            }
            //m3u8????????????
            List<String> list = new ArrayList<>();

            if (!redirectResponse(info, list))
                return;

            if (list.isEmpty()) {
                //????????????
                retryToRun();
                return;
            }

            //??????????????? ts???????????????
            ArrayList<M3u8DownloaderBlock> m3U8DownloaderBlocks = new ArrayList<>();
            URL url;
            if (info.getRedirectUrl() != null) {
                url = new URL(info.getRedirectUrl());
            } else {
                url = new URL(info.getOriginalUrl());
            }

            for (String name : list) {
                M3u8DownloaderBlock block = new M3u8DownloaderBlock();
                block.setUrl(getRedirectsUrl(url, name));
                block.setName(XDownUtils.getUrlName(block.getUrl()));
                m3U8DownloaderBlocks.add(block);
            }

            info.setBlockList(m3U8DownloaderBlocks);
            //????????????
            InfoSerializeProxy.writeM3u8DownloadInfo(httpRequest, info);
        }

        //??????????????????
        if (httpRequest.isUseMultiThread() && httpRequest.getDownloadMultiThreadSize() > 1) {
            //???????????????
            multiThreadRun(info.getBlockList());
        } else {
            //???????????????
            SingleDownloadM3u8Task threadTask = new SingleDownloadM3u8Task(httpRequest,
                    listenerDisposer,
                    info.getBlockList());
            if (!threadTask.checkComplete()) {
                Future<?> future = ExecutorGather.executorDownloaderQueue().submit(threadTask);
                threadTask.setTaskFuture(future);
                XDownload.get().addDownload(httpRequest.getTag(), threadTask);
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param info
     * @param list
     * @return
     * @throws Exception
     */
    private boolean redirectResponse(M3u8DownloaderInfo info, List<String> list) throws Exception {
        String redirectResponse = info.getRedirectResponse();
        if (redirectResponse != null) {
            splitStringList(list, redirectResponse);
            return true;
        } else {
            if (info.getRedirectUrl() != null) {
                return getResponse(info.getRedirectUrl(), info, list, true);
            } else {
                return originalResponse(info, list);
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param info
     * @param list
     * @return
     * @throws Exception
     */
    private boolean originalResponse(M3u8DownloaderInfo info, List<String> list) throws Exception {
        String originalResponse = info.getOriginalResponse();
        if (originalResponse != null) {
            //???????????????????????????,???????????????
            splitStringList(list, originalResponse);
        }
        //????????????
        if (list.isEmpty()) {
            //?????????????????????????????????
            boolean response = getResponse(info.getOriginalUrl(), info, list, false);
            if (response) {
                //???????????????????????????
                if (info.getRedirectUrl() != null) {
                    return redirectResponse(info, list);
                }
            }
            return response;
        }
        return true;//??????!
    }

    /**
     * ????????????
     *
     * @param list
     * @param redirectResponse
     * @return
     */
    private void splitStringList(List<String> list, String redirectResponse) {
        list.clear();
        String[] split = redirectResponse.split("\r?\n");
        for (String key : split) {
            if (!key.startsWith("#")) {
                list.add(key);
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param url
     * @return false ????????????
     * @throws Exception
     */
    private boolean getResponse(String url, M3u8DownloaderInfo info, List<String> list, boolean isRedirect) throws Exception {
        HttpURLConnection http = httpRequest.buildConnect(url);
        int responseCode = http.getResponseCode();
        //???????????????????????????
        while (isNeedRedirects(responseCode)) {
            http = redirectsConnect(http, httpRequest);
            responseCode = http.getResponseCode();
        }

        if (!isSuccess(responseCode)) {
            //??????????????????
            String stream = readStringStream(http.getErrorStream(), XDownUtils.getInputCharset(http));
            listenerDisposer.onRequestError(this, responseCode, stream);
            //????????????
            XDownUtils.disconnectHttp(http);
            //??????
            retryToRun();
            return false;//?????????,????????????
        }
        String response = readStringStream(http.getInputStream(), XDownUtils.getInputCharset(http));
        //???????????????????????????
        splitStringList(list, response);
        //?????????????????????
        if (list.size() == 1) {
            String redirectUrl = list.get(0);
            if (redirectUrl.endsWith(".m3u8")) {
                //????????????????????????
                info.setRedirectUrl(getRedirectsUrl(http.getURL(), redirectUrl));
            }
        }
        if (isRedirect) {
            info.setRedirectResponse(response);
        } else {
            info.setOriginalResponse(response);
        }
        //????????????
        XDownUtils.disconnectHttp(http);
        return true;//??????
    }


    private void multiThreadRun(List<M3u8DownloaderBlock> list) {
        if (threadPoolExecutor != null) {
            //isShutDown????????????shutdown()???shutdownNow()??????????????????true?????
            //isTerminated????????????shutdown()?????????????????????????????????????????????????????????true;
            //isTerminated????????????shutdownNow()????????????????????????????????????true;
            if (threadPoolExecutor.isShutdown()) {
                //??????????????????
                return;
            }
            if (!threadPoolExecutor.isTerminated()) {
                //?????????????????????????????????
                return;
            }
        }
        //??????????????????,???????????????????????????
        File tempCacheDir = XDownUtils.getTempCacheDir(httpRequest);


        //???????????????????????????????????????
        final boolean isDelectTemp = !httpRequest.isUseBreakpointResume();
        if (isDelectTemp) {
            //???????????????????????????????????????
            XDownUtils.deleteDir(tempCacheDir);
        }

        threadPoolExecutor = ExecutorGather.newSubTaskQueue(httpRequest.getDownloadMultiThreadSize());

        final CountDownLatch countDownLatch = new CountDownLatch(list.size());//?????????
        final MultiM3u8Disposer disposer = new MultiM3u8Disposer(httpRequest, countDownLatch, getFilePath(), list, listenerDisposer);

        String filePath = getFilePath();
        for (int i = 0; i < list.size(); i++) {
            M3u8DownloaderBlock m3U8DownloaderBlock = list.get(i);
            //?????????????????????
            File tempM3u8 = new File(tempCacheDir, m3U8DownloaderBlock.getName());

            MultiDownloadM3u8Task task = new MultiDownloadM3u8Task(httpRequest,
                    filePath,
                    m3U8DownloaderBlock,
                    tempM3u8,
                    autoRetryRecorder,
                    i,
                    disposer);
            Future<?> submit = threadPoolExecutor.submit(task);
            XDownload.get().addDownload(httpRequest.getTag(), task);
            task.setTaskFuture(submit);
        }

        //??????????????????
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();
    }

    @Override
    protected void onRetry() {
        listenerDisposer.onRetry(this);
    }

    @Override
    protected void onError(Exception e) {
        listenerDisposer.onFailure(this);
    }

    @Override
    protected void onCancel() {
        listenerDisposer.onCancel(this);
    }

    @Override
    public String tag() {
        return httpRequest.getTag();
    }

    @Override
    public String url() {
        return httpRequest.getConnectUrl();
    }

    @Override
    public boolean cancel() {
        isCancel = true;
        if (taskFuture != null) {
            return taskFuture.cancel(true);
        }
        return false;
    }

    @Override
    public int retryCount() {
        return autoRetryRecorder.getRetryCount();
    }

    @Override
    public XDownloadRequest request() {
        return httpRequest;
    }

    private File getFile() {
        File saveFile = httpRequest.getSaveFile();
        if (saveFile != null) {
            return saveFile;
        }
        return new File(httpRequest.getSaveDir(), httpRequest.getSaveName() + ".mp4");
    }

    @Override
    public String getFilePath() {
        return getFile().getAbsolutePath();
    }

    @Override
    public long getTotalLength() {
        return 0;
    }

    @Override
    public long getSofarLength() {
        return 0;
    }
}
