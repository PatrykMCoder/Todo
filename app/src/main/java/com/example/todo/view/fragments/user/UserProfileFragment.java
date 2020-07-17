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
import com.example.todo.utils.Messages;
import com.example.todo.view.dialogs.PolicyDialog;
import com.example.todo.view.dialogs.SourceDialog;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private MainActivity mainActivity;
    private ScrollView scrollView;
    private TextView welcomeUserTextView, pricacyTextView, openSourceTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button logoutButton;
    private ImageButton editUserDataButton;

    private Context context;
    private String userID;
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
        pricacyTextView = rootView.findViewById(R.id.privacy);
        openSourceTextView = rootView.findViewById(R.id.open_source);
        editUserDataButton = rootView.findViewById(R.id.edit_userdata_button);

        scrollView = rootView.findViewById(R.id.scroll_view_profile);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        logoutButton = rootView.findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(this);
        editUserDataButton.setOnClickListener(this);

        pricacyTextView.setOnClickListener(this);
        openSourceTextView.setOnClickListener(this);

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

    private void updateUI(String username) {
        if (username.length() > 15)
            username = username.substring(0, Math.min(username.length(), 15)) + "...";

        welcomeUserTextView.setText(String.format("Welcome\n%s", username));
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
            case R.id.edit_userdata_button: {
                mainActivity.initFragment(new UserProfileEditFragment(userID, username, email), true);
                break;
            }

            case R.id.privacy: {
                DialogFragment dialogFragment = new PolicyDialog();
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "Policy dialog");
                break;
            }

            case R.id.open_source: {
                DialogFragment dialogFragment = new SourceDialog();
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "OpenSource dialog");
                break;
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
                    updateUI(username);
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, try again. Slide down to refresh");
                    break;
                }
            }
        }
    }
}
