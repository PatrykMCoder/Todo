package com.pmprogramms.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.text.TextHelper;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SAVE = 100000, RC_HINT = 100001;
    private TextView registerText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RelativeLayout rootView;
    private CredentialsClient credentialsClient;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        credentialsClient = Credentials.getClient(this);
        setContentView(R.layout.activity_login);

        rootView = findViewById(R.id.root_view);
        registerText = findViewById(R.id.open_register_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.button_login);

        registerText.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        loginButton.setOnClickListener(v -> {
            new HideKeyboard(v, LoginActivity.this).hide();
            if (emailEditText.getText().toString().equals("") && passwordEditText.getText().toString().equals("")) {
                new Messages(this).showMessage("Please enter email and password");
                return;
            }
        
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("password", password);

            if (new TextHelper().emailValid(email)) {
                progressDialog = ProgressDialog.show(LoginActivity.this, "Login...", "Please wait...");
                API api = Client.getInstance().create(API.class);
                Call<JsonHelperLogin> call = api.loginUser(map);
                call.enqueue(new Callback<JsonHelperLogin>() {
                    @Override
                    public void onResponse(Call<JsonHelperLogin> call, Response<JsonHelperLogin> response) {
                        progressDialog.dismiss();
                        if (!response.isSuccessful()) {
                            new Messages(LoginActivity.this).showMessage(response.message());
                        }
                        JsonHelperLogin loginHelper = response.body();

                        if (loginHelper != null && (response.code() == 200 || response.code() == 201)) {
                            new UserData(LoginActivity.this).setUserToken(loginHelper.data.token);
                            saveUserCredential(email, password);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            new Messages(LoginActivity.this).showMessage("Something wrong, try again");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonHelperLogin> call, Throwable t) {
                        new Messages(LoginActivity.this).showMessage(t.getMessage());
                    }
                });
            } else
                new Messages(getApplicationContext()).showMessage("Invalid email");
        });
    }

    public void saveUserCredential(String email, String password) {
        Credential credential = new Credential.Builder(email)
                .setPassword(password)
                .build();


        credentialsClient.save(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                new Messages(this).showMessage("Credentials save");
            }
            Exception e = task.getException();
            if (e instanceof ResolvableApiException) {
                ResolvableApiException rae = (ResolvableApiException) e;
                try {
                    rae.startResolutionForResult(this, RC_SAVE);

                } catch (IntentSender.SendIntentException exception) {
                    Log.e(LoginActivity.class.toString(), "Failed to send resolution.", exception);
                    new Messages(this).showMessage("Credentials save failed");
                }
            } else
                new Messages(this).showMessage("Credentials save failed");
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                new Messages(this).showMessage("Credentials save");
            } else {
                Log.e(LoginActivity.class.toString(), "SAVE: Canceled by user");
            }
        }
    }
}