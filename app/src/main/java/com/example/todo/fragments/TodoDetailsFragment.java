package com.example.todo.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.helpers.GetDataHelper;
import com.example.todo.utils.objects.TodoObject;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.ArrayList;

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

    private FloatingActionMenu floatingActionMenu;
    private com.github.clans.fab.FloatingActionButton editFAB;
    private com.github.clans.fab.FloatingActionButton archiveFAB;
    private com.github.clans.fab.FloatingActionButton deleteFAB;

    private Context context;
    private MainActivity mainActivity;

    private ArrayList<GetDataHelper> data;
    private ArrayList<String> helperForCheckBox;

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
        final View rootView = inflater.inflate(R.layout.fragment_todo_details, container, false);

        helperForCheckBox = new ArrayList<>();

        titleTextView = rootView.findViewById(R.id.title_preview);
        tagView = rootView.findViewById(R.id.tag_view);

        box = rootView.findViewById(R.id.box);
        floatingActionMenu = rootView.findViewById(R.id.menu);
        editFAB = rootView.findViewById(R.id.editTODO);
        archiveFAB = rootView.findViewById(R.id.archiveTODO);
        deleteFAB = rootView.findViewById(R.id.deleteTODO);

        editFAB.setOnClickListener(this);

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

    private void getDataToShow() {
        todoAdapter = new TodoAdapter(context, title);
        todoAdapter.openDB();
        data = todoAdapter.loadAllData(title);
        Log.d(TAG, "getDataToShow: " + data.size());
        todoAdapter.closeDB();
        title = title.replace("_", " ");
        titleTextView.setText(title);

        for (int i = 0; i < data.size(); i++) {
            createElements(i);
        }
        if(data.get(0).getTag().equals("") || data.get(0).getTag().equals("No tag"))
            tagView.setText("TAG: no tag");
        else tagView.setText(String.format("TAG: %s", data.get(0).getTag()));
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

        taskTextView.setText(data.get(position).getTask().replace("'", ""));
        doneCheckBox.setTag("d_" + position);
        doneCheckBox.setOnCheckedChangeListener(this);

        helperForCheckBox.add(doneCheckBox.getTag().toString());
        doneCheckBox.setChecked(data.get(position).getDone() == 1);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskTextView);

        box.addView(linearLayout);
    }

    private void updateUI() {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        for (int i = 0; i < helperForCheckBox.size(); i++) {
            if (compoundButton.getTag().equals(String.format("d_%s", i))) {
                TodoAdapter todoAdapter = new TodoAdapter(context);
                todoAdapter.openDB();
                todoAdapter.changeStatusTask(title.replace(" ", "_"), data.get(i).getTask(), b ? 1 : 0);
                todoAdapter.closeDB();
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editTODO:
                title = title.replace(" ", "_");
                mainActivity.initFragment(new EditTodoFragment(title), true);
                break;
        }
    }
}
