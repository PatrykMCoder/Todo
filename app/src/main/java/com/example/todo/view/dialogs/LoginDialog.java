package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.LoginActivity;
import com.example.todo.MainActivity;
import com.example.todo.service.MongoDBClient;
import com.example.todo.view.fragments.user.UserProfileEditFragment;
import com.example.todo.view.fragments.user.UserProfileFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LoginDialog extends DialogFragment {
    private ProgressDialog progressDialog;
    private MainActivity mainActivity;

    private String userID;
    private String username;
    private String emailNew;
    private String emailOld;
    private String password = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    public LoginDialog(String userID, String username, String emailNew, String emailOld, @Nullable String password) {
        this.userID = userID;
        this.username = username;
        this.emailNew = emailNew;
        this.emailOld = emailOld;
        this.password = password;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        EditText password = new EditText(getContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        password.setLayoutParams(layoutParams);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password.setHint("Password(old)");
        builder.setView(password);

        builder.setTitle("Password required")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pass = password.getText().toString();
                progressDialog = ProgressDialog.show(getContext(), "Please wait", "");
                new LoginThread(emailOld, pass).execute();
            }
        });
        return builder.create();
    }
    class LoginThread extends AsyncTask<String, String, String> {
        private String email;
        private String password;
        private ArrayList<Object> data;

        public LoginThread(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... str) {
            MongoDBClient mongoDBClient = new MongoDBClient(email, password);
            data = mongoDBClient.loginUser();

            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(data != null && ((int)data.get(0) == 200 || (int)data.get(0) == 201)) {
                EditUserProfileAsync editUserProfileAsync = new EditUserProfileAsync();
                editUserProfileAsync.execute();
            }else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, "Bad password, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public class EditUserProfileAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            int code;
            MongoDBClient mongoDBClient = new MongoDBClient();
            if (password != null) {
                code = mongoDBClient.editUserProfile(userID, username, emailOld, emailNew, password);
            } else {
                code = mongoDBClient.editUserProfile(userID, username, emailOld, emailNew, null);
            }

            if (code == 200 || code == 201) {
                return "done";
            }
            return "notDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("done")) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, "Updated", Toast.LENGTH_SHORT).show();
                    }
                });

                mainActivity.initFragment(new UserProfileFragment(), false);
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
