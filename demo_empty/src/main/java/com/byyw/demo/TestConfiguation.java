package com.byyw.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import jakarta.annotation.PostConstruct;

@Configuration
public class TestConfiguation implements Ordered{
    public TestConfiguation(){
        System.out.println("TestConfiguationinit");
    }

    @PostConstruct
    public void construct(){
        System.out.println("TestConfiguationConstruct");
    }

    @Bean
    public TestBean testBean(){
        System.out.println("TestConfiguation");
        return new TestBean();
    }

    public static class TestBean{
        public TestBean(){
            System.out.println("TestBean");
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
