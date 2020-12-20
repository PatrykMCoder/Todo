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

import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.fragments.note.NoteFragment;
import com.pmprogramms.todo.view.fragments.note.NotePreviewFragment;

public class DeleteNoteAskDialog extends DialogFragment {
    private final String userID;
    private final String noteID;
    private String title;
    private Context context;
    private MainActivity mainActivity;
    private NotePreviewFragment notePreviewFragment;

    public DeleteNoteAskDialog(Context context, MainActivity mainActivity, NotePreviewFragment notePreviewFragment, String title, String userID, String noteID) {
        this.title = title;
        this.context = context;
        this.mainActivity = mainActivity;
        this.notePreviewFragment = notePreviewFragment;
        this.userID = userID;
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
                    DeleteNoteAsync deleteNoteAsync = new DeleteNoteAsync();
                    deleteNoteAsync.execute();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }
    class DeleteNoteAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient();
            int code = APIClient.deleteNote(userID, noteID);
            if(code == 201 || code == 200)
                return TaskState.DONE;
            else
                return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);

            switch (state) {
                case DONE: {
                    new Messages(context).showMessage("Note deleted");
                    mainActivity.closeFragment(notePreviewFragment, new NoteFragment());
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot delete note, try again");
                    break;
                }
            }
        }
    }
}
