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

import com.pmprogramms.todo.databinding.FragmentArchiveTodoBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

public class TodoArchiveFragment extends Fragment {

    private FragmentArchiveTodoBinding fragmentArchiveTodoBinding;
    private TodoNoteViewModel todoNoteViewModel;
    private String userToken;

    private Context context;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        userToken = new UserData(context).getUserToken();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentArchiveTodoBinding = FragmentArchiveTodoBinding.inflate(inflater);
        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        fragmentArchiveTodoBinding.todoListRecyclerView.setLayoutManager(layoutManager);

        fragmentArchiveTodoBinding.refreshSwipe.setOnRefreshListener(this::getData);

        fragmentArchiveTodoBinding.todoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fragmentArchiveTodoBinding.refreshSwipe.setEnabled(dy <= 0);
            }
        });

        getData();

        return fragmentArchiveTodoBinding.getRoot();
    }

    private void getData() {
        todoNoteViewModel.getAllTodos(true, userToken).observe(getViewLifecycleOwner(), todos -> {
            if (todos != null) {
                TodoRecyclerViewAdapter adapterTodoRecyclerView = new TodoRecyclerViewAdapter(todos.data);
                fragmentArchiveTodoBinding.todoListRecyclerView.setAdapter(adapterTodoRecyclerView);
            }
        });
    }
}
