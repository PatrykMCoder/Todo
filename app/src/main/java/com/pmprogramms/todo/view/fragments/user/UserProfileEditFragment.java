package com.pmprogramms.todo.view.fragments.user;

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

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.text.TextHelper;
import com.pmprogramms.todo.view.dialogs.LoginEditProfileDialog;

public class UserProfileEditFragment extends Fragment {
    private View rootView;

    private String userToken;
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
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        this.context = context;
    }

    public UserProfileEditFragment() {

    }

    public UserProfileEditFragment(String userToken, String username, String email) {
        this.userToken = userToken;
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

                if (!emailEdit.getText().toString().trim().equals("") && !usernameEdit.getText().toString().trim().equals("")) {
                    if (new TextHelper().emailValid(emailEdit.getText().toString())) {
                        emailNew = emailEdit.getText().toString().trim();
                        username = usernameEdit.getText().toString().trim();
                        password = passwordEdit.getText().toString();

                        if (emailOld.equals(emailNew)) {
                            emailNew = null;
                        }
                        LoginEditProfileDialog loginEditProfileDialog;
                        if (password.equals(""))
                            loginEditProfileDialog = new LoginEditProfileDialog(username, emailNew, emailOld, userToken, null);
                        else
                            loginEditProfileDialog = new LoginEditProfileDialog(username, emailNew, emailOld, userToken, password);

                        loginEditProfileDialog.show(getFragmentManager(), "login require");
                    } else {
                        new Messages(context).showMessage("Invalid email");
                    }
                } else {
                    new Messages(context).showMessage("Email and username can't be empty!");
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
