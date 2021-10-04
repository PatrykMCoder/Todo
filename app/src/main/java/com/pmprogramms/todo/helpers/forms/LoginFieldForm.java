package com.pmprogramms.todo.helpers.forms;

public class LoginFieldForm {
    private String email, password;

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
