package com.controller.servlet;

public class ThreadLocalUtils {
    private static final ThreadLocal<String> usernameThreadLocal = new ThreadLocal<>();

    public static void setUsername(String username) {
        usernameThreadLocal.set(username);
    }

    public static String getUsername() {
        return usernameThreadLocal.get();
    }

    public static void remove() {
        usernameThreadLocal.remove();
    }
}