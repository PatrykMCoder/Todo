package com.example.todo.view.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.HideAppBarHelper;
import com.example.todo.service.MongoDBClient;
import com.example.todo.service.jsonhelper.JSONHelperLoadDataTodo;
import com.example.todo.service.jsonhelper.JSONHelperLoadTitles;
import com.example.todo.utils.TodoRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TodoFragment extends Fragment implements View.OnClickListener {

    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodoRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addNewTodo;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Context context;

    private MainActivity mainActivity;
    private static final String TAG = "TodoFragment";
    private ArrayList<JSONHelperLoadTitles> arrayTodos;
    private LoadDataThread loadDataThread;
    private View rootView;
    private String userID;

    public TodoFragment() {
        // Required empty public constructor
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
        rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        todoList = rootView.findViewById(R.id.todoListRecyclerView);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);
        addNewTodo = rootView.findViewById(R.id.add_new_todo);
        userID = context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("user_id", null);
        addNewTodo.setOnClickListener(this);
        loadDataThread = new LoadDataThread();
        loadDataThread.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataThread = new LoadDataThread();
                loadDataThread.execute();

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    private void initRecyclerView() {
        adapterTodoRecyclerView = new TodoRecyclerViewAdapter(context, arrayTodos, userID);
        todoList.swapAdapter(adapterTodoRecyclerView, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_todo) {
            mainActivity.initFragment(new AddNewTodoFragment(userID), true);
        }
    }

    class LoadDataThread extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String userID = context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("user_id", null);
            if (userID != null) {
                if (arrayTodos != null) {
                    arrayTodos.clear();
                }
                MongoDBClient mongoDBClient = new MongoDBClient();
                Gson gson = new Gson();
                arrayTodos = gson.fromJson(mongoDBClient.loadTitlesTodoUser(userID), new TypeToken<ArrayList<JSONHelperLoadTitles>>() {
                }.getType());
                return "done";
            }
            return "notDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);
            if (s.equals("done")) {
                initRecyclerView();
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
