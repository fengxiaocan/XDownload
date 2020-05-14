package com.xjava.down;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ExecutorGather{
    private static final int MAIN_TASK_KEEP_ALIVE_TIME=60;//主任务线程保活时间
    private static final int SUBTASK_KEEP_ALIVE_TIME=1;//子任务线程保活时间

    private static ThreadPoolExecutor taskExecutor;
    private static ExecutorService newExecutor;

    /**
     * 创建子任务线程池队列
     *
     * @return
     */
    public static ThreadPoolExecutor newSubtaskQueue(int corePoolSize){
        ThreadPoolExecutor executor=new ThreadPoolExecutor(corePoolSize,
                                                           Integer.MAX_VALUE,
                                                           SUBTASK_KEEP_ALIVE_TIME,
                                                           TimeUnit.SECONDS,
                                                           new LinkedBlockingQueue<Runnable>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static synchronized ThreadPoolExecutor executorQueue(){
        if(taskExecutor==null){
            taskExecutor=new ThreadPoolExecutor(XDownload.get().config().getSameTimeDownloadCount(),
                                                Integer.MAX_VALUE,
                                                MAIN_TASK_KEEP_ALIVE_TIME,
                                                TimeUnit.SECONDS,
                                                new LinkedBlockingQueue<Runnable>());
            taskExecutor.allowCoreThreadTimeOut(true);
        }
        return taskExecutor;
    }

    public static synchronized ExecutorService newThreadExecutor(){
        if(newExecutor==null){
            newExecutor=new ThreadPoolExecutor(XDownload.get().getMaxThreadCount(),
                                               Integer.MAX_VALUE,
                                               MAIN_TASK_KEEP_ALIVE_TIME,
                                               TimeUnit.SECONDS,
                                               new LinkedBlockingQueue<Runnable>());
        }
        return newExecutor;
    }
}
