package com.example.todo.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.todo.helpers.HideAppBarHelper;
import com.example.todo.helpers.TagsHelper;
import com.example.todo.utils.formats.StringFormater;
import com.example.todo.utils.reminders.ReminderHelper;
import com.example.todo.view.dialogs.CreateReminderDialog;
import com.example.todo.view.dialogs.DeleteTodoAskDialog;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class TodoDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final static String TAG = "detailsfragment";

    private int id = 0;

    private TodoAdapter todoAdapter;

    private String title;
    private String description;
    private String done;
    private String tag;

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
                DialogFragment dialogFragment = new DeleteTodoAskDialog(context, mainActivity, TodoDetailsFragment.this, title);
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "delete todo");
            }
        });

        return rootView;
    }

    private String formatForTextLastEdit(String data) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int pixelsWidth = displayMetrics.widthPixels;
        return pixelsWidth < 1200 ? String.format("Edited:\n%s", data) : String.format("Edited: %s", data);
    }

    private void getDataToShow() {
        todoAdapter = new TodoAdapter(context, title);
        data = todoAdapter.loadAllData();
        title = new StringFormater(title).deformatTitle();
        titleTextView.setText(title);

        for (int i = 0; i < data.size(); i++) {
            createElements(i);
        }
        tag = data.get(0).getTag();
        tagView.setText(String.format("TAG: %s", tag));
        reminderStatusImageView.setImageResource(R.drawable.ic_outline_notifications_off_24);

        if (!data.get(0).getLastEdited().equals(""))
            lastEditedView.setText((formatForTextLastEdit(data.get(0).getLastEdited())));

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

        taskTextView.setText(data.get(position).getTask());

        taskTextView.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);

        doneCheckBox.setOnCheckedChangeListener(this);

        helperForCheckBox.add(doneCheckBox.getTag().toString());
        doneCheckBox.setChecked(data.get(position).getDone() == 1);

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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isPressed()) {
            String helperTag = "";
            for (int i = 0; i < helperForCheckBox.size(); i++) {
                if (compoundButton.getTag().equals(String.format("d_%s", i))) {
                    TodoAdapter todoAdapter = new TodoAdapter(context);
                    todoAdapter.changeStatusTask(new StringFormater(title).formatTitle(), data.get(i).getTask(), b ? 1 : 0);
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
                title = new StringFormater(title).formatTitle();
                mainActivity.initFragment(new EditTodoFragment(title), true);
                TagsHelper.setTag(tag);
                break;
            }

            case R.id.create_reminder: {
                DialogFragment dialogFragment = new CreateReminderDialog();
                ReminderHelper.setTitle(new StringFormater(titleTextView.getText().toString()).formatTitle());
                dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "create reminder");
                break;
            }
        }
    }
}
