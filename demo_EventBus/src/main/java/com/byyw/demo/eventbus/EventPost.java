package com.byyw.demo.eventbus;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EventPost {

    @PostConstruct
    public void construct() {
        new Thread(() -> {
            int i=0;
            while(true){
                EventMsg e = new EventMsg();
                e.setId(i+"");
                e.setCode("cc");
                e.setContent("con");

                EventBusInstance.post(e,"dddd");
                i++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        
        }).start();
    }
}
