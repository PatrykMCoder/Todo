package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmprogramms.todo.LoginActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;

public class SessionDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Session expired")
                .setMessage("Your session expired, please login again")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("OK", (dialog, which) -> {
                    new UserData(requireContext()).removeUserToken();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        return builder.create();
    }
}
