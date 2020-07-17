package com.example.todo.utils.text;

import android.util.Patterns;

public class TextHelper {
    public boolean emailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
