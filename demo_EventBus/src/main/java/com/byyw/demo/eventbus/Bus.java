package com.byyw.demo.eventbus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Bus {
    
    @Autowired
    private AsyncEventBus asyncEventBus;
    @Autowired
    private EventBus eventBus;
    
    public void asyncPost(EventMsg event){
        asyncEventBus.post(event);
    }

    public void post(EventMsg event){
        eventBus.post(event);
    }

    public void post(DeadEvent event){
        eventBus.post(event);
    }
}
