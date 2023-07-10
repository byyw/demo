package com.byyw.demo.eventbus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cn.hutool.json.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExtendsMsgListener {

    @Autowired
    private AsyncEventBus asyncEventBus;
    @Autowired
    private EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        asyncEventBus.register(this);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventMsg e = new EventMsg();
                e.setId("1");
                eventBus.post(e);
            }
        }).start();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handlerFatherMsg(Object event) {
        log.info("handlerFatherMsg:{}", new JSONObject(event).toString());
    }
}
