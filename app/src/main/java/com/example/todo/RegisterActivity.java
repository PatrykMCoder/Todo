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
import android.widget.Toast;

import com.example.todo.service.MongoDBClient;
import com.example.todo.service.MongodbHelper;
import com.example.todo.service.Operation;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, usernameEditText, confirmPasswordEditText;
    private Button createUserButton;

    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createUserButton = findViewById(R.id.button_login);
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create validation
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String passwordConfirm = confirmPasswordEditText.getText().toString();

                if (!username.isEmpty() && !email.isEmpty() && password.length() >= 8) {
                    if (passwordConfirm.equals(password)) {
                        progressDialog = ProgressDialog.show(RegisterActivity.this, "Create account", "Please wait...");
                        RegisterThread registerThread = new RegisterThread(username, email, password);
                        registerThread.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Password are not this same or ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Filed cannot be empty or password length should be 8 characters!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class RegisterThread extends AsyncTask<String, String, String> {
        private String username, password, email;

        RegisterThread(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient(username, email, password);
            int code = mongoDBClient.createUser();

            if (code == 201)
                return "Done";

            return "NotDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Handler handler = new Handler();
            if (s.equals("Done")) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "User created!😊", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.dismiss();
            }
        }
    }
}