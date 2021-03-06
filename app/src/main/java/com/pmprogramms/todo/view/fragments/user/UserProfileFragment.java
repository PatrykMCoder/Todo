package com.pmprogramms.todo.view.fragments.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.user.JsonHelperUser;
import com.pmprogramms.todo.LoginActivity;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.PolicyDialog;
import com.pmprogramms.todo.view.dialogs.SourceDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private MainActivity mainActivity;
    private ScrollView scrollView;
    private TextView welcomeUserTextView, pricacyTextView, openSourceTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button logoutButton;
    private ImageButton editUserDataButton;

    private Context context;
    private String userToken;
    private JsonHelperUser userObject;
    private ProgressDialog progressDialog;

    private String username;
    private String email;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        this.context = context;
        new HideAppBarHelper(mainActivity).hideBar();
        userToken = new UserData(context).getUserToken();
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
            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> swipeRefreshLayout.setEnabled(scrollY == 0));
        }

        getData();

        swipeRefreshLayout.setOnRefreshListener(this::getData);
        return rootView;
    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(false);
        API api = Client.getInstance().create(API.class);
        Call<JsonHelperUser> call = api.getUserData(userToken);
        call.enqueue(new Callback<JsonHelperUser>() {
            @Override
            public void onResponse(Call<JsonHelperUser> call, Response<JsonHelperUser> response) {
                progressDialog.cancel();
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage(response.message());
                    return;
                }
                userObject = response.body();
                if (userObject != null) {
                    email = userObject.data.email;
                    username = userObject.data.username;
                }
                updateUI(username);
            }

            @Override
            public void onFailure(Call<JsonHelperUser> call, Throwable t) {
                progressDialog.dismiss();
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }

    private void updateUI(String username) {
        if (username.length() > 15)
            username = username.substring(0, Math.min(username.length(), 15)) + "...";

        welcomeUserTextView.setText(String.format("Welcome\n%s", username));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_logout) {
            new UserData(context).removeUserToken();
            Intent intent = new Intent(mainActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.edit_userdata_button) {
            mainActivity.initFragment(new UserProfileEditFragment(userToken, username, email), true);
        } else if (id == R.id.privacy) {
            DialogFragment dialogFragment = new PolicyDialog();
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "Policy dialog");
        } else if (id == R.id.open_source) {
            DialogFragment dialogFragment = new SourceDialog();
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "OpenSource dialog");
        }
    }
}
