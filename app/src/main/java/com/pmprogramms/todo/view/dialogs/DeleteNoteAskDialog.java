package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.fragments.note.NoteFragment;
import com.pmprogramms.todo.view.fragments.note.NotePreviewFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteNoteAskDialog extends DialogFragment {
    private final String noteID;
    private String title;
    private Context context;
    private MainActivity mainActivity;
    private NotePreviewFragment notePreviewFragment;

    public DeleteNoteAskDialog(Context context, MainActivity mainActivity, NotePreviewFragment notePreviewFragment, String title, String noteID) {
        this.title = title;
        this.context = context;
        this.mainActivity = mainActivity;
        this.notePreviewFragment = notePreviewFragment;
        this.noteID = noteID;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete note")
                .setMessage(String.format("Do you want delete: %s?", title))
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show();
                    deleteTodo();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }

    private void deleteTodo() {
        String userToken = new UserData(context).getUserToken();
        API api = Client.getInstance().create(API.class);
        Call<Void> call = api.deleteNote(noteID, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage(response.message());
                }
                new Messages(context).showMessage("Note deleted");
                mainActivity.closeFragment(notePreviewFragment, new NoteFragment());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }
}
