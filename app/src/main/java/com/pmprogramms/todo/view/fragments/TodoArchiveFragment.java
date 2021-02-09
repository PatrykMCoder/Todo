package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.todo.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoArchiveFragment extends Fragment {
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodoRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<JSONHelperTodo> arrayTodos;
    private API api;
    private String userID;

    private Context context;
    private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
        userID = new UserData(context).getUserID();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        api = Client.getInstance().create(API.class);
        getData();
        rootView = inflater.inflate(R.layout.fragment_archive_todo, null);

        todoList = rootView.findViewById(R.id.todoListRecyclerView);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);
        swipeRefreshLayout.setOnRefreshListener(this::getData);

        todoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(dy <= 0);
            }
        });

        return rootView;
    }

    private void getData() {
        Call<JSONHelperTodo> call = api.getUserTodosTitle(userID);
        call.enqueue(new Callback<JSONHelperTodo>() {
            @Override
            public void onResponse(Call<JSONHelperTodo> call, Response<JSONHelperTodo> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Something wrong, try again", Toast.LENGTH_LONG).show();
                }
                JSONHelperTodo jsonHelperTodo = response.body();
                ArrayList<Data> todosData = jsonHelperTodo.data;
                ArrayList<Data> archiveData = new ArrayList<>();

                for (Data t : todosData) {
                    if (t.archive)
                        archiveData.add(t);
                }

                initRecyclerView(archiveData);
            }

            @Override
            public void onFailure(Call<JSONHelperTodo> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecyclerView(ArrayList<Data> dataTodo) {
        adapterTodoRecyclerView = new TodoRecyclerViewAdapter(context, dataTodo, userID);
        todoList.swapAdapter(adapterTodoRecyclerView, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }
}
