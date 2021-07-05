package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentArchiveTodoBinding;
import com.pmprogramms.todo.helpers.api.SessionHelper;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.SessionDialog;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoArchiveFragment extends Fragment {

    private FragmentArchiveTodoBinding fragmentArchiveTodoBinding;
    private TodoNoteViewModel todoNoteViewModel;
    private String userToken;

    private Context context;


    @Override
    public void onAttach(Context context) {
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
            if (todos != null)
                initRecyclerView(todos.data);
        });
    }

    private void initRecyclerView(ArrayList<Data> dataTodo) {
        TodoRecyclerViewAdapter adapterTodoRecyclerView = new TodoRecyclerViewAdapter(dataTodo);
        fragmentArchiveTodoBinding.todoListRecyclerView.setAdapter(adapterTodoRecyclerView);
    }
}
