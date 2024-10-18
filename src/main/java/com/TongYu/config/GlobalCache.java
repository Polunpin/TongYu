package com.TongYu.config;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalCache {

    private static final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    // 清空缓存
    public static void clear() {
        cache.clear();
    }
}
