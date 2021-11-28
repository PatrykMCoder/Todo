package com.pmprogramms.todo.helpers.forms;

public class LoginFieldForm {
    private final String email;
    private final String password;

    public LoginFieldForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
