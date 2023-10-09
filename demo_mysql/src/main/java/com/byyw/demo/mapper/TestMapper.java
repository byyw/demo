package com.byyw.demo.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byyw.demo.po.Test;

public interface TestMapper extends BaseMapper<Test>{
    public List<Map<String,Object>> selectList();
}
