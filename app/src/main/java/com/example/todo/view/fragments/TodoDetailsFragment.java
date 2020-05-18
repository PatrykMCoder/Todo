package com.example.todo.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.helpers.GetDataHelper;
import com.example.todo.helpers.TagsHelper;
import com.example.todo.utils.objects.TodoObject;
import com.example.todo.utils.reminders.ReminderHelper;
import com.example.todo.view.dialogs.CreateReminderDialog;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class TodoDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final static String TAG = "detailsfragment";

    private int id = 0;

    private TodoAdapter todoAdapter;
    private TodoObject todoObject;

    private String title;
    private String description;
    private String done;
    private String dataCreate;
    private String dataReaming;


    private LinearLayout box;
    private TextView titleTextView;
    private TextView taskTextView;
    private CheckBox doneCheckBox;
    private TextView tagView;
    private TextView lastEditedView;
    private ImageView reminderStatusImageView;

    private FloatingActionMenu floatingActionMenu;
    private com.github.clans.fab.FloatingActionButton createReminderFAB;
    private com.github.clans.fab.FloatingActionButton editFAB;
    private com.github.clans.fab.FloatingActionButton archiveFAB;
    private com.github.clans.fab.FloatingActionButton deleteFAB;

    private Context context;
    private MainActivity mainActivity;

    private ArrayList<GetDataHelper> data;
    private ArrayList<String> helperForCheckBox;

    private View rootView;

    private SharedPreferences remindersTitlePreference;

    public TodoDetailsFragment() {

    }

    public TodoDetailsFragment(String title) {
        this.title = title;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_todo_details, container, false);

        helperForCheckBox = new ArrayList<>();

        titleTextView = rootView.findViewById(R.id.title_preview);
        tagView = rootView.findViewById(R.id.tag);
        lastEditedView = rootView.findViewById(R.id.last_edited);
        reminderStatusImageView = rootView.findViewById(R.id.reminder_status);

        box = rootView.findViewById(R.id.box);

        floatingActionMenu = rootView.findViewById(R.id.menu);
        createReminderFAB = rootView.findViewById(R.id.create_reminder);
        editFAB = rootView.findViewById(R.id.editTODO);
        archiveFAB = rootView.findViewById(R.id.archiveTODO);
        deleteFAB = rootView.findViewById(R.id.deleteTODO);

        editFAB.setOnClickListener(this);
        createReminderFAB.setOnClickListener(this);
        tagView.setOnClickListener(this);
        getDataToShow();

        deleteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoAdapter.openDB();
                todoAdapter.deleteTodo(title.replace(" ", "_"));
                todoAdapter.closeDB();
                //delete file from device
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    File file = new File(context.getDataDir() + "/databases/" + title + ".db");
                    if (file.exists()) {
                        file.delete();
                        Log.d(TAG, "onClick: delete");
                    }
                } else {
                    //todo > this same for recycler
                }

                mainActivity.closeFragment(TodoDetailsFragment.this, new TodoFragment(getContext()));
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_nav_item, menu);
    }

    private void getDataToShow() {
        todoAdapter = new TodoAdapter(context, title);
        todoAdapter.openDB();
        data = todoAdapter.loadAllData(title);
        todoAdapter.closeDB();
        title = title.replace("_", " ");
        titleTextView.setText(title);

        for (int i = 0; i < data.size(); i++) {
            createElements(i);
        }
        tagView.setText(String.format("TAG: %s", data.get(0).getTag()));

        if (!data.get(0).getLastEdited().equals(""))
            lastEditedView.setText(String.format("Last edited: %s", data.get(0).getLastEdited()));

        remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> s:
             remindersTitlePreference.getAll().entrySet()) {
            Log.d(TAG, "getDataToShow: " + s);
            if (s.getValue().toString().replace(" ", "_").equals(titleTextView.getText().toString()))
                reminderStatusImageView.setImageResource(R.drawable.ic_notifications_green_24dp);
            else
                reminderStatusImageView.setImageResource(R.drawable.ic_notifications_none_gray_24dp);
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

        taskTextView.setText(data.get(position).getTask().replace("'", ""));

        taskTextView.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);

        doneCheckBox.setOnCheckedChangeListener(this);

        helperForCheckBox.add(doneCheckBox.getTag().toString());
        doneCheckBox.setChecked(data.get(position).getDone() == 1);

        if (doneCheckBox.isChecked())
            taskTextView.setPaintFlags(taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            taskTextView.setPaintFlags(taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskTextView);

        box.addView(linearLayout);
    }

    private void updateUI(boolean b, String tag) {
        TextView textView;
        if (rootView != null) {
            textView = rootView.findViewWithTag(tag);
            if (b)
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            mainActivity.closeFragment(this, new TodoFragment());
            Toast.makeText(context, "Something wrong, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isPressed()) {
            String helperTag = "";
            for (int i = 0; i < helperForCheckBox.size(); i++) {
                if (compoundButton.getTag().equals(String.format("d_%s", i))) {
                    TodoAdapter todoAdapter = new TodoAdapter(context);
                    todoAdapter.openDB();
                    todoAdapter.changeStatusTask(title.replace(" ", "_"), data.get(i).getTask(), b ? 1 : 0);
                    todoAdapter.closeDB();
                    helperTag = compoundButton.getTag().toString().replace("d", "t");
                    updateUI(b, helperTag);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editTODO: {
                title = title.replace(" ", "_");
                mainActivity.initFragment(new EditTodoFragment(title), true);
                break;
            }

            case R.id.create_reminder: {
                DialogFragment dialogFragment = new CreateReminderDialog();
                ReminderHelper.setTitle(titleTextView.getText().toString().replace("_", " "));
                dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "create reminder");
                break;
            }
        }
    }
}
