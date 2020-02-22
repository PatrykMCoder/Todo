package com.example.todo.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.database.TodoAdapterV2;
import com.example.todo.utils.TodoRecyclerViewAdapter;
import com.example.todo.utils.TodoRecyclerViewAdapterV2;
import com.example.todo.utils.objects.TodoObject;
import com.example.todo.utils.setteings.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoFragment extends Fragment implements View.OnClickListener {

    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodoRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addNewTodo;

    private ArrayList<TodoObject> data;
    private Context context;

    private TodoAdapter todoAdapter;
    private TodoObject todoObject;

    private MainActivity mainActivity;
    private static final String TAG = "TodoFragment";

    public TodoFragment(){
        // Required empty public constructor
    }

    public TodoFragment(Context context) {
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
        final View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        data = new ArrayList<>();
        addNewTodo = rootView.findViewById(R.id.add_new_todo);
        addNewTodo.setOnClickListener(this);
        initRecyclerView(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initRecyclerView(View v){
        todoList = v.findViewById(R.id.todoListRecyclerView);
        todoList.setNestedScrollingEnabled(false);
        todoList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);
        adapterTodoRecyclerView = new TodoRecyclerViewAdapterV2(context);
        adapterTodoRecyclerView.getItemCount();
        todoList.setAdapter(adapterTodoRecyclerView);
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
