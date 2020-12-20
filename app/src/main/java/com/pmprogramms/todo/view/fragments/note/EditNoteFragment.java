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
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNoteFragment extends Fragment implements View.OnClickListener {
    private String title, contents, userToken, noteID;

    private Context context;
    private MainActivity mainActivity;

    private EditText titleEditText, contentsEditText;
    private FloatingActionButton saveButton;
    private ProgressDialog progressDialog;

    public EditNoteFragment(String title, String contents, String noteID) {
        this.title = title;
        this.contents = contents;
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
        userToken = new UserData(context).getUserToken();

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

        int id = v.getId();

        if (id == R.id.save_note) {
            progressDialog = ProgressDialog.show(context, "Update...", "Please wait...");
            title = titleEditText.getText().toString().trim();
            contents = contentsEditText.getText().toString();

            HashMap<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("contents", contents);
            editNote(map);
        }
    }

    private void editNote(HashMap<String, String> data) {
        API api = Client.getInstance().create(API.class);
        Call<Void> call = api.editNote(noteID, data, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    new Messages(context).showMessage("Note updated");
                    mainActivity.closeFragment(EditNoteFragment.this, new NotePreviewFragment(noteID));
                } else {
                    new Messages(context).showMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.cancel();
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }
}
