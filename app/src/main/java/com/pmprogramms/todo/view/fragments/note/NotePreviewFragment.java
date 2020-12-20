package com.pmprogramms.todo.view.fragments.note;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.note.JSONHelperNote;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.DeleteNoteAskDialog;

import java.util.ArrayList;


public class NotePreviewFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private MainActivity mainActivity;

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView titleTextView, contentsTextView, lastEditedTextView;
    private FloatingActionMenu menuButton;
    private FloatingActionButton deleteNoteButton;
    private FloatingActionButton editNoteButton;
    private FloatingActionButton archiveNoteButton;
    private CardView menuCardView;
    private ScrollView scrollView;

    private String userID;
    private String noteID;
    private boolean archive;
    private boolean tmpArchive;
    private ArrayList<JSONHelperNote> jsonHelperNote;

    public NotePreviewFragment(String userID, String noteID) {
        this.noteID = noteID;
        this.userID = userID;

        LoadNoteThread loadNoteThread = new LoadNoteThread();
        loadNoteThread.execute();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note_preview, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        titleTextView = view.findViewById(R.id.title_preview);
        contentsTextView = view.findViewById(R.id.contents_preview);
        lastEditedTextView = view.findViewById(R.id.last_edited);
        deleteNoteButton = view.findViewById(R.id.delete_note);
        editNoteButton = view.findViewById(R.id.edit_note);
        archiveNoteButton = view.findViewById(R.id.archive_note);
        scrollView = view.findViewById(R.id.note_scroll);
        menuButton = view.findViewById(R.id.menu);
        menuCardView = view.findViewById(R.id.menu_card);

        deleteNoteButton.setOnClickListener(this);
        editNoteButton.setOnClickListener(this);
        archiveNoteButton.setOnClickListener(this);

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipeRefreshLayout.setEnabled(scrollY == 0);
            if (scrollY > oldScrollY) {
                if (menuCardView.getVisibility() != View.INVISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_down);
                    menuCardView.setVisibility(View.INVISIBLE);
                    menuButton.setVisibility(View.INVISIBLE);
                    menuCardView.startAnimation(animation);
                }
            } else if (scrollY < oldScrollY) {
                if (menuCardView.getVisibility() != View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_up);
                    menuCardView.setVisibility(View.VISIBLE);
                    menuButton.setVisibility(View.VISIBLE);
                    menuCardView.startAnimation(animation);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            LoadNoteThread loadNoteThread = new LoadNoteThread();
            loadNoteThread.execute();
        });

        return view;
    }

    private void getDataToShow() {
        titleTextView.setText(jsonHelperNote.get(0).title);
        contentsTextView.setText(jsonHelperNote.get(0).contents);
        lastEditedTextView.setText(new TextFormat().formatForTextLastEdit(mainActivity, jsonHelperNote.get(0).updatedAt));

        tmpArchive = archive = jsonHelperNote.get(0).archive;
        archiveNoteButton.setImageResource(archive ? R.drawable.ic_baseline_unarchive_24 : R.drawable.ic_archive_white_24dp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.delete_note) {
            DialogFragment deleteNoteFragment = new DeleteNoteAskDialog(context, mainActivity, this, titleTextView.getText().toString().trim(), userID, noteID);
            deleteNoteFragment.show(mainActivity.getSupportFragmentManager(), "Delete note");
        } else if (id == R.id.edit_note) {
            mainActivity.initFragment(new EditNoteFragment(titleTextView.getText().toString().trim(), contentsTextView.getText().toString(), userID, noteID), true);
        } else if (id == R.id.archive_note) {
            ArchiveActionAsync archiveActionAsync = new ArchiveActionAsync();
            archiveActionAsync.execute();
        }
    }

    class LoadNoteThread extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient apiClient = new APIClient();
            jsonHelperNote = apiClient.loadNotePreview(userID, noteID);

            return TaskState.DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            swipeRefreshLayout.setRefreshing(false);

            switch (state) {
                case DONE: {
                    if (jsonHelperNote != null) {
                        getDataToShow();
                    }
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, try again");
                    break;
                }
                case DONE_NOT_TO_UI: {
                    break;
                }
            }
        }
    }

    class ArchiveActionAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient();
            int code = APIClient.archiveNodeAction(userID, noteID, !archive);
            if (code == 200 || code == 201)
                return TaskState.DONE;
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    new Messages(context).showMessage(tmpArchive ? "Unarchive" : "Archive");
                    archiveNoteButton.setImageResource(archive ? R.drawable.ic_archive_white_24dp : R.drawable.ic_baseline_unarchive_24);
                    archive = !tmpArchive;
                    tmpArchive = archive;
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Something wrong, try again");
                    break;
                }
            }
        }
    }
}
