package com.pmprogramms.todo.view.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.todo.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoFragment extends Fragment implements View.OnClickListener {

    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodoRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addNewTodo;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Context context;

    private MainActivity mainActivity;
    private static final String TAG = "TodoFragment";
    private ArrayList<JSONHelperTodo> arrayTodos;
    //    private LoadDataThread loadDataThread;
    private View rootView;
    private String userID;

    private API api;

    public TodoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        api = Client.getInstance().create(API.class);

        rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        todoList = rootView.findViewById(R.id.todoListRecyclerView);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);

        addNewTodo = rootView.findViewById(R.id.add_new_todo);
        userID = new UserData(context).getUserID();
        addNewTodo.setOnClickListener(this);

        getData();

        todoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(dy <= 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::getData);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    private void getData() {
        Call<JSONHelperTodo> call = api.getUserTodosTitle(userID);
        call.enqueue(new Callback<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo>() {
            @Override
            public void onResponse(Call<JSONHelperTodo> call, Response<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Something wrong, try again", Toast.LENGTH_LONG).show();
                }
                JSONHelperTodo jsonHelperTodo = response.body();
                if (jsonHelperTodo!=null) {
                    ArrayList<Data> todosData = jsonHelperTodo.data;
                    ArrayList<Data> unarchiveData = new ArrayList<>();

                    for (Data t : todosData) {
                        if (!t.archive)
                            unarchiveData.add(t);
                    }

                    initRecyclerView(unarchiveData);
                } else
                    new Messages(context).showMessage("Try again");
            }

            @Override
            public void onFailure(Call<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecyclerView(ArrayList<Data> data) {
        adapterTodoRecyclerView = new TodoRecyclerViewAdapter(context, data, userID);
        todoList.swapAdapter(adapterTodoRecyclerView, true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_todo) {
            mainActivity.initFragment(new AddNewTodoFragment(userID), true);
        }
    }
}
