package com.pmprogramms.todo.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pmprogramms.todo.API.jsonhelper.JSONHelperLastEdit;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTag;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperEditTodo;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperDataTodo;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.reminders.ReminderHelper;
import com.pmprogramms.todo.view.dialogs.CreateReminderDialog;
import com.pmprogramms.todo.view.dialogs.DeleteTodoAskDialog;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Map;

public class TodoDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private String todoID;
    private String tag;
    private String title;
    private String userID;
    private boolean archive;
    private boolean tmpArchive;

    private LinearLayout box;
    private TextView titleTextView;
    private TextView taskTextView;
    private CheckBox doneCheckBox;
    private TextView tagView;
    private TextView lastEditedView;
    private ImageView reminderStatusImageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardView menuCardView;
    private ScrollView scrollView;


    private FloatingActionMenu floatingActionMenu;
    private com.github.clans.fab.FloatingActionButton createReminderFAB;
    private com.github.clans.fab.FloatingActionButton editFAB;
    private com.github.clans.fab.FloatingActionButton archiveFAB;
    private com.github.clans.fab.FloatingActionButton deleteFAB;

    public Context context;
    private MainActivity mainActivity;

    private View rootView;

    private SharedPreferences remindersTitlePreference;
    private ArrayList<JSONHelperDataTodo> arrayData;
    private ArrayList<JSONHelperEditTodo> dataHelper;
    private int tmpPosition;

    private String dateUpdatedAt;

    public TodoDetailsFragment() {

    }

    public TodoDetailsFragment(String userID, String todoID, String title, boolean archive) {
        this.todoID = todoID;
        this.title = title;
        this.userID = userID;
        this.archive = archive;
        tmpArchive = archive;
        LoadTasksThread loadTasksThread = new LoadTasksThread();
        loadTasksThread.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_todo_details, container, false);

        titleTextView = rootView.findViewById(R.id.title_preview);
        tagView = rootView.findViewById(R.id.tag);
        lastEditedView = rootView.findViewById(R.id.last_edited);
        reminderStatusImageView = rootView.findViewById(R.id.reminder_status);

        menuCardView = rootView.findViewById(R.id.menu_card);
        scrollView = rootView.findViewById(R.id.scroll_view);

        box = rootView.findViewById(R.id.box);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        floatingActionMenu = rootView.findViewById(R.id.menu);
        createReminderFAB = rootView.findViewById(R.id.create_reminder);
        editFAB = rootView.findViewById(R.id.editTODO);
        archiveFAB = rootView.findViewById(R.id.archiveTODO);
        deleteFAB = rootView.findViewById(R.id.deleteTODO);

        editFAB.setOnClickListener(this);
        archiveFAB.setOnClickListener(this);
        deleteFAB.setOnClickListener(this);
        createReminderFAB.setOnClickListener(this);
        tagView.setOnClickListener(this);

        archiveFAB.setImageResource(archive ? R.drawable.ic_baseline_unarchive_24 : R.drawable.ic_archive_white_24dp);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            box.removeAllViews();
            LoadTasksThread loadTasksThread = new LoadTasksThread();
            loadTasksThread.execute();
        });

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipeRefreshLayout.setEnabled(scrollY == 0);
            if (scrollY > oldScrollY) {
                if (menuCardView.getVisibility() != View.INVISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_down);
                    menuCardView.setVisibility(View.INVISIBLE);
                    menuCardView.startAnimation(animation);
                }
            } else if (scrollY < oldScrollY) {
                if (menuCardView.getVisibility() != View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_up);
                    menuCardView.setVisibility(View.VISIBLE);
                    menuCardView.startAnimation(animation);
                }
            }
        });

        return rootView;
    }

    private void getDataToShow() {
        TextFormat textFormat = new TextFormat();
        sortData();
        titleTextView.setText(title);
        tagView.setText(String.format("Tag: %s", textFormat.splitTextTag(tag)));
        lastEditedView.setText(textFormat.formatForTextLastEdit(mainActivity, dateUpdatedAt));

        if (arrayData != null) {
            int index = 0;
            for (JSONHelperDataTodo data : arrayData) {
                createElements(data, index);
                index++;
            }
            tmpPosition = arrayData.size();
        }

        reminderStatusImageView.setImageResource(R.drawable.ic_outline_notifications_off_24);

        remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> s :
                remindersTitlePreference.getAll().entrySet()) {

            if (s.getValue().equals(titleTextView.getText().toString()))
                reminderStatusImageView.setImageResource(R.drawable.ic_notifications_green_24dp);
        }
    }

    private void sortData() {
        ArrayList<JSONHelperDataTodo> tmp = new ArrayList<>();
        for (JSONHelperDataTodo data : arrayData) {
            if (!data.done)
                tmp.add(data);
        }
        for (JSONHelperDataTodo data : arrayData) {
            if (data.done)
                tmp.add(data);
        }
        arrayData.clear();
        arrayData = tmp;
    }

    private void createElements(JSONHelperDataTodo data, int index) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        doneCheckBox = new CheckBox(context);
        taskTextView = new TextView(context);
        taskTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskTextView.setBackgroundColor(Color.WHITE);
        taskTextView.setTextSize(20);
        taskTextView.setTextColor(Color.BLACK);
        taskTextView.setPadding(0, 20, 0, 20);

        taskTextView.setText(data.task);

        taskTextView.setTag("t_" + index);
        doneCheckBox.setTag("d_" + index);

        doneCheckBox.setOnCheckedChangeListener(this);

        doneCheckBox.setChecked(data.done);

        taskTextView.setPaintFlags(doneCheckBox.isChecked() ?
                taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        taskTextView.setTextColor(data.done ? Color.GRAY : Color.BLACK);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskTextView);

        box.addView(linearLayout);
    }

    private void updateUI(boolean b, String tag) {
        TextView textView;
        if (rootView != null) {
            textView = rootView.findViewWithTag(tag);
            textView.setPaintFlags(b ? textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                    textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(b ? Color.GRAY : Color.BLACK);

        } else {
            mainActivity.closeFragment(this, new TodoFragment());
            new Messages(context).showMessage("Something wrong, try again.");
        }
    }

    private void updateTodo() {
        JSONHelperEditTodo jhet;
        dataHelper = new ArrayList<>();
        for (int i = 0; i < tmpPosition; i++) {
            taskTextView = rootView.findViewWithTag("t_" + i);
            doneCheckBox = rootView.findViewWithTag("d_" + i);
            jhet = new JSONHelperEditTodo(taskTextView.getText().toString(), doneCheckBox.isChecked());

            dataHelper.add(jhet);
        }

        EditStatusTodoAsync editTodoAsync = new EditStatusTodoAsync();
        editTodoAsync.execute();

        LoadTasksThread loadTasksThread = new LoadTasksThread();
        loadTasksThread.execute("notToUI");
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadTasksThread loadTasksThread = new LoadTasksThread();
        loadTasksThread.execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isPressed()) {
            updateTodo();
            updateUI(b, compoundButton.getTag().toString().replace("d", "t"));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editTODO: {
                mainActivity.initFragment(new EditTodoFragment(title, userID, todoID, arrayData, tag, archive), true);
                break;
            }
            case R.id.create_reminder: {
                DialogFragment dialogFragment = new CreateReminderDialog();
                ReminderHelper.setTitle(titleTextView.getText().toString());
                dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "create reminder");
                break;
            }
            case R.id.archiveTODO: {
                ArchiveActionAsync archiveActionAsync = new ArchiveActionAsync();
                archiveActionAsync.execute();

                break;
            }
            case R.id.deleteTODO: {
                DialogFragment dialogFragment = new DeleteTodoAskDialog(context, mainActivity, TodoDetailsFragment.this, title, userID, todoID);
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "delete todo");
            }
        }
    }

    class LoadTasksThread extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient();
            arrayData = new ArrayList<>();
            arrayData = APIClient.loadTodos(userID, todoID);
            JSONHelperTag tagObject = APIClient.getTagTodo(userID, todoID);
            tag = tagObject.tag;

            JSONHelperLastEdit dateUpdatedAtObject = APIClient.loadTodosLastEdit(userID, todoID);
            dateUpdatedAt = dateUpdatedAtObject.updatedAt;

            if (arrayData != null && arrayData.size() > 0) {
                if (strings != null)
                    for (String s : strings) {
                        if (s.equals("notToUI")) return TaskState.DONE_NOT_TO_UI;
                        else return TaskState.DONE;
                    }
                return TaskState.DONE;
            }

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            swipeRefreshLayout.setRefreshing(false);

            switch (state) {
                case DONE: {
                    if (arrayData != null) {
                        box.removeAllViews();
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

    class EditStatusTodoAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient();
            int code = APIClient.editTodoTaskStatus(userID, todoID, dataHelper);
            JSONHelperLastEdit dateUpdatedAtObject = APIClient.loadTodosLastEdit(userID, todoID);
            dateUpdatedAt = dateUpdatedAtObject.updatedAt;

            if (code == 200 || code == 201) {
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);

            switch (state) {
                case DONE: {
                    lastEditedView.setText(new TextFormat().formatForTextLastEdit(mainActivity, dateUpdatedAt));
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Todo not updated, try again");
                    break;
                }
            }
        }
    }

    class ArchiveActionAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient();
            int code = APIClient.archiveTodoAction(userID, todoID, !archive);
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
                    archiveFAB.setImageResource(archive ? R.drawable.ic_archive_white_24dp : R.drawable.ic_baseline_unarchive_24);
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
