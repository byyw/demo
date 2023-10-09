package com.byyw.demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.byyw.demo.mapper.TestMapper;
import com.byyw.demo.po.Test;
import com.byyw.demo.service.TestService;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper,Test> implements TestService{
    
}
