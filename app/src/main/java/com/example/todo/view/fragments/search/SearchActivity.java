package com.example.todo.view.fragments.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.utils.loader.LoaderDatabases;
import com.example.todo.utils.recyclerView.SearchRecyclerViewAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        includeView = findViewById(R.id.custom_app_bar2);
        searchEditText = includeView.findViewById(R.id.search_label);
        backButton = includeView.findViewById(R.id.back);

        searchList = findViewById(R.id.search_container);


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
        ArrayList<String> titles = new LoaderDatabases(this).loadTitles();
        Set<String> result = new HashSet<>();

        if (!querySearch.isEmpty()) {
            for (String t : titles) {
                t = t.replace("_", " ");
                /*
                * should be better result <- todo
                * */
                if (t.contains(querySearch.toLowerCase())) {
                    result.add(t);
                }
            }
        } else
            result.clear();

        displayResult(result);
    }

    private void displayResult(Set<String> result) {
        adapterSearchRecyclerView = new SearchRecyclerViewAdapter(result);
        searchList.setAdapter(adapterSearchRecyclerView);
        adapterSearchRecyclerView.getItemCount();
        searchList.setNestedScrollingEnabled(false);
        searchList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        searchList.setLayoutManager(layoutManager);
    }
}