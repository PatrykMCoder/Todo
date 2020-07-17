package com.example.todo.view.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todo.API.MongoDBClient;
import com.example.todo.API.jsonhelper.JSONHelperLoadTitles;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.user.UserData;
import com.example.todo.helpers.view.HideAppBarHelper;
import com.example.todo.utils.Messages;
import com.example.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TodoArchiveFragment extends Fragment {
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodoRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<JSONHelperLoadTitles> arrayTodos;
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
        rootView = inflater.inflate(R.layout.fragment_archive_todo, null);

        todoList = rootView.findViewById(R.id.todoListRecyclerView);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            LoadDataThread loadDataThread = new LoadDataThread();
            loadDataThread.execute();
        });

        todoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(dy <= 0);
            }
        });

        LoadDataThread loadDataThread = new LoadDataThread();
        loadDataThread.execute();

        return rootView;
    }

    private void initRecyclerView() {
        adapterTodoRecyclerView = new TodoRecyclerViewAdapter(context, arrayTodos, userID);
        todoList.swapAdapter(adapterTodoRecyclerView, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    class LoadDataThread extends AsyncTask<String, String, TaskState> {
        @Override
        protected TaskState doInBackground(String... strings) {
            String userID = context.getSharedPreferences("user_data", Context.MODE_PRIVATE).getString("user_id", null);
            if (userID != null) {
                if (arrayTodos != null) {
                    arrayTodos.clear();
                }
                MongoDBClient mongoDBClient = new MongoDBClient();
                Gson gson = new Gson();
                arrayTodos = gson.fromJson(mongoDBClient.loadTitlesTodoUser(userID), new TypeToken<ArrayList<JSONHelperLoadTitles>>() {
                }.getType());
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        private void removeNotArchiveTodos() {
            ArrayList<JSONHelperLoadTitles> loadTitles = new ArrayList<>();
            for (JSONHelperLoadTitles obj : arrayTodos) {
                if (obj.archive)
                    loadTitles.add(obj);
            }

            arrayTodos.clear();
            arrayTodos = loadTitles;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);

            switch (state) {
                case DONE: {
                    removeNotArchiveTodos();
                    initRecyclerView();
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, try again");
                    break;
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
