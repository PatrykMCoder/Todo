package com.pmprogramms.todo.view.fragments.user;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pmprogramms.todo.LoginActivity;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentUserProfileBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.PolicyDialog;
import com.pmprogramms.todo.view.dialogs.SourceDialog;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private FragmentUserProfileBinding fragmentUserProfileBinding;
    private MainActivity mainActivity;
    private TodoNoteViewModel todoNoteViewModel;

    private String username;

    private ProgressBar progressBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentUserProfileBinding = FragmentUserProfileBinding.inflate(inflater);
        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        fragmentUserProfileBinding.buttonLogout.setOnClickListener(this);
        fragmentUserProfileBinding.editUserdataButton.setOnClickListener(this);

        fragmentUserProfileBinding.privacy.setOnClickListener(this);
        fragmentUserProfileBinding.openSource.setOnClickListener(this);
        fragmentUserProfileBinding.scrollViewProfile
                .setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> fragmentUserProfileBinding.swipeRefresh.setEnabled(scrollY == 0));

        fragmentUserProfileBinding.swipeRefresh.setOnRefreshListener(this::getData);

        progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyle);
        fragmentUserProfileBinding.getRoot().addView(progressBar);
        progressBar.setVisibility(View.VISIBLE);


        getData();

        return fragmentUserProfileBinding.getRoot();
    }

    private void getData() {
        fragmentUserProfileBinding.swipeRefresh.setRefreshing(false);
        String userToken = new UserData(requireContext()).getUserToken();
        todoNoteViewModel.getUserData(userToken).observe(getViewLifecycleOwner(), userData -> {
            progressBar.setVisibility(View.GONE);
            if (userData != null) {
                username = userData.data.username;

                updateUI();
            } else {
                new Messages(requireContext()).showMessage("Something wrong, try again");
            }
        });
    }

    private void updateUI() {
        if (username.length() > 15)
            username = username.substring(0, 15) + "...";

        fragmentUserProfileBinding.welcomeText.setText(String.format("Welcome\n%s", username));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_logout) {
            new UserData(requireContext()).removeUserToken();
            Intent intent = new Intent(mainActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.edit_userdata_button) {
            new Messages(requireContext()).showMessage("For now is blocking, please be patient for update this function");

        } else if (id == R.id.privacy) {
            DialogFragment dialogFragment = new PolicyDialog();
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "Policy dialog");
        } else if (id == R.id.open_source) {
            DialogFragment dialogFragment = new SourceDialog();
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "OpenSource dialog");
        }
    }
}
