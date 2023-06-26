package com.byyw.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TT {
    @Autowired
    @Qualifier(value = "globalRedisTemplate")
    private RedisTemplate<Object, Object> globalRedisTemplate; // redis连接

    @PostConstruct
    public void test() {
        int i = 0;
        long t = System.currentTimeMillis();
        while(true){
            i++;
            globalRedisTemplate.opsForValue().set("test"+i, "test"+i);       // 4.2w
            // globalRedisTemplate.opsForValue().get("test"+i);                    // 4.5w
            if(System.currentTimeMillis() - t > 1000){
                System.out.println(i);
                t = System.currentTimeMillis();
            }
        }
        
        // System.out.println(globalRedisTemplate.opsForValue().get("test"));
    }
}
