package com.controller.DItest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleDIContainer {
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 注册一个Bean到容器中。
     * clazz Bean的类型（Class对象）
     * instance Bean的实例
     */
    public <T> void registerBean(Class<T> clazz, T instance) {
        // 确保容器中还没有同类型的Bean，避免覆盖
        if (beanMap.containsKey(clazz)) {
            throw new IllegalArgumentException("Bean of type " + clazz.getName() + " is already registered.");
        }
        beanMap.put(clazz, instance);
    }

    /**
     * 根据类型从容器中获取Bean实例。
     * clazz Bean的类型（Class对象）
     * <T> Bean的类型
     * @return Bean的实例
     */
    public <T> T getBean(Class<T> clazz) {
        Object bean = beanMap.get(clazz);
        if (bean == null) {
            throw new NoSuchElementException("Bean of type " + clazz.getName() + " not found in the container.");
        }
        return (T) bean;
    }

}