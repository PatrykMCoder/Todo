package com.pmprogramms.todo.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.Todos;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperEditTodo;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.reminders.ReminderHelper;
import com.pmprogramms.todo.view.dialogs.CreateReminderDialog;
import com.pmprogramms.todo.view.dialogs.DeleteTodoAskDialog;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private String todoID;
    private String tag;
    private String title;
    private String userID;
    private int color;
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
    private RelativeLayout relativeLayout;

    private API api;

    private FloatingActionMenu floatingActionMenu;
    private com.github.clans.fab.FloatingActionButton createReminderFAB;
    private com.github.clans.fab.FloatingActionButton editFAB;
    private com.github.clans.fab.FloatingActionButton archiveFAB;
    private com.github.clans.fab.FloatingActionButton deleteFAB;

    public Context context;
    private MainActivity mainActivity;

    private View rootView;

    private SharedPreferences remindersTitlePreference;
    private ArrayList<JSONHelperEditTodo> dataHelper;
    private ArrayList<Todos> todosArrayList;
    private int tmpPosition;

    private String dateUpdatedAt;

    public TodoDetailsFragment() {

    }

    public TodoDetailsFragment(String userID, String todoID, String title, boolean archive, int color) {
        this.todoID = todoID;
        this.title = title;
        this.userID = userID;
        this.archive = archive;
        this.color = color;
        tmpArchive = archive;
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
        api = Client.getInstance().create(API.class);

        rootView = inflater.inflate(R.layout.fragment_todo_details, container, false);
        relativeLayout = rootView.findViewById(R.id.container2);
        relativeLayout.setBackgroundColor(color);

        titleTextView = rootView.findViewById(R.id.title_preview);
        titleTextView.setBackgroundColor(color);
        tagView = rootView.findViewById(R.id.tag);
        lastEditedView = rootView.findViewById(R.id.last_edited);
        reminderStatusImageView = rootView.findViewById(R.id.reminder_status);

        menuCardView = rootView.findViewById(R.id.menu_card);
        scrollView = rootView.findViewById(R.id.scroll_view);

        box = rootView.findViewById(R.id.box);
        box.setBackgroundColor(color);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setBackgroundColor(color);

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

        getTodoData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            box.removeAllViews();
            getTodoData();

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

    private void getTodoData() {
        Call<JSONHelperTodo> call = api.getUserTodoData(userID, todoID);
        call.enqueue(new Callback<JSONHelperTodo>() {
            @Override
            public void onResponse(Call<JSONHelperTodo> call, Response<JSONHelperTodo> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONHelperTodo helperTodo = response.body();
                ArrayList<Data> data = helperTodo.data;
                todosArrayList = data.get(0).todos;
                dateUpdatedAt = data.get(0).updatedAt;
                tag = data.get(0).tag;
                getDataToShow();
            }

            @Override
            public void onFailure(Call<JSONHelperTodo> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDataToShow() {
        TextFormat textFormat = new TextFormat();
        sortData();
        titleTextView.setText(title);
        tagView.setText(String.format("Tag: %s", textFormat.splitTextTag(tag)));
        lastEditedView.setText(textFormat.formatForTextLastEdit(mainActivity, dateUpdatedAt));

        if (todosArrayList != null) {
            int index = 0;
            for (Todos data : todosArrayList) {
                createElements(data, index);
                index++;
            }
            tmpPosition = todosArrayList.size();
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
        ArrayList<Todos> tmp = new ArrayList<>();
        for (Todos data : todosArrayList) {
            if (!data.done)
                tmp.add(data);
        }
        for (Todos data : todosArrayList) {
            if (data.done)
                tmp.add(data);
        }
        todosArrayList.clear();
        todosArrayList = tmp;
    }

    private void createElements(Todos data, int index) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        doneCheckBox = new CheckBox(context);
        taskTextView = new TextView(context);
        taskTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskTextView.setBackgroundColor(Color.TRANSPARENT);
        taskTextView.setTextSize(20);
        taskTextView.setTextColor(Color.BLACK);
        taskTextView.setPadding(0, 20, 0, 20);
        taskTextView.setBackgroundColor(Color.TRANSPARENT);

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

        HashMap<String, String> map = new HashMap<>();
        map.put("todos", new Gson().toJson(dataHelper, new TypeToken<ArrayList<JSONHelperEditTodo>>() {
        }.getType()));

        Call<Data> call = api.updateTodoStatus(userID, todoID, map);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage("Something wrong, try again");
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
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
        int id = v.getId();
        if (id == R.id.editTODO) {
            mainActivity.initFragment(new EditTodoFragment(title, userID, todoID, todosArrayList, tag, archive, color), true);
        } else if (id == R.id.create_reminder) {
            DialogFragment dialogFragment = new CreateReminderDialog();
            ReminderHelper.setTitle(titleTextView.getText().toString());
            dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "create reminder");
        } else if (id == R.id.archiveTODO) {
            archiveAction();
        } else if (id == R.id.deleteTODO) {
            DialogFragment dialogFragment = new DeleteTodoAskDialog(context, mainActivity, TodoDetailsFragment.this, title, userID, todoID);
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "delete todo");
        }
    }

    private void archiveAction() {
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("archive", !tmpArchive);
        Call<Void> call = api.archiveAction(userID, todoID, map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage("Something wrong, try again");
                }

                new Messages(context).showMessage(tmpArchive ? "Unarchive" : "Archive");
                archiveFAB.setImageResource(archive ? R.drawable.ic_archive_white_24dp : R.drawable.ic_baseline_unarchive_24);

                archive = !tmpArchive;
                tmpArchive = archive;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                new Messages(context).showMessage("Something wrong, try again");
            }
        });
    }
}
