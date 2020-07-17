package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.MainActivity;
import com.example.todo.database.TodoAdapter;
import com.example.todo.service.MongoDBClient;
import com.example.todo.utils.formats.StringFormater;
import com.example.todo.view.fragments.TodoDetailsFragment;
import com.example.todo.view.fragments.TodoFragment;

import java.io.File;

public class DeleteTodoAskDialog extends DialogFragment {
    private final String userID;
    private final String todoID;
    private String title;
    private Context context;
    private MainActivity mainActivity;
    private TodoDetailsFragment todoDetailsFragment;

    public DeleteTodoAskDialog(Context context, MainActivity mainActivity, TodoDetailsFragment todoDetailsFragment, String titleTodo, String userID, String todoID) {
        title = titleTodo;
        this.context = context;
        this.mainActivity = mainActivity;
        this.todoDetailsFragment = todoDetailsFragment;
        this.userID = userID;
        this.todoID = todoID;
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
                        Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show();
                        DeleteTodoAsync deleteTodoAsync = new DeleteTodoAsync();
                        deleteTodoAsync.execute();
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
    class DeleteTodoAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            int code = mongoDBClient.deleteTodo(userID, todoID);
            if(code == 201)
                return "done";
            else
                return "notDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("done")) {
                mainActivity.closeFragment(todoDetailsFragment, new TodoFragment());
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
