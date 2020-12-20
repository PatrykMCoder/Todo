package com.pmprogramms.todo.view.search;

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

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTodo;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.recyclerView.SearchRecyclerViewAdapter;

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

    private ArrayList<JSONHelperTodo> arrayTodos;

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
        Set<JSONHelperTodo> result = new HashSet<>();

        if (!querySearch.isEmpty()) {
            for (JSONHelperTodo t : arrayTodos) {
                if (t.title.toLowerCase().contains(querySearch.toLowerCase())) {
                    result.add(t);
                }
            }
        } else
            result.clear();

        displayResult(result);
    }

    private void displayResult(Set<JSONHelperTodo> result) {
        adapterSearchRecyclerView = new SearchRecyclerViewAdapter(result);
        searchList.setAdapter(adapterSearchRecyclerView);
        adapterSearchRecyclerView.getItemCount();
        searchList.setNestedScrollingEnabled(false);
        searchList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        searchList.setLayoutManager(layoutManager);
    }


    class LoadTitlesAsync extends AsyncTask<String, String, TaskState> {
        String userID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userID = new UserData(SearchActivity.this).getUserID();
        }

        @Override
        protected TaskState doInBackground(String... strings) {
            if (userID != null) {
                if (arrayTodos != null) {
                    arrayTodos.clear();
                }
                APIClient APIClient = new APIClient();
                arrayTodos = APIClient.loadTitlesTodoUser(userID);
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);

            switch (state){
                case DONE: break;
                case NOT_DONE: {
                    new Messages(getApplicationContext()).showMessage("Something wrong, try again");
                    break;
                }
            }
        }
    }
}