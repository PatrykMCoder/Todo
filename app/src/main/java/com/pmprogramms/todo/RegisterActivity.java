package com.pmprogramms.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.text.TextHelper;
import com.pmprogramms.todo.view.dialogs.PolicyDialog;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, usernameEditText, confirmPasswordEditText;
    private Button createUserButton;
    private CheckBox acceptCheckbox;
    private TextView infoText;
    private RelativeLayout rootView;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rootView = findViewById(R.id.root_view);
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createUserButton = findViewById(R.id.button_login);
        acceptCheckbox = findViewById(R.id.accept_checkbox);
        infoText = findViewById(R.id.info_text_view);

        infoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new PolicyDialog();
                dialogFragment.show(getSupportFragmentManager(), "Policy dialog");
            }
        });

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HideKeyboard(v, RegisterActivity.this).hide();

                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String passwordConfirm = confirmPasswordEditText.getText().toString();

                if (acceptCheckbox.isChecked()) {
                    if (!username.isEmpty() && !email.isEmpty() && password.length() >= 8) {
                        if (new TextHelper().emailValid(email)) {
                            if (passwordConfirm.equals(password)) {
                                progressDialog = ProgressDialog.show(RegisterActivity.this, "Create account", "Please wait...");
                                RegisterThread registerThread = new RegisterThread(username, email, password);
                                registerThread.execute();
                            } else {
                                new Messages(getApplicationContext()).showMessage("Password are not this same");
                            }
                        } else {
                            new Messages(getApplicationContext()).showMessage("Invalid email");
                        }
                    } else {
                        new Messages(getApplicationContext()).showMessage("Filed cannot be empty or password length should be 8 characters!");
                    }
                } else {
                    new Messages(getApplicationContext()).showMessage("Please accept privacy policy!");
                }
            }
        });
    }

    class RegisterThread extends AsyncTask<String, String, TaskState> {
        private String username, password, email;
        private boolean accept;

        RegisterThread(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
            accept = acceptCheckbox.isChecked();
        }

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient(username, email, password);
            int code = APIClient.createUser(accept);

            if (code == 201 || code == 200)
                return TaskState.DONE;

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            progressDialog.dismiss();

            switch (state) {
                case DONE: {
                    new Messages(getApplicationContext()).showMessage("User created!😊");
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }
                case NOT_DONE: {
                    new Messages(getApplicationContext()).showMessage("Cannot create user, try again");
                    break;
                }
            }
        }
    }
}