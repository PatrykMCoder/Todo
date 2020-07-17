package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.MainActivity;
import com.example.todo.API.MongoDBClient;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.utils.Messages;
import com.example.todo.view.fragments.user.UserProfileFragment;

import java.util.ArrayList;

public class LoginDialog extends DialogFragment {
    private ProgressDialog progressDialog;
    private MainActivity mainActivity;

    private String userID;
    private String username;
    private String emailNew;
    private String emailOld;
    private String password = "";
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        this.context = context;
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
        password.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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

    class LoginThread extends AsyncTask<String, String, TaskState> {
        private String email;
        private String password;
        private ArrayList<Object> data;

        public LoginThread(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected TaskState doInBackground(String... str) {
            MongoDBClient mongoDBClient = new MongoDBClient(email, password);
            data = mongoDBClient.loginUser();

            return TaskState.DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            progressDialog.dismiss();

            if (data != null && ((int) data.get(0) == 200 || (int) data.get(0) == 201)) {
                EditUserProfileAsync editUserProfileAsync = new EditUserProfileAsync();
                editUserProfileAsync.execute();
            } else {
                new Messages(context).showMessage("Bad password, try again");
            }
        }
    }

    public class EditUserProfileAsync extends AsyncTask<String, String, TaskState> {
        @Override
        protected TaskState doInBackground(String... strings) {
            int code;
            MongoDBClient mongoDBClient = new MongoDBClient();
            if (password != null) {
                code = mongoDBClient.editUserProfile(userID, username, emailOld, emailNew, password);
            } else {
                code = mongoDBClient.editUserProfile(userID, username, emailOld, emailNew, null);
            }

            if (code == 200 || code == 201) {
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);

            switch (state) {
                case DONE: {
                    new Messages(context).showMessage("User updated");
                    mainActivity.initFragment(new UserProfileFragment(), false);
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot update user, try again");
                    break;
                }
            }
        }
    }
}
