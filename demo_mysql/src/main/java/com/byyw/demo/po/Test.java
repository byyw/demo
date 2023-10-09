package com.byyw.demo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("test")
@Data
public class Test {
    @TableId
    private Integer id;
    private String str;

}
