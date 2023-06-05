package com.byyw.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
// @DependsOn("testBean")
public class TestComponent implements CommandLineRunner{
    public TestComponent(){
        System.out.println("init");
    }
    
    @PostConstruct
    public void construct(){
        System.out.println("TestComponent");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("TestComponentRun");
    }
}
