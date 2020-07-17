package com.example.todo.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todo.LoginActivity;
import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.HideAppBarHelper;
import com.example.todo.service.MongoDBClient;
import com.example.todo.service.jsonhelper.JSONHelperUser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private MainActivity mainActivity;
    private ScrollView scrollView;
    private RelativeLayout userWelcomeLayout;
    private TextView welcomeUserTextView, usernameTextView, emailTextView;
    private FrameLayout frameLayout;
    private ImageView waveImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button logoutButton;

    private Context context;
    private String userID;
    private ArrayList<JSONHelperUser> userData;
    private JSONHelperUser userObject;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferencesUserData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        this.context = context;
        new HideAppBarHelper(mainActivity).hideBar();
        sharedPreferencesUserData = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        userID = sharedPreferencesUserData.getString("user_id", null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_profile, null);

        welcomeUserTextView = rootView.findViewById(R.id.welcome_text);
        usernameTextView = rootView.findViewById(R.id.user_name);
        emailTextView = rootView.findViewById(R.id.user_email);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        logoutButton = rootView.findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(this);
        progressDialog = ProgressDialog.show(context, "Loading data", "Please wait...");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadUserDataAsync loadUserDataAsync = new LoadUserDataAsync();
                loadUserDataAsync.execute();
            }
        });

        LoadUserDataAsync loadUserDataAsync = new LoadUserDataAsync();
        loadUserDataAsync.execute();
        return rootView;
    }

    private void updateUI(String username, String email){
        if (username.length() > 15)
            username = username.substring(0, Math.min(username.length(), 15)) + "...";
        if(email.length() >= 15)
            email = email.substring(0, Math.min(email.length(), 15)) + "...";

        welcomeUserTextView.setText(String.format("Welcome\n%s", username));
        usernameTextView.setText(String.format("Username: %s", username));
        emailTextView.setText(String.format("Email: %s", email));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_logout: {
                sharedPreferencesUserData.edit().putStringSet("user_id", null).apply();
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
        }
    }

    class LoadUserDataAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            String data = mongoDBClient.loadDataUser(userID);
            if(data != null){
                Gson gson = new Gson();
                userObject = gson.fromJson(data, JSONHelperUser.class);
                return userObject != null ? "done" : "notDone";
            }
            return "notDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);
            if(progressDialog != null)
                progressDialog.dismiss();
            if (s.equals("done")) {
                updateUI(userObject.username, userObject.email);
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
