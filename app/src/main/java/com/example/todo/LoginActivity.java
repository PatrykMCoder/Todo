package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.service.MongoDBClient;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private TextView registerText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //todo add checking if userid is exist on device. if existing go to list with todos, else please login

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
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                progressDialog = ProgressDialog.show(LoginActivity.this, "Login...", "Please wait...");
                LoginThread loginThread = new LoginThread(email, password);
                loginThread.execute();
            }
        });
    }

    class LoginThread extends AsyncTask<String, String, String> {
        private String email;
        private String password;

        public LoginThread(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... str) {
            MongoDBClient mongoDBClient = new MongoDBClient(email, password);
            int code = mongoDBClient.loginUser();

            if (code == 200)
                return "Done";

            return "NotDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s.equals("Done")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.dismiss();
            }
        }
    }
}