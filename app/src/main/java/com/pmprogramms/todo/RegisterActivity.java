package com.pmprogramms.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.text.TextHelper;
import com.pmprogramms.todo.view.dialogs.PolicyDialog;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        infoText.setOnClickListener(v -> {
            DialogFragment dialogFragment = new PolicyDialog();
            dialogFragment.show(getSupportFragmentManager(), "Policy dialog");
        });

        createUserButton.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(RegisterActivity.this, "Create user...", "Please wait...");            new HideKeyboard(v, RegisterActivity.this).hide();

            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String passwordConfirm = confirmPasswordEditText.getText().toString();

            if (acceptCheckbox.isChecked()) {
                if (!username.isEmpty() && !email.isEmpty() && password.length() >= 8) {
                    if (new TextHelper().emailValid(email)) {
                        if (passwordConfirm.equals(password)) {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("email", email);
                            map.put("password", password);
                            map.put("username", username);
                            map.put("accept_privacy", acceptCheckbox.isChecked());
                            API api = Client.getInstance().create(API.class);
                            Call<Void> call = api.createUser(map);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    progressDialog.cancel();
                                    if (!response.isSuccessful()) {
                                        new Messages(RegisterActivity.this).showMessage(response.message());
                                    }

                                    if (response.code() == 200 || response.code() == 201) {
                                        new Messages(RegisterActivity.this).showMessage("User created!😊");
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        new Messages(RegisterActivity.this).showMessage("Cannot create user, try again");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    progressDialog.cancel();
                                    new Messages(RegisterActivity.this).showMessage(t.getMessage());
                                }
                            });
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
        });
    }
}