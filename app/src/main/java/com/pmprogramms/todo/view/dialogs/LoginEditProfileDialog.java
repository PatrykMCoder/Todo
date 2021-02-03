package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUser;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.fragments.user.UserProfileFragment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginEditProfileDialog extends DialogFragment {
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

    public LoginEditProfileDialog(String userID, String username, String emailNew, String emailOld, @Nullable String password) {
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

                        HashMap<String, String> map = new HashMap<>();
                        map.put("email", emailOld);
                        map.put("password", pass);

                        API api = Client.getInstance().create(API.class);
                        Call<JsonHelperLogin> call = api.loginUser(map);
                        call.enqueue(new Callback<JsonHelperLogin>() {
                            @Override
                            public void onResponse(Call<JsonHelperLogin> call, Response<JsonHelperLogin> response) {
                                progressDialog.cancel();
                                if (!response.isSuccessful()) {
                                    new Messages(context).showMessage(response.message());
                                }

                                JsonHelperLogin helperLogin = response.body();
                                assert helperLogin != null;
                                if (helperLogin.code == 200) {
                                    callEditProfile();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonHelperLogin> call, Throwable t) {
                                progressDialog.cancel();
                                new Messages(context).showMessage(t.getMessage());
                            }
                        });
                    }
                });
        return builder.create();
    }

    private void callEditProfile() {
        HashMap<String, String> map = new HashMap<>();

        if (password != null) {
            map.put("username", username);
            map.put("email_old", emailOld);
            map.put("email_new", emailNew);
            map.put("password", password);
        } else {
            map.put("username", username);
            map.put("email_old", emailOld);
            map.put("email_new", emailNew);
            map.put("password", null);
        }

        API api = Client.getInstance().create(API.class);
        Call<JSONHelperUser> call = api.editProfile(userID, map);
        call.enqueue(new Callback<JSONHelperUser>() {
            @Override
            public void onResponse(Call<JSONHelperUser> call, Response<JSONHelperUser> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage(response.message());
                }

                if (response.code() == 200 || response.code() == 201) {
                    new Messages(context).showMessage("User updated");
                    mainActivity.initFragment(new UserProfileFragment(), false);
                } else {
                    new Messages(context).showMessage("Cannot update user, try again");
                }
            }

            @Override
            public void onFailure(Call<JSONHelperUser> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }
}
