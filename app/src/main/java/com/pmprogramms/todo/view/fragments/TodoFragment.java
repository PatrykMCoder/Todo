package com.pmprogramms.todo.view.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentTodoBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;

public class TodoFragment extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    private String userToken;
    private TodoNoteViewModel todoNoteViewModel;
    private FragmentTodoBinding fragmentTodoBinding;

    public TodoFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        fragmentTodoBinding = FragmentTodoBinding.inflate(inflater);
        fragmentTodoBinding.addNewTodo.setOnClickListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        fragmentTodoBinding.todoListRecyclerView.setLayoutManager(layoutManager);

        fragmentTodoBinding.todoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fragmentTodoBinding.refreshSwipe.setEnabled(dy <= 0);
            }
        });

        fragmentTodoBinding.refreshSwipe.setOnRefreshListener(this::getData);

        getData();

        return fragmentTodoBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    private void getData() {
        userToken = new UserData(requireContext()).getUserToken();
        todoNoteViewModel.getAllTodos(userToken).observe(getViewLifecycleOwner(), jsonHelperTodo -> {
            if (jsonHelperTodo != null) {
                Log.i(TodoFragment.class.toString(), String.valueOf(jsonHelperTodo.data.size()));
                initRecyclerView(jsonHelperTodo.data);
            }
        });
    }


    private void initRecyclerView(ArrayList<Data> data) {
        RecyclerView.Adapter adapterTodoRecyclerView = new TodoRecyclerViewAdapter(requireContext(), data, userToken);
        fragmentTodoBinding.todoListRecyclerView.swapAdapter(adapterTodoRecyclerView, true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_todo) {
            NavDirections navDirections = TodoFragmentDirections.actionTodoFragmentToAddNewTodoFragment(userToken);
            Navigation.findNavController(view).navigate(navDirections);
        }
    }
}
