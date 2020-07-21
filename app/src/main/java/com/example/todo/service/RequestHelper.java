package com.example.todo.service;

public class RequestHelper {
    private static int code = 0;

    public static int getCode() {
        return code;
    }

    public static void setCode(int code) {
        RequestHelper.code = code;
    }
}
