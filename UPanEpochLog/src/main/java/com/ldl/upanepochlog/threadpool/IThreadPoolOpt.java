package com.ldl.upanepochlog.threadpool;

import java.util.concurrent.Future;

public
interface IThreadPoolOpt {
    Future<?> commitTask(Runnable runnable);

    void removeTask(Runnable runnable);

    void closeThread();

    void executeTask(Runnable runnable);
}
