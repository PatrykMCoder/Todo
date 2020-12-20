package com.pmprogramms.todo.view.fragments.note;

import android.content.Context;
import android.os.AsyncTask;
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
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.note.JSONHelperNote;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.NoteRecyclerViewAdapter;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

public class NoteFragment extends Fragment implements View.OnClickListener {
    private RecyclerView noteList;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton addNewNote;

    private MainActivity mainActivity;
    private Context context;
    private String userID;
    private ArrayList<JSONHelperNote> arrayNotes;

    public NoteFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
        userID = new UserData(context).getUserID();
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

        LoadTitlesThread loadTitlesThread = new LoadTitlesThread();
        loadTitlesThread.execute();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            LoadTitlesThread loadTitlesThread1 = new LoadTitlesThread();
            loadTitlesThread1.execute();
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    private void initRecyclerView() {
        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(context, arrayNotes, userID);
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

    class LoadTitlesThread extends AsyncTask<String, String, TaskState> {
        @Override
        protected TaskState doInBackground(String... strings) {
            if (userID != null) {
                if (arrayNotes != null) {
                    arrayNotes.clear();
                }
                APIClient apiClient = new APIClient();
                arrayNotes = apiClient.loadTitlesNoteUser(userID);
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        private void removeArchiveTodos() {
            ArrayList<JSONHelperNote> loadTitles = new ArrayList<>();
            for (JSONHelperNote obj : arrayNotes) {
                if (!obj.archive)
                    loadTitles.add(obj);
            }

            arrayNotes.clear();
            arrayNotes = loadTitles;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);

            switch (state) {
                case DONE: {
                    if (arrayNotes != null) {
                        removeArchiveTodos();
                        initRecyclerView();
                    }
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, slide down to refresh data again");
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
