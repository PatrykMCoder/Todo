package com.pmprogramms.todo.view.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTodo;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private ArrayList<JSONHelperTodo> arrayTodos;
    private LoadDataThread loadDataThread;
    private View rootView;
    private String userID;

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
        rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        todoList = rootView.findViewById(R.id.todoListRecyclerView);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);

        addNewTodo = rootView.findViewById(R.id.add_new_todo);
        userID = new UserData(context).getUserID();
        addNewTodo.setOnClickListener(this);
        loadDataThread = new LoadDataThread();
        loadDataThread.execute();

        todoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(dy <= 0);
            }
        });

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
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_todo) {
            mainActivity.initFragment(new AddNewTodoFragment(userID), true);
        }
    }

    class LoadDataThread extends AsyncTask<String, String, TaskState> {
        @Override
        protected TaskState doInBackground(String... strings) {
            if (userID != null) {
                if (arrayTodos != null) {
                    arrayTodos.clear();
                }
                APIClient apiClient = new APIClient();
                arrayTodos = apiClient.loadTitlesTodoUser(userID);
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        private void removeArchiveTodos() {
           ArrayList<JSONHelperTodo> loadTitles = new ArrayList<>();
               for (JSONHelperTodo obj : arrayTodos) {
                   if (!obj.archive)
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
                    if(arrayTodos != null) {
                        removeArchiveTodos();
                        initRecyclerView();
                    }
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, slide down to refresh data again");
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }
}