package com.pmprogramms.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.databinding.ActivityRegisterBinding;
import com.pmprogramms.todo.helpers.forms.RegisterFieldForm;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.PolicyDialog;
import com.pmprogramms.todo.viewmodel.RegistrationViewModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding activityRegisterBinding;
    private RegistrationViewModel registrationViewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegisterBinding.getRoot());

        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        activityRegisterBinding.containerPrivacy.setOnClickListener(view -> {
            DialogFragment dialogFragment = new PolicyDialog();
            dialogFragment.show(getSupportFragmentManager(), "Policy dialog");
        });

        activityRegisterBinding.buttonLogin.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(RegisterActivity.this, "Create user...", "Please wait...");
            new HideKeyboard(v, RegisterActivity.this).hide();

            String username = activityRegisterBinding.usernameEditText.getText().toString().trim();
            String email = activityRegisterBinding.emailEditText.getText().toString().trim();
            String password = activityRegisterBinding.passwordEditText.getText().toString();
            String repeatPassword = activityRegisterBinding.confirmPasswordEditText.getText().toString();
            boolean acceptedPrivacy = activityRegisterBinding.acceptCheckbox.isChecked();

            RegisterFieldForm registerFieldForm = new RegisterFieldForm(email, password, repeatPassword, username, acceptedPrivacy);

            registrationViewModel.validationRegisterForm(registerFieldForm).observe(this, resultOfValidation -> {
                progressDialog.dismiss();

                if (resultOfValidation.getMsg().equals("")) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("email", email);
                    userDataMap.put("password", password);
                    userDataMap.put("username", username);
                    userDataMap.put("accept_privacy", acceptedPrivacy);

                    registrationViewModel.createUser(userDataMap).observe(this, createUserResult -> {
                        if (createUserResult != null) {
                            if (createUserResult == 201) {
                                new Messages(RegisterActivity.this).showMessage("User created!😊");
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Snackbar.make(activityRegisterBinding.getRoot(), "Can't create user, try again", Snackbar.LENGTH_LONG)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .show();
                            }
                        }
                    });

                } else {
                    Snackbar.make(activityRegisterBinding.getRoot(), resultOfValidation.getMsg(), Snackbar.LENGTH_LONG)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .show();
                }
            });
        });
    }
}