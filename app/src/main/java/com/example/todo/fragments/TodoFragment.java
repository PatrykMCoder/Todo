package com.example.todo.fragments;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.TodoRecyclerViewAdapter;
import com.example.todo.utils.objects.TodoObject;
import com.example.todo.utils.setteings.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoFragment extends Fragment implements View.OnClickListener {

    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodo;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addNewTodo;

    private ArrayList<TodoObject> data;
    private Context context;

    private TodoAdapter todoAdapter;
    private TodoObject todoObject;

    private MainActivity mainActivity;
    private static final String TAG = "TodoFragment";

    private ArrayList<Integer> colors = new ArrayList<>();

    private String sortData = "";

    public TodoFragment(){
        // Required empty public constructor
    }

    public TodoFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        loadSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        data = new ArrayList<>();
        addNewTodo = rootView.findViewById(R.id.add_new_todo);
        addNewTodo.setBackgroundColor(Color.argb(colors.get(0), colors.get(1), colors.get(2), colors.get(3)));
        addNewTodo.setOnClickListener(this);
        initRecyclerView(rootView);
        loadSettings();
        getDataToShow();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadSettings(){
        Settings settings = new Settings(context);
        colors = settings.loadBackgroundColor();
        sortData = settings.loadSortTodo();

        Log.d(TAG, "loadSettings: " + settings.loadSortTodo());
    }

    private void initRecyclerView(View v){
        todoList = v.findViewById(R.id.todoListRecyclerView);
        todoList.setNestedScrollingEnabled(false);
        todoList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);
        adapterTodo = new TodoRecyclerViewAdapter(data);
        todoList.setAdapter(adapterTodo);
    }

    private void getDataToShow(){
        TodoAdapter todoAdapter = new TodoAdapter(context);
        todoAdapter.setSort(sortData);
        todoAdapter.openDB();
        for(int i = 0; i < todoAdapter.getTitleTODO().size(); i++) {
            todoObject = new TodoObject(todoAdapter.getTitleTODO().get(i), todoAdapter.getDescriptionTODO().get(i), todoAdapter.getDoneTODO().get(i), "", "", todoAdapter.getArchiveTODO().get(i)); // TODO: 29/08/2019 repeat this filed for done
            data.add(todoObject);
        }

        Log.d(TAG, "getDataToShow: " + todoAdapter.getAllTodo());

        todoAdapter.closeDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_todo) {
            mainActivity.initFragment(new AddNewTodoFragment(), true);

        }
    }
}
