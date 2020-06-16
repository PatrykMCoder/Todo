package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.MainActivity;
import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.formats.StringFormater;
import com.example.todo.view.fragments.TodoDetailsFragment;
import com.example.todo.view.fragments.TodoFragment;

import java.io.File;

public class DeleteTodoAskDialog extends DialogFragment {
    private String title;
    private Context context;
    private MainActivity mainActivity;
    private TodoDetailsFragment todoDetailsFragment;

    public DeleteTodoAskDialog(Context context, MainActivity mainActivity, TodoDetailsFragment todoDetailsFragment, String titleTodo) {
        title = titleTodo;
        this.context = context;
        this.mainActivity = mainActivity;
        this.todoDetailsFragment = todoDetailsFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete todo")
                .setMessage(String.format("Do you want delete: %s?", title))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new TodoAdapter(context, title).deleteTodo(new StringFormater(title).formatTitle());
                        String path = "";
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            path = context.getDataDir() + "/databases";
                        } else {
                            path = "data/data/" + context.getPackageName() + "/databases/";
                        }
                        File fileToRemove = new File(String.valueOf(path) + "/" + new StringFormater(title).formatTitle() + ".db");
                        if (fileToRemove.delete())
                            mainActivity.closeFragment(todoDetailsFragment, new TodoFragment());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        return builder.create();
    }


    /*

     */
}
