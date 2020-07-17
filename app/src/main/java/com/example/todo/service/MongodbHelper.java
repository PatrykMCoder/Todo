package com.example.todo.service;

public class MongodbHelper {
    private static String username, password, email;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MongodbHelper.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MongodbHelper.password = password;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        MongodbHelper.email = email;
    }
}
