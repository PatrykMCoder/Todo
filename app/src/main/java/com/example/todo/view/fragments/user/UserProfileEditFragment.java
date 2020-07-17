package com.example.todo.view.fragments.user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.input.HideKeyboard;
import com.example.todo.utils.text.TextHelper;
import com.example.todo.view.dialogs.LoginDialog;
import com.google.android.material.snackbar.Snackbar;

public class UserProfileEditFragment extends Fragment {
    private View rootView;

    private String userID;
    private String username;
    private String emailNew;
    private String emailOld;
    private String password = "";

    private TextView infoText;
    private EditText emailEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button editButton;

    private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public UserProfileEditFragment() {

    }

    public UserProfileEditFragment(String userID, String username, String email) {
        this.userID = userID;
        this.username = username;
        emailOld = email;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_progile_user_edit, null);
        infoText = rootView.findViewById(R.id.info_text);
        emailEdit = rootView.findViewById(R.id.user_email_edit);
        usernameEdit = rootView.findViewById(R.id.user_name_edit);
        passwordEdit = rootView.findViewById(R.id.user_password_edit);
        editButton = rootView.findViewById(R.id.button_submit);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HideKeyboard(v, mainActivity).hide();

                if (!(emailEdit.getText().toString().trim().equals("") && usernameEdit.getText().toString().trim().equals(""))) {
                    if (new TextHelper().emailValid(emailEdit.getText().toString())) {
                        emailNew = emailEdit.getText().toString().trim();
                        username = usernameEdit.getText().toString().trim();
                        password = passwordEdit.getText().toString();

                        if (emailOld.equals(emailNew)) {
                            emailNew = null;
                        }
                        LoginDialog loginDialog;
                        if (password.equals(""))
                            loginDialog = new LoginDialog(userID, username, emailNew, emailOld, null);
                        else
                            loginDialog = new LoginDialog(userID, username, emailNew, emailOld, password);

                        loginDialog.show(getFragmentManager(), "login requirde");
                    } else {
                        Snackbar.make(rootView, "Invalid email", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(rootView, "Email and username can't be empty!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        fillInputs();

        return rootView;
    }

    private void fillInputs() {
        infoText.setText(String.format("Edit\n%s", username));
        usernameEdit.setText(username);
        emailEdit.setText(emailOld);
    }
}
