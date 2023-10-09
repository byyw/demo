package com.byyw.demo.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;

// 枚举单例
public class MyTableNameHandler implements TableNameHandler {
    private static ThreadLocal<String> table = new ThreadLocal<>();

    public static void set(String tableName) {
        table.set(tableName);
    }

    @Override
    public String dynamicTableName(String sql, String tn) {
        tn = table.get();
        return tn;
    }
}
