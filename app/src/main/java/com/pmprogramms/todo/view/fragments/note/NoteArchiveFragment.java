package com.pmprogramms.todo.view.fragments.note;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.note.Data;
import com.pmprogramms.todo.API.retrofit.note.JSONNoteHelper;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.recyclerView.NoteRecyclerViewAdapter;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteArchiveFragment extends Fragment {
    private Context context;
    private MainActivity mainActivity;

    private View view;
    private RecyclerView noteList;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String userToken;

    public NoteArchiveFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        mainActivity = (MainActivity) context;
        userToken = new UserData(context).getUserToken();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_archive_note, container, false);

        noteList = view.findViewById(R.id.note_list_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.refresh_swipe);

        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        noteList.setLayoutManager(layoutManager);

        getData();

        swipeRefreshLayout.setOnRefreshListener(this::getData);

        return view;
    }

    private void getData() {
        API api = Client.getInstance().create(API.class);

        Call<JSONNoteHelper> call = api.getUserNoteTitles(userToken);
        call.enqueue(new Callback<JSONNoteHelper>() {
            @Override
            public void onResponse(Call<JSONNoteHelper> call, Response<JSONNoteHelper> response) {
                if (response.isSuccessful()) {
                    JSONNoteHelper jsonNoteHelper = response.body();
                    ArrayList<Data> archiveData = new ArrayList<>();
                    for (Data data : jsonNoteHelper.data) {
                        if (data.archive)
                            archiveData.add(data);
                    }

                    initRecyclerView(archiveData);
                } else
                    new Messages(context).showMessage(response.message());

            }

            @Override
            public void onFailure(Call<JSONNoteHelper> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }

    private void initRecyclerView(ArrayList<Data> data) {
        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(context, data, userToken);
        noteList.swapAdapter(noteRecyclerViewAdapter, true);
    }
}
