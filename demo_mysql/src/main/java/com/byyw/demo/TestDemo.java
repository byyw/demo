package com.byyw.demo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;


@Component
@RestController
public class TestDemo {

    @Autowired
    private TestMapper testMapper;
    
    // @GetMapping("/test")
    @PostConstruct
    public void test() {
        List<Map<String,Object>> list = testMapper.selectList();
        list.forEach(item-> System.out.println(item));
    }
}
