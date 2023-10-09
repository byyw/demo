package com.byyw.demo.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@Configuration
public class EventBusConfig {
    
    @Bean
    public AsyncEventBus AsyncEventBus(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("asyncEventBus").build();
        ExecutorService executorService = new ThreadPoolExecutor(
            5,10,10000l,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(64),namedThreadFactory,new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return new AsyncEventBus(executorService);
    }

    @Bean
    public EventBus eventBus(){
        return new EventBus();
    }
}
