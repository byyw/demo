package com.byyw.demo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface GpsinfoMapper {

    public List<Map<String, Object>> selectList(@Param("sql")String sql);

    public int insertTest(@Param("sql")String sql);
}
