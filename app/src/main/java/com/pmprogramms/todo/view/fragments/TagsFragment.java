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


import com.pmprogramms.todo.API.retrofit.customTags.TagsData;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.databinding.FragmentTagsBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TagsRecyclerAdapter;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;

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

        fragmentTagsBinding.refreshSwipe.setOnRefreshListener(this::loadData);

        return fragmentTagsBinding.getRoot();
    }

    private void initRecyclerView(ArrayList<TagsData> tags) {
        TagsRecyclerAdapter tagsAdapter = new TagsRecyclerAdapter(tags);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
        fragmentTagsBinding.tagsRecyclerView.setLayoutManager(layoutManager);
        fragmentTagsBinding.tagsRecyclerView.setAdapter(tagsAdapter);
    }

    private void loadData() {
        String userToken = new UserData(requireContext()).getUserToken();

        todoNoteViewModel.getAllTags(userToken).observe(getViewLifecycleOwner(), tagsData -> {
            if(tagsData.data != null) {
                initRecyclerView(tagsData.data);
                fragmentTagsBinding.refreshSwipe.setRefreshing(false);
            }
        });
    }
}
