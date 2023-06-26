package com.byyw.demo;

import java.io.IOException;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byyw.demo.util.HeartManager;
import com.byyw.demo.util.HttpClientUtils;
import com.byyw.demo.util.SystemCache;

import jakarta.annotation.PostConstruct;

@Component
public class TT {

    @Autowired
    private HeartManager heartManager;

    @PostConstruct
    public void test() throws Exception {
    }

    public void a1() throws Exception {
        while (true) {
            for (int i = 0; i < 1000; i++) {
                String id = String.format("%03d", i);
                HttpClientUtils.sendAsyncPost("http://192.168.1.77:8083/faceRcognition/heart?SerialNo=5R17R300" + id
                        + "&DevName=Cam1&Left=992", null);
            }
            Thread.sleep(2000);
            System.out.println(111);
        }

    }

}
