package com.ldl.upanepochlog.threadpool;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ScheduledThteadpoolImpl implements IThreadPoolApi {
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Override
    public void init() {
        if (scheduledThreadPoolExecutor == null) {
            synchronized (ScheduledThteadpoolImpl.class) {
                scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ScheduledThreadFactory());
            }

        }
    }

    public void dismiss() {
        if (null != scheduledThreadPoolExecutor) {
            scheduledThreadPoolExecutor.shutdown();
        }
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        init();
        return scheduledThreadPoolExecutor;
    }

    private class ScheduledThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            return thread;
        }
    }

}
