package com.github.shoothzj.config.client.impl.mysql;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hezhangjian
 */
@Slf4j
public class CcPool {

    private static final Executor ASYNC_NOTIFY_EXECUTOR = new ThreadPoolExecutor(5, 5,
            1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new DefaultThreadFactory("async-notify"));

    public static Executor getAsyncNotifyExecutor() {
        return ASYNC_NOTIFY_EXECUTOR;
    }
}
