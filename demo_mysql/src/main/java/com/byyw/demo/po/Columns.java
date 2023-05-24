package com.byyw.demo.po;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("information_schema.columns")
public class Columns {
    private String tableCatalog;
}
