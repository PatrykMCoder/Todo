package com.example.todo.utils.formats;

import androidx.annotation.Nullable;

public class StringFormater {
    public String text;

    public StringFormater(String text){
        this.text = text;
    }

    public String formatTitle(){
        return text.replace(" ", "_");
    }

    public String deformatTitle() {
        return text.replace("_", " ");
    }

    public boolean isWhiteCharLast(){
        return text.endsWith(" ");
    }

    public String deleteWhiteChar() {
        int l = text.length();

        if(text.endsWith(" ")){
            text = text.substring(0, l - 1);
        }

        return text;
    }
}
