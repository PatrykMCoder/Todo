package com.example.todo;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.TodoRecyclerViewAdapter;
import com.example.todo.utils.objects.TodoObject;
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

    public TodoFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        getDataToShow();
        return rootView;
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
        todoAdapter.openDB();
        for(int i = 0; i < todoAdapter.getTitleTODO().size(); i++) {
            todoObject = new TodoObject(todoAdapter.getTitleTODO().get(i), todoAdapter.getDescriptionTODO().get(i), todoAdapter.getDoneTODO().get(i), "", "", todoAdapter.getArchiveTODO().get(i)); // TODO: 29/08/2019 repeat this filed for done
            data.add(todoObject);
        }

        todoAdapter.closeDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_new_todo:
                mainActivity.initFragment(new AddNewTodo(), true);
                break;
        }
    }
}
