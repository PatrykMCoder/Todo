package com.pmprogramms.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.material.snackbar.Snackbar;
import com.pmprogramms.todo.databinding.ActivityLoginBinding;
import com.pmprogramms.todo.helpers.forms.LoginFieldForm;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.viewmodel.LoginViewModel;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SAVE = 200;
    private LoginFieldForm loginFieldForm;
    private CredentialsClient credentialsClient;

    private ActivityLoginBinding activityLoginBinding;
    private LoginViewModel loginViewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        credentialsClient = Credentials.getClient(this);
        setContentView(activityLoginBinding.getRoot());


        activityLoginBinding.buttonRegister.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        activityLoginBinding.buttonLogin.setOnClickListener(v -> {
            progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
            activityLoginBinding.getRoot().addView(progressBar);
            progressBar.setVisibility(View.VISIBLE);
            new HideKeyboard(v, LoginActivity.this).hide();

            loginFieldForm = new LoginFieldForm(activityLoginBinding.emailEditText.getText().toString(),
                    activityLoginBinding.passwordEditText.getText().toString().trim());

            loginViewModel.validLoginForm(loginFieldForm).observe(this, resultValid -> {
                if (resultValid.getMsg().equals("")) {
                    HashMap<String, String> authDataMap = new HashMap<>();
                    authDataMap.put("email", loginFieldForm.getEmail());
                    authDataMap.put("password", loginFieldForm.getPassword());

                    loginViewModel.loginUser(authDataMap).observe(this, resultLogin -> {
                        if (resultLogin != null) {
                            if (resultLogin.data.auth) {
                                new UserData(this).setUserToken(resultLogin.data.token);
                                saveUserCredential();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else
                                Snackbar.make(activityLoginBinding.getRoot(), "Can't login, try again", Snackbar.LENGTH_LONG)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .show();
                        } else
                            Snackbar.make(activityLoginBinding.getRoot(), "Can't login, try again", Snackbar.LENGTH_LONG)
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .show();

                    });
                } else {
                    Snackbar.make(activityLoginBinding.getRoot(), resultValid.getMsg(), Snackbar.LENGTH_LONG)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show();
                }
                progressBar.setVisibility(View.GONE);
            });
        });
    }

    public void saveUserCredential() {
        Credential credential = new Credential.Builder(loginFieldForm.getEmail())
                .setPassword(loginFieldForm.getPassword())
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
            }
        }
    }
}