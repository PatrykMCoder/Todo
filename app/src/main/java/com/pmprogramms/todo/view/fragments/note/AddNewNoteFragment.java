package com.pmprogramms.todo.view.fragments.note;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;

public class AddNewNoteFragment extends Fragment {

    private EditText titleEditText, contentsEditText;

    private View rootView;
    private ProgressDialog progressDialog;
    private FloatingActionButton saveNoteButton;

    private Context context;
    private MainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_new_note, container, false);

        titleEditText = rootView.findViewById(R.id.new_title_note);
        contentsEditText = rootView.findViewById(R.id.new_contents_note);
        saveNoteButton = rootView.findViewById(R.id.save_note);

        saveNoteButton.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(context, "Save...", "Please wait..");

            String userID = new UserData(context).getUserID();
            String title = titleEditText.getText().toString().trim();
            String contents = contentsEditText.getText().toString();

            SaveNoteThread saveNoteThread = new SaveNoteThread();
            saveNoteThread.execute(userID, title, contents);
        });

        return rootView;
    }

    class SaveNoteThread extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient apiClient = new APIClient();
            String userID = strings[0];
            String title = strings[1];
            String contents = strings[2];
            int code = apiClient.saveNote(userID, title, contents);
            if (code == 200 || code == 201)
                return TaskState.DONE;

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);

            progressDialog.dismiss();

            switch (taskState) {
                case DONE: {
                    mainActivity.closeFragment(AddNewNoteFragment.this, new NoteFragment());
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot save todo, try again");
                    break;
                }
            }
        }
    }

}
