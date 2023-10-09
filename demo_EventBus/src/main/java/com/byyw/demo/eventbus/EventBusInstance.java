package com.byyw.demo.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.byyw.demo.plus.EventBusPlus;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class EventBusInstance {
    public static AsyncEventBus asyncEventBus;
    public static EventBusPlus eventBus;

    static {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("asyncEventBus").build();
        ExecutorService executorService = new ThreadPoolExecutor(
                5, 20, 10000l, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(64), namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        asyncEventBus = new AsyncEventBus(executorService);

        eventBus = new EventBusPlus();
    }

    public static void registerAll(Object obj) {
        asyncEventBus.register(obj);
        eventBus.register(obj);
    }

    public static void post(Object obj, String key) {
        eventBus.post(obj, key);
    }

    public static void asyncPost(Object obj) {
        asyncEventBus.post(obj);
    }
}
