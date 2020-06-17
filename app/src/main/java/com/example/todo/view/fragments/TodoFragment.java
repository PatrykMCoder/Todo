package com.example.todo.view.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.TitleSearchHandle;
import com.example.todo.utils.TodoRecyclerViewAdapter;
import com.example.todo.view.fragments.search.SearchActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TodoFragment extends Fragment implements View.OnClickListener {

    private RecyclerView todoList;
    private RecyclerView.Adapter adapterTodoRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addNewTodo;

    private Context context;

    private MainActivity mainActivity;
    private static final String TAG = "TodoFragment";

    private View includeView;
    private EditText searchEditText;
    private ImageButton openSlideMenu;

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
        final View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        addNewTodo = rootView.findViewById(R.id.add_new_todo);

        includeView = rootView.findViewById(R.id.custom_app_bar);

        searchEditText = includeView.findViewById(R.id.search_label);
        openSlideMenu = includeView.findViewById(R.id.open_slide_menu);

        addNewTodo.setOnClickListener(this);

        searchEditText.setOnClickListener(this);
        openSlideMenu.setOnClickListener(this);

        initRecyclerView(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initRecyclerView(View v) {
        todoList = v.findViewById(R.id.todoListRecyclerView);
        todoList.setNestedScrollingEnabled(false);
        todoList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        todoList.setLayoutManager(layoutManager);
        adapterTodoRecyclerView = new TodoRecyclerViewAdapter(context);
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
        } else if (view.getId() == R.id.search_label) {
            Intent intent = new Intent(context, SearchActivity.class);
            intent.setAction(Intent.ACTION_SEARCH);
            startActivity(intent);
        } else if (view.getId() == R.id.open_slide_menu) {
        }
    }
}
