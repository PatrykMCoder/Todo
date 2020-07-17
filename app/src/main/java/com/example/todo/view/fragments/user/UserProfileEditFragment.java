package com.example.todo.view.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todo.R;

public class UserProfileEditFragment extends Fragment {
    private View rootView;
    private String userID;
    private String username;
    private String email;

    private TextView infoText;
    private EditText emailEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;

    /* TODO ( x -> done )
        - create view fields: email, username and password ( x )
        - load data to view - only email and username. Password is not readable
        - create checking method:
            if password field is not empty: update all data of user. First step is asking about password. If is correct (method return code 200 or 201) update data
            else if edit only email and username
     */

    public UserProfileEditFragment() {

    }

    public UserProfileEditFragment(String userID, String username, String email) {
        this.userID = userID;
        this.username = username;
        this.email = email;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_progile_user_edit, null);
        infoText = rootView.findViewById(R.id.info_text);
        emailEdit = rootView.findViewById(R.id.user_email_edit);
        usernameEdit = rootView.findViewById(R.id.user_name_edit);
        passwordEdit = rootView.findViewById(R.id.user_password_edit);

        fillInputs();

        return rootView;
    }

    private void fillInputs() {
        infoText.setText(String.format("Edit\n%s", username));
        usernameEdit.setText(username);
        emailEdit.setText(email);
    }
}
