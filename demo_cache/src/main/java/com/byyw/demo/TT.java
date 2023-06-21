package com.byyw.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.byyw.demo.util.SystemCache;
import jakarta.annotation.PostConstruct;

@Component
public class TT {

    @PostConstruct
    public static void init() throws Exception {
        
    }
    public static void main(String[] args) {
        try {
            init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
