package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.fragments.TodoDetailsFragment;
import com.pmprogramms.todo.view.fragments.TodoFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteTodoAskDialog extends DialogFragment {
    private final String userToken;
    private final String todoID;
    private String title;
    private Context context;
    private MainActivity mainActivity;
    private TodoDetailsFragment todoDetailsFragment;

    public DeleteTodoAskDialog(Context context, MainActivity mainActivity, TodoDetailsFragment todoDetailsFragment, String titleTodo, String userToken, String todoID) {
        title = titleTodo;
        this.context = context;
        this.mainActivity = mainActivity;
        this.todoDetailsFragment = todoDetailsFragment;
        this.userToken = userToken;
        this.todoID = todoID;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete todo")
                .setMessage(String.format("Do you want delete: %s?", title))
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show();
                    API api = Client.getInstance().create(API.class);
                    Call<Void> call = api.deleteTodo(todoID, userToken);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(!response.isSuccessful()){
                                new Messages(context).showMessage(response.message());
                            }
                            new Messages(context).showMessage("Todo deleted");
                            mainActivity.closeFragment(todoDetailsFragment, new TodoFragment());
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            new Messages(context).showMessage(t.getMessage());
                        }
                    });
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }
}
