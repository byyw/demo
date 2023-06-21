package com.byyw.demo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.Data;

/**
 * 系统缓存
 * 使用前必须先通过init初始化值，可设定值的有效期，如果设置为0表示永久有效，有效期过后值将被重新初始化
 * 如果一个对象超过1天未被访问，将被自动清除，但无需再初始化，包括永久有效的对象
 */
public class SystemCache {

    private static Map<String, Callable<Cell>> func = new ConcurrentHashMap<>();

    private static Cache<String, Cell> cache = Caffeine
            .newBuilder()
            .build();

    private static Cell cache_get(String key) {
        return cache.get(key, new Function<String, Cell>() {
            @Override
            public Cell apply(String key) {
                try {
                    return func.get(key).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
    private static void cache_refresh(String key) {
        try {
            cache.put(key,func.get(key).call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(String key, Integer period, Callable<? extends Object> callback) {
        init(key, period.longValue(), callback);
    }

    public static void init(String key, Long period, Callable<? extends Object> callback) {
        func.put(key, () -> {
            Cell c = new Cell();
            c.setValue(callback.call());
            if (period != 0)
                c.setTime(System.currentTimeMillis() + period);
            else
                c.setTime(Long.MAX_VALUE);
            return c;
        });
    }

    public static void put(String key, Object value) throws ExecutionException {
        if (!func.containsKey(key)) {
            throw new RuntimeException("key not exist,please init first");
        }
        cache_get(key).setValue(value);
    }

    public static Integer plusValue(String key, Integer i) throws ExecutionException {
        if (!func.containsKey(key)) {
            throw new RuntimeException("key not exist,please init first");
        }

        Cell c = cache_get(key);
        if (c.getTime() < System.currentTimeMillis()) {
            cache_refresh(key);
            c = cache_get(key);
        }
        if (c.getValue() instanceof Integer) {
            c.setValue((Integer) c.getValue() + i);
        }
        return (Integer) c.getValue();
    }

    public static <T> T get(String key) throws ExecutionException {
        if (!func.containsKey(key)) {
            throw new RuntimeException("key not exist,please init first");
        }
        Cell c = cache_get(key);
        if (c.getTime() < System.currentTimeMillis()) {
            cache_refresh(key);
            c = cache_get(key);
        }
        return (T) c.getValue();
    }

    // public static <T> T get(String key, Callable<T> callback) throws
    // ExecutionException {
    // return (T) cache.get(key, callback);
    // }

    public static Map<String, Object> getAll() {
        Map<String, Cell> ms = cache.asMap();
        Map<String, Object> mo = new HashMap<>();
        for (Map.Entry<String, Cell> e : ms.entrySet()) {
            mo.put(e.getKey(), e.getValue().getValue());
        }
        return mo;
    }

    public static void removeAll() {
        cache.invalidateAll();
    }

    public static void remove(String key) {
        cache.invalidate(key);
    }

    @Data
    public static class Cell {
        private Object value;
        private Long time;
    }

}