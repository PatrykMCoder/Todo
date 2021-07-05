package com.pmprogramms.todo.helpers.validation;

public enum RegisterValidationEnum {

    EMPTY_EMAIL("Email can't be empty"),
    WRONG_EMAIL("Wrong email format"),
    EMPTY_PASSWORD("Password can't be empty"),
    SHORT_PASSWORD("Password is too short"),
    NOT_ACCEPTED_PRIVACY("Please accept privacy policy"),
    EMPTY_USERNAME("Username can't be empty"),
    PASSWORD_NOT_MATCH("Passwords are not same"),
    OK("");

    private String msg;
    RegisterValidationEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
