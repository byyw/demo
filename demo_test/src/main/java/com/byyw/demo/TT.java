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

    public static void main(String[] args) {
        T1("123");
    }

    public static void T1(Object a) {
        System.out.println("Object");
    }

    public static void T1(String a) {
        System.out.println("String");
    }

}
