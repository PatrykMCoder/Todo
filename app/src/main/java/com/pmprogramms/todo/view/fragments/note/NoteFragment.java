package com.pmprogramms.todo.view.fragments.note;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.note.Data;
import com.pmprogramms.todo.API.retrofit.note.JSONNoteHelper;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.NoteRecyclerViewAdapter;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteFragment extends Fragment implements View.OnClickListener {
    private RecyclerView noteList;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton addNewNote;

    private MainActivity mainActivity;
    private Context context;
    private String userToken;

    public NoteFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);

        noteList = rootView.findViewById(R.id.note_list_recycler_view);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_swipe);
        addNewNote = rootView.findViewById(R.id.add_new_note);

        addNewNote.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        noteList.setLayoutManager(layoutManager);

        getData();

        swipeRefreshLayout.setOnRefreshListener(this::getData);
        return rootView;
    }

    private void getData() {
        API api = Client.getInstance().create(API.class);
        userToken = new UserData(context).getUserToken();
        Call<JSONNoteHelper> call = api.getUserNoteTitles(userToken);
        call.enqueue(new Callback<JSONNoteHelper>() {
            @Override
            public void onResponse(Call<JSONNoteHelper> call, Response<JSONNoteHelper> response) {
                if (response.isSuccessful()) {
                    JSONNoteHelper jsonNoteHelper = response.body();
                    ArrayList<Data> unArchiveData = new ArrayList<>();
                    for (Data data : jsonNoteHelper.data) {
                        if (!data.archive)
                            unArchiveData.add(data);
                    }

                    initRecyclerView(unArchiveData);
                } else
                    new Messages(context).showMessage(response.message());

            }

            @Override
            public void onFailure(Call<JSONNoteHelper> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    private void initRecyclerView(ArrayList<Data> data) {
        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(context, data, userToken);
        noteList.swapAdapter(noteRecyclerViewAdapter, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_new_note) {
            mainActivity.initFragment(new AddNewNoteFragment(), true);
        }
    }
}
