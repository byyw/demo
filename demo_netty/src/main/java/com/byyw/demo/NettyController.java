package com.byyw.demo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.byyw.demo.NettyTcpServer.TcpResponseEvent;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

import cn.hutool.json.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class NettyController {

    @Autowired
    private AsyncEventBus asyncEventBus;
    @Autowired
    private NettyTcpServer nettyTcpServer;

    @PostConstruct
    public void init(){
        asyncEventBus.register(this);
    }
    
    @PostMapping("/send")
    public ResponseEntity<JSONObject> write(@RequestBody Map<String,Object> map){
        String id = (String) map.get("id");
        String message = (String) map.get("message");
        TcpResponseEvent tRes = null;
        try {
            tRes = (TcpResponseEvent)SO.wait(id, 30000, ()->{
                log.info(">>> "+id+" : "+message.replace(" ",""));
                nettyTcpServer.send(id, hexStringToBytes(message.replace(" ","")));
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        JSONObject res = new JSONObject();
        if(tRes != null){
            res.set("msg", tRes.getMsg());
        }
        return ResponseEntity.ok(res);
    }

    @Subscribe
    public void tcpResponseEventLicense(TcpResponseEvent e){
        SO.notifyAll(e.getId(), e);
    }

    public static byte[] hexStringToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public static class SO{
        public static Map<String,Cell> map = new HashMap<>();

        public static Object wait(String id,long limit,Run r) throws InterruptedException, TimeoutException{
            Cell c = new Cell();
            map.put(id, c);
            long startTime = System.currentTimeMillis();
            synchronized(c){
                r.run();
                while(c.getO() == null){
                    if(limit != -1){
                        long now = System.currentTimeMillis();
                        if(now-startTime>=limit){
                            throw new TimeoutException(id);
                        } else {
                            c.wait(limit+startTime-now);
                        }
                    } else {
                        c.wait();
                    }
                }
                map.remove(id);
            }
            return c.getO();
        }
        public static void notifyAll(String id,Object o){
            if(map.containsKey(id)){
                Cell c = map.get(id);
                synchronized(c){
                    c.setO(o);
                    c.notifyAll();
                }
            }
        }


        @Data
        public static class Cell{
            private Object o;
        }

        public interface Run{
            public abstract void run();
        }
    }
}
