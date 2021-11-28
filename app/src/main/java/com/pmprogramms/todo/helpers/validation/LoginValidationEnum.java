package com.pmprogramms.todo.helpers.validation;

public enum LoginValidationEnum {

    EMPTY_EMAIL("Email can't be empty"),
    WRONG_EMAIL("Wrong email format"),
    EMPTY_PASSWORD("Password can't be empty"),
    SHORT_PASSWORD("Password is too short"),
    OK("");

    private final String msg;
    LoginValidationEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
