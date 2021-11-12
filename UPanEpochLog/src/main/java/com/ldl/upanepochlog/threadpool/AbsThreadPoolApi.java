package com.ldl.upanepochlog.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public
abstract class AbsThreadPoolApi implements IThreadPoolApi, IThreadPoolOpt {
    protected ThreadPoolExecutor threadPoolExecutor;


    @Override
    public void init() {
        if (null == threadPoolExecutor) {
            synchronized (AbsThreadPoolApi.class) {
                threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }

        }

    }



    @Override
    public Future<?> commitTask(Runnable runnable) {
        init();
        return threadPoolExecutor.submit(runnable);
    }

    @Override
    public void executeTask(Runnable runnable) {
        init();
        threadPoolExecutor.execute(runnable);
    }

    @Override
    public void removeTask(Runnable runnable) {
        if (null != threadPoolExecutor && runnable != null) {
            threadPoolExecutor.remove(runnable);
        }

    }

    @Override
    public void closeThread() {
        if (null != threadPoolExecutor) {
            threadPoolExecutor.shutdown();
        }
    }
}
