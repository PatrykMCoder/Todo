package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.todo.helpers.input.HideKeyboard;
import com.example.todo.helpers.user.UserData;
import com.example.todo.API.MongoDBClient;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.utils.Messages;
import com.example.todo.utils.text.TextHelper;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private TextView registerText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RelativeLayout rootView;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootView = findViewById(R.id.root_view);
        registerText = findViewById(R.id.open_register_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.button_login);

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HideKeyboard(v, LoginActivity.this).hide();

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                if (new TextHelper().emailValid(email)) {
                    progressDialog = ProgressDialog.show(LoginActivity.this, "Login...", "Please wait...");
                    LoginThread loginThread = new LoginThread(email, password);
                    loginThread.execute();
                } else
                    new Messages(getApplicationContext()).showMessage("Invalid email");
            }
        });
    }

    class LoginThread extends AsyncTask<String, String, TaskState> {
        private String email;
        private String password;

        public LoginThread(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected TaskState doInBackground(String... str) {
            MongoDBClient mongoDBClient = new MongoDBClient(email, password);
            ArrayList<Object> data = mongoDBClient.loginUser();

            if (data != null && (int) data.get(0) == 200) {
                new UserData(LoginActivity.this).setUserID(data.get(1).toString());
                return TaskState.DONE;
            }

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            progressDialog.dismiss();
            switch (state) {
                case DONE: {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }
                case NOT_DONE: {
                    new Messages(getApplicationContext()).showMessage("Wrong user data, try again");
                    break;
                }
            }
        }
    }
}