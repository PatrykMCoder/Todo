package com.pmprogramms.todo.view.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.strictmode.CleartextNetworkViolation;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.recyclerView.SearchRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private View includeView;
    private EditText searchEditText;
    private ImageButton backButton;

    private RecyclerView searchList;
    private RecyclerView.Adapter adapterSearchRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "SearchActivity";
    private MainActivity mainActivity;

    private ArrayList<Data> arrayTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        arrayTodos = new ArrayList<>();
        includeView = findViewById(R.id.custom_app_bar2);
        searchEditText = includeView.findViewById(R.id.search_label);
        backButton = includeView.findViewById(R.id.back);

        searchList = findViewById(R.id.search_container);

        getData();

        backButton.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    search(searchEditText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        search(searchEditText.getText().toString());
                    }
                    return true;
                }
            });
        }
    }

    private void getData() {
        API api = Client.getInstance().create(API.class);

        Call<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo> call = api.getUserTodosTitle(new UserData(SearchActivity.this).getUserID());
        call.enqueue(new Callback<JSONHelperTodo>() {
            @Override
            public void onResponse(Call<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo> call, Response<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(SearchActivity.this, "Something wrong, try again", Toast.LENGTH_LONG).show();
                }
                com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo jsonHelperTodo = response.body();
                arrayTodos = jsonHelperTodo.data;
            }

            @Override
            public void onFailure(Call<com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo> call, Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
        } else
            result.clear();

        displayResult(result);
    }

    private void displayResult(Set<Data> result) {
        adapterSearchRecyclerView = new SearchRecyclerViewAdapter(result);
        searchList.setAdapter(adapterSearchRecyclerView);
        adapterSearchRecyclerView.getItemCount();
        searchList.setNestedScrollingEnabled(false);
        searchList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        searchList.setLayoutManager(layoutManager);
    }
}