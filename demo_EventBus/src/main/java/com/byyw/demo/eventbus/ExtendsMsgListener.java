package com.byyw.demo.eventbus;

import org.springframework.stereotype.Component;

import com.byyw.demo.plus.EventParam;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import cn.hutool.json.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExtendsMsgListener {
    @PostConstruct
    public void construct() {
        EventBusInstance.registerAll(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    @EventParam("ddd")
    public void handlerFatherMsg(EventMsg event) {
        log.info("ddd :{}", new JSONObject(event).toString());
    }

    @Subscribe
    @AllowConcurrentEvents
    @EventParam("dd")
    public void handlerFatherMsg2(EventMsg event) {
        log.info("dd :{}", new JSONObject(event).toString());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handlerFatherMsg3(EventMsg event) {
        log.info("null :{}", new JSONObject(event).toString());
    }
}
