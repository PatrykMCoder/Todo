package com.example.todo.view.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.service.MongoDBClient;
import com.example.todo.service.jsonhelper.JSONHelperLoadTitles;
import com.example.todo.utils.loader.LoaderDatabases;
import com.example.todo.utils.recyclerView.SearchRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private View includeView;
    private EditText searchEditText;
    private ImageButton backButton;

    private RecyclerView searchList;
    private RecyclerView.Adapter adapterSearchRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "SearchActivity";
    private MainActivity mainActivity;

    private ArrayList<JSONHelperLoadTitles> arrayTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        arrayTodos = new ArrayList<>();
        includeView = findViewById(R.id.custom_app_bar2);
        searchEditText = includeView.findViewById(R.id.search_label);
        backButton = includeView.findViewById(R.id.back);

        searchList = findViewById(R.id.search_container);

        LoadTitlesAsync titlesAsync = new LoadTitlesAsync();
        titlesAsync.execute();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

    private void search(String querySearch) {
        Set<JSONHelperLoadTitles> result = new HashSet<>();

        if (!querySearch.isEmpty()) {
            for (JSONHelperLoadTitles t : arrayTodos) {
                if (t.title.contains(querySearch.toLowerCase())) {
                    result.add(t);
                }
            }
        } else
            result.clear();

        displayResult(result);
    }

    private void displayResult(Set<JSONHelperLoadTitles> result) {
        adapterSearchRecyclerView = new SearchRecyclerViewAdapter(result);
        searchList.setAdapter(adapterSearchRecyclerView);
        adapterSearchRecyclerView.getItemCount();
        searchList.setNestedScrollingEnabled(false);
        searchList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        searchList.setLayoutManager(layoutManager);
    }


    class LoadTitlesAsync extends AsyncTask<String, String, String> {
        String userID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userID = getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", "");
        }

        @Override
        protected String doInBackground(String... strings) {
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
        }
    }
}