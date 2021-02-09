package com.pmprogramms.todo.view.fragments;

import android.content.Context;
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

import com.pmprogramms.todo.API.jsonhelper.JSONHelperCustomTags;
import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUser;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.API.retrofit.customTags.TagsData;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.recyclerView.TagsRecyclerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        loadData();
        initRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        return rootView;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(false);
        API api = Client.getInstance().create(API.class);
        Call<JsonHelperTag> call = api.getUserTagData(new UserData(context).getUserID());
        call.enqueue(new Callback<JsonHelperTag>() {
            @Override
            public void onResponse(Call<JsonHelperTag> call, Response<JsonHelperTag> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage(response.message());
                    return;
                }
                JsonHelperTag responseBody = response.body();
                tags = new ArrayList<>();
                tagsID = new ArrayList<>();
                if(responseBody != null) {
                    for (TagsData tag : responseBody.data) {
                        tags.add(tag.tag_name);
                        tagsID.add(tag._id);
                        initRecyclerView();
                    }
                }else {
                    new Messages(context).showMessage("Something wrong, try again");
                }
            }

            @Override
            public void onFailure(Call<JsonHelperTag> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }
}
