package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.databinding.FragmentTagsBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TagsRecyclerAdapter;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

public class TagsFragment extends Fragment {
    private MainActivity mainActivity;
    private TodoNoteViewModel todoNoteViewModel;
    private FragmentTagsBinding fragmentTagsBinding;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTagsBinding = FragmentTagsBinding.inflate(inflater);

        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        mainActivity.setSupportActionBar(fragmentTagsBinding.customToolBar);
        loadData();

        fragmentTagsBinding.refresh.setOnRefreshListener(this::loadData);

        return fragmentTagsBinding.getRoot();
    }

    private void loadData() {
        String userToken = new UserData(requireContext()).getUserToken();

        todoNoteViewModel.getAllTags(userToken).observe(getViewLifecycleOwner(), tagsData -> {
            if(tagsData.data != null) {
                TagsRecyclerAdapter tagsAdapter = new TagsRecyclerAdapter(tagsData.data);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
                fragmentTagsBinding.recycler.setLayoutManager(layoutManager);
                fragmentTagsBinding.recycler.setAdapter(tagsAdapter);
                fragmentTagsBinding.refresh.setRefreshing(false);
            }
        });
    }
}
