package com.example.todo.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todo.API.MongoDBClient;
import com.example.todo.API.jsonhelper.JSONHelperLoadCustomTags;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.user.UserData;
import com.example.todo.helpers.view.HideAppBarHelper;
import com.example.todo.utils.recyclerView.ReminderRecyclerAdapter;
import com.example.todo.utils.recyclerView.TagsRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TagsFragment extends Fragment {
    private MainActivity mainActivity;

    private RecyclerView tagsList;
    private RecyclerView.Adapter tagsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    private ArrayList<JSONHelperLoadCustomTags> helperLoadCustomTagsArrayList;
    private ArrayList<String> tags;
    private ArrayList<String> tagsID;

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
        new HideAppBarHelper(mainActivity).hideBar();
    }

    private void initRecyclerView() {
        tagsAdapter = new TagsRecyclerAdapter(context, tags, tagsID);

        layoutManager = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
        tagsList.setLayoutManager(layoutManager);

        tagsList.swapAdapter(tagsAdapter, true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tags, null);
        tagsList = rootView.findViewById(R.id.tags_recycler_view);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);
        toolbar = rootView.findViewById(R.id.custom_tool_bar);

        mainActivity.setSupportActionBar(toolbar);

        LoadCustomTagsAsync loadCustomTagsAsync = new LoadCustomTagsAsync();
        loadCustomTagsAsync.execute();

        initRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            LoadCustomTagsAsync loadCustomTagsAsync1 = new LoadCustomTagsAsync();
            loadCustomTagsAsync1.execute();
        });

        return rootView;
    }

    class LoadCustomTagsAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            if (helperLoadCustomTagsArrayList != null)
                helperLoadCustomTagsArrayList.clear();

            MongoDBClient mongoDBClient = new MongoDBClient();
            String data = mongoDBClient.loadUserTags(new UserData(context).getUserID());
            if (data != null) {
                Gson gson = new Gson();
                helperLoadCustomTagsArrayList = gson.fromJson(data, new TypeToken<ArrayList<JSONHelperLoadCustomTags>>() {
                }.getType());
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    if (helperLoadCustomTagsArrayList != null) {
                        tags = new ArrayList<>();
                        tagsID = new ArrayList<>();
                        for (JSONHelperLoadCustomTags tag : helperLoadCustomTagsArrayList) {
                            tags.add(tag.tag_name);
                            tagsID.add(tag._id);
                            initRecyclerView();
                        }
                        break;
                    }
                }
                case NOT_DONE: {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(rootView, "Something wrong, try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
