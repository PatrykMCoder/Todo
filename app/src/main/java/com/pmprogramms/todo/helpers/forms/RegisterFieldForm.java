package com.pmprogramms.todo.helpers.forms;

public class RegisterFieldForm {
    private final String email;
    private final String password;
    private final String repeatPassword;
    private final String username;
    private final boolean acceptedPrivacy;

    public RegisterFieldForm(String email, String password, String repeatPassword, String username, boolean acceptedPrivacy) {
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.username = username;
        this.acceptedPrivacy = acceptedPrivacy;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAcceptedPrivacy() {
        return acceptedPrivacy;
    }
}
