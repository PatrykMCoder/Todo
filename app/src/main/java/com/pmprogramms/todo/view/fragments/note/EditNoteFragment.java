package com.pmprogramms.todo.view.fragments.note;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.utils.text.Messages;

public class EditNoteFragment extends Fragment implements View.OnClickListener {
    private String title, contents, userID, noteID;

    private Context context;
    private MainActivity mainActivity;

    private EditText titleEditText, contentsEditText;
    private FloatingActionButton saveButton;
    private ProgressDialog progressDialog;

    public EditNoteFragment(String title, String contents, String userID, String noteID) {
        this.title = title;
        this.contents = contents;
        this.userID = userID;
        this.noteID = noteID;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        titleEditText = view.findViewById(R.id.title_edit);
        contentsEditText = view.findViewById(R.id.edit_contents);

        saveButton = view.findViewById(R.id.save_note);
        saveButton.setOnClickListener(this);

        titleEditText.setText(title);
        contentsEditText.setText(contents);

        return view;
    }

    @Override
    public void onClick(View v) {
        progressDialog = ProgressDialog.show(context, "Update...", "Please wait...");

        int id = v.getId();

        if (id == R.id.save_note) {
            title = titleEditText.getText().toString().trim();
            contents = contentsEditText.getText().toString();
            EditNoteAsync editNoteAsync = new EditNoteAsync();
            editNoteAsync.execute();
        }

    }

    class EditNoteAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient apiClient = new APIClient();
            int code = apiClient.editNote(userID, noteID, title, contents);
            if (code == 200 || code == 201)
                return TaskState.DONE;

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            progressDialog.dismiss();

            switch (state) {
                case DONE: {
                    mainActivity.closeFragment(EditNoteFragment.this, new NotePreviewFragment(userID, noteID));
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot update note, try again");
                    break;
                }
            }
        }
    }
}
