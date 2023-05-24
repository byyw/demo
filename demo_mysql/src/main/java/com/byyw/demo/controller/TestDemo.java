package com.byyw.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.byyw.demo.mapper.ColumnsMapper;
import com.byyw.demo.mapper.TestMapper;
import com.byyw.demo.po.Columns;

import jakarta.annotation.PostConstruct;


@Component
@RestController
public class TestDemo {

    @Autowired
    private TestMapper testMapper;
    @Autowired
    private ColumnsMapper columnsMapper;
    
    // @GetMapping("/test")
    @PostConstruct
    public void test() {
        List<Columns> list = columnsMapper.selectList(new QueryWrapper<Columns>().eq("table_name", "gw_device"));
        list.forEach(item-> System.out.println(item));
    }

    
}
