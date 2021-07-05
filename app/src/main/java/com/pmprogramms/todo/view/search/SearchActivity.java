package com.pmprogramms.todo.view.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.databinding.ActivitySearchBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.recyclerView.SearchRecyclerViewAdapter;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterSearchRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ActivitySearchBinding activitySearchBinding;
    private TodoNoteViewModel todoNoteViewModel;

    private ArrayList<Data> arrayTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySearchBinding = ActivitySearchBinding.inflate(getLayoutInflater());

        setContentView(activitySearchBinding.getRoot());

        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        getData();

        activitySearchBinding.searchBar.backButton.setOnClickListener(v -> onBackPressed());


        activitySearchBinding.searchBar.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(activitySearchBinding.searchBar.searchEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activitySearchBinding.searchBar.searchEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                search(activitySearchBinding.searchBar.searchEditText.getText().toString());
            }
            return true;
        });

    }

    private void getData() {
        arrayTodos = new ArrayList<>();
        String userToken = new UserData(getApplicationContext()).getUserToken();
        todoNoteViewModel.getAllTodos(false, userToken).observe(this, data -> {
            if (data != null) {
                arrayTodos.addAll(data.data);
            }
        });

        todoNoteViewModel.getAllTodos(true, userToken).observe(this, data -> {
            if (data != null) {
                arrayTodos.addAll(data.data);
            }
        });
    }

    private void search(String querySearch) {
        Set<Data> result = new HashSet<>();

        if (!querySearch.isEmpty()) {
            for (Data t : arrayTodos) {
                if (t.title.toLowerCase().contains(querySearch.toLowerCase())) {
                    result.add(t);
                }
            }
        }
        displayResult(result);
    }

    private void displayResult(Set<Data> result) {
        adapterSearchRecyclerView = new SearchRecyclerViewAdapter(result);
        activitySearchBinding.searchRecycler.setAdapter(adapterSearchRecyclerView);
        activitySearchBinding.searchRecycler.setNestedScrollingEnabled(false);
        activitySearchBinding.searchRecycler.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        activitySearchBinding.searchRecycler.setLayoutManager(layoutManager);
    }
}