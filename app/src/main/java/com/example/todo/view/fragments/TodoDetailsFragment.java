package com.example.todo.view.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.view.HideAppBarHelper;
import com.example.todo.API.jsonhelper.JSONHelperEditTodo;
import com.example.todo.API.MongoDBClient;
import com.example.todo.API.jsonhelper.JSONHelperLoadDataTodo;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.utils.formats.StringFormater;
import com.example.todo.utils.reminders.ReminderHelper;
import com.example.todo.view.dialogs.CreateReminderDialog;
import com.example.todo.view.dialogs.DeleteTodoAskDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class TodoDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final static String TAG = "detailsfragment";

    private String todoID;
    private String description;
    private String done;
    private String tag;
    private String title;
    private String userID;
    private String taskID;
    private boolean archive;

    private LinearLayout box;
    private TextView titleTextView;
    private TextView taskTextView;
    private CheckBox doneCheckBox;
    private TextView tagView;
    private TextView lastEditedView;
    private ImageView reminderStatusImageView;
    private ProgressDialog progressDialog;
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

    private ArrayList<String> helperForCheckBox;

    private View rootView;

    private SharedPreferences remindersTitlePreference;
    private ArrayList<JSONHelperLoadDataTodo> arrayData;
    private ArrayList<JSONHelperEditTodo> arrayDataEdit;
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
        helperForCheckBox = new ArrayList<>();

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
        createReminderFAB.setOnClickListener(this);
        tagView.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                box.removeAllViews();
                LoadTasksThread loadTasksThread = new LoadTasksThread();
                loadTasksThread.execute();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        }

        return rootView;
    }

    private String formatForTextLastEdit(String data) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int pixelsWidth = displayMetrics.widthPixels;
        return pixelsWidth < 800 ? String.format("Edited:\n%s", data.split("T")[0]) : String.format("Edited: %s", data.split("T")[0]);
    }

    private String splitTextTag(String tag) {
        if (tag.length() >= 7) {
            tag = tag.substring(0, Math.min(tag.length(), 7)) + "...";
        }
        return tag;
    }

    private void getDataToShow() {
        titleTextView.setText(title);
        tagView.setText(String.format("Tag: %s", splitTextTag(tag)));
        lastEditedView.setText(formatForTextLastEdit(dateUpdatedAt));
        if (arrayData != null) {
            for (int i = 0; i < arrayData.size(); i++) {
                createElements(i);
            }
            ;
            tmpPosition = arrayData.size();
        }

        reminderStatusImageView.setImageResource(R.drawable.ic_outline_notifications_off_24);

        remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> s :
                remindersTitlePreference.getAll().entrySet()) {

            if (new StringFormater(s.getValue().toString()).deformatTitle().equals(titleTextView.getText().toString()))
                reminderStatusImageView.setImageResource(R.drawable.ic_notifications_green_24dp);
        }
    }

    private void createElements(int position) {
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

        taskTextView.setText(arrayData.get(position).task);

        taskTextView.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);

        doneCheckBox.setOnCheckedChangeListener(this);

        doneCheckBox.setChecked(arrayData.get(position).done);

        taskTextView.setPaintFlags(doneCheckBox.isChecked() ?
                taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

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
        } else {
            mainActivity.closeFragment(this, new TodoFragment());
            Toast.makeText(context, "Something wrong, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTodo() {
        EditText editText;
        CheckBox checkBox;
        JSONHelperEditTodo jhet;
        dataHelper = new ArrayList<>();
        for (int i = 0; i < tmpPosition; i++) {
            taskTextView = rootView.findViewWithTag("t_" + i);
            checkBox = rootView.findViewWithTag("d_" + i);
            jhet = new JSONHelperEditTodo(taskTextView.getText().toString(), checkBox.isChecked());

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
            MongoDBClient mongoDBClient = new MongoDBClient();
            Gson gson = new Gson();
            arrayData = new ArrayList<>();
            arrayData = gson.fromJson(mongoDBClient.loadTodos(userID, todoID), new TypeToken<ArrayList<JSONHelperLoadDataTodo>>() {
            }.getType());
            tag = mongoDBClient.getTagTodo(userID, todoID);
            dateUpdatedAt = mongoDBClient.loadTodosLastEdit(userID, todoID);

            if (arrayData != null) {
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
                    getDataToShow();
                    break;
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
                case DONE_NOT_TO_UI: {
                    //NOTHING
                    break;
                }
            }
        }
    }

    class EditStatusTodoAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            int code = mongoDBClient.editTodoTaskStatus(userID, todoID, dataHelper);
            dateUpdatedAt = mongoDBClient.loadTodosLastEdit(userID, todoID);

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
                    lastEditedView.setText(formatForTextLastEdit(dateUpdatedAt));
                    break;
                }
                case NOT_DONE: {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(rootView, "Todo not updated, try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
        }
    }

    class ArchiveActionAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            int code = mongoDBClient.archiveTodoAction(userID, todoID, !archive);
            if (code == 200 || code == 201)
                return TaskState.DONE;
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    new Handler().post(() -> {
                       Toast.makeText(context, "Action done", Toast.LENGTH_SHORT).show();
                    });
                    break;
                }
                case NOT_DONE: {
                    new Handler().post(() -> {
                        Toast.makeText(context, "Action error", Toast.LENGTH_SHORT).show();
                    });
                    break;
                }
            }
        }
    }
}
