package com.example.todo.view.fragments.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todo.LoginActivity;
import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.user.UserData;
import com.example.todo.helpers.view.HideAppBarHelper;
import com.example.todo.API.MongoDBClient;
import com.example.todo.API.jsonhelper.JSONHelperUser;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.view.dialogs.PolicyDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private MainActivity mainActivity;
    private ScrollView scrollView;
    private RelativeLayout userWelcomeLayout;
    private TextView welcomeUserTextView, usernameTextView, emailTextView, pricacyTextView;
    private FrameLayout frameLayout;
    private ImageView waveImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button logoutButton;
    private ImageButton editButtonPassword, editButtonEmail, editButtonUserName;

    private Context context;
    private String userID;
    private ArrayList<JSONHelperUser> userData;
    private JSONHelperUser userObject;
    private ProgressDialog progressDialog;

    private String username;
    private String email;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        this.context = context;
        new HideAppBarHelper(mainActivity).hideBar();
        userID = new UserData(context).getUserID();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_profile, null);

        welcomeUserTextView = rootView.findViewById(R.id.welcome_text);
        usernameTextView = rootView.findViewById(R.id.user_name);
        emailTextView = rootView.findViewById(R.id.user_email);
        pricacyTextView = rootView.findViewById(R.id.privacy);

        scrollView = rootView.findViewById(R.id.scroll_view_profile);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        logoutButton = rootView.findViewById(R.id.button_logout);
        editButtonEmail = rootView.findViewById(R.id.edit_email);
        editButtonPassword = rootView.findViewById(R.id.edit_password);
        editButtonUserName = rootView.findViewById(R.id.edit_username);

        logoutButton.setOnClickListener(this);
        editButtonUserName.setOnClickListener(this);
        editButtonPassword.setOnClickListener(this);
        editButtonEmail.setOnClickListener(this);
        pricacyTextView.setOnClickListener(this);

        progressDialog = ProgressDialog.show(context, "Loading data", "Please wait...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    swipeRefreshLayout.setEnabled(scrollY == 0);
                }
            });
        }

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

    private void updateUI(String username, String email) {
        if (username.length() > 15)
            username = username.substring(0, Math.min(username.length(), 15)) + "...";
        if (email.length() >= 15)
            email = email.substring(0, Math.min(email.length(), 15)) + "...";

        welcomeUserTextView.setText(String.format("Welcome\n%s", username));
        usernameTextView.setText(String.format("Username: %s", username));
        emailTextView.setText(String.format("Email: %s", email));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_logout: {
                new UserData(context).removeUserID();
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case R.id.edit_email:
            case R.id.edit_username:
            case R.id.edit_password: {
                mainActivity.initFragment(new UserProfileEditFragment(userID, username, email), true);
                break;
            }

            case R.id.privacy: {
                DialogFragment dialogFragment = new PolicyDialog();
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "Policy dialog");
            }
        }
    }

    class LoadUserDataAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            String data = mongoDBClient.loadDataUser(userID);
            if (data != null) {
                Gson gson = new Gson();
                userObject = gson.fromJson(data, JSONHelperUser.class);
                return userObject != null ? TaskState.DONE : TaskState.NOT_DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            swipeRefreshLayout.setRefreshing(false);
            if (progressDialog != null)
                progressDialog.dismiss();

            switch (state) {
                case DONE: {
                    email = userObject.email;
                    username = userObject.username;
                    updateUI(username, email);
                    break;
                }
                case NOT_DONE: {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(rootView, "Something wrong, try again. Slide down to refresh", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    break;
                }
            }
        }
    }
}
