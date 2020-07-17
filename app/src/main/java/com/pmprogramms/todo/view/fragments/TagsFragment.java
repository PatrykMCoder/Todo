package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperCustomTags;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.recyclerView.TagsRecyclerAdapter;

import java.util.ArrayList;

public class TagsFragment extends Fragment {
    private MainActivity mainActivity;

    private RecyclerView tagsList;
    private RecyclerView.Adapter tagsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    private ArrayList<JSONHelperCustomTags> helperLoadCustomTagsArrayList;
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

            APIClient APIClient = new APIClient();
            helperLoadCustomTagsArrayList = APIClient.loadUserTags(new UserData(context).getUserID());
            if (helperLoadCustomTagsArrayList != null)
                return TaskState.DONE;
            else
                return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    tags = new ArrayList<>();
                    tagsID = new ArrayList<>();
                    for (JSONHelperCustomTags tag : helperLoadCustomTagsArrayList) {
                        tags.add(tag.tag_name);
                        tagsID.add(tag._id);
                        initRecyclerView();
                    }
                    break;

                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, try again");
                    break;
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
