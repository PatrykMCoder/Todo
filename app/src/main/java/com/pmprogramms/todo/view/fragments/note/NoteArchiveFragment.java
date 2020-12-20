package com.pmprogramms.todo.view.fragments.note;

import android.content.Context;
import android.os.AsyncTask;
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

import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.note.JSONHelperNote;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.recyclerView.NoteRecyclerViewAdapter;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

public class NoteArchiveFragment extends Fragment {
    private Context context;
    private MainActivity mainActivity;

    private View view;
    private RecyclerView noteList;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String userID;
    private ArrayList<JSONHelperNote> arrayNotes;

    public NoteArchiveFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        mainActivity = (MainActivity) context;
        userID = new UserData(context).getUserID();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_archive_note, container, false);

        noteList = view.findViewById(R.id.note_list_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.refresh_swipe);

        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        noteList.setLayoutManager(layoutManager);

        LoadTitlesThread loadTitlesThread = new LoadTitlesThread();
        loadTitlesThread.execute();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            LoadTitlesThread loadTitlesThread2 = new LoadTitlesThread();
            loadTitlesThread2.execute();
        });

        return view;
    }

    private void initRecyclerView() {
        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(context, arrayNotes, userID);
        noteList.swapAdapter(noteRecyclerViewAdapter, true);
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

        private void removeNoArchiveTodos() {
            ArrayList<JSONHelperNote> loadTitles = new ArrayList<>();
            for (JSONHelperNote obj : arrayNotes) {
                if (obj.archive)
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
                        removeNoArchiveTodos();
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
