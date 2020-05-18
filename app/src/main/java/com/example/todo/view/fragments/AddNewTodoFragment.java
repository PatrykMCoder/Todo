package com.example.todo.view.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.helpers.CreateTodoHelper;
import com.example.todo.helpers.TagsHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewTodoFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "AddNewTodoFragment";
    List<CreateTodoHelper> data;
    private Context context;
    private EditText newTitleEditText;
    private LinearLayout box;
    private CheckBox checkBoxDone;
    private EditText newTaskEditText;
    private FloatingActionButton saveTodoButton;
    private FloatingActionButton setTagButton;
    private TodoAdapter todoAdapter;
    private MainActivity mainActivity;
    private String title;
    private String task;
    private String tag;
    private int done;
    private int createdElement = 1;

    private CreateTodoHelper createTodoHelper;

    public AddNewTodoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_add_new_todo, container, false);

        newTitleEditText = rootView.findViewById(R.id.new_title_todo);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        setTagButton = rootView.findViewById(R.id.set_tag);
        box = rootView.findViewById(R.id.box);

        data = new ArrayList<>();

        saveTodoButton.setOnClickListener(this);
        setTagButton.setOnClickListener(this);
        createElements();
        return rootView;
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()) {
           case R.id.save_todo:{
               saveTodo();
               break;
           }
           case R.id.set_tag: {
                DialogFragment dialogFragment = new SelectTodoTagFragment();
                dialogFragment.show(getFragmentManager(), "set tag todo");
                break;
           }
       }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_todo_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createElements() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        checkBoxDone = new CheckBox(context);
        newTaskEditText = new EditText(context);
        newTaskEditText.setId(createdElement);
        newTaskEditText.setHint("Enter task(press enter to submit end create other edit text)");
        newTaskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newTaskEditText.setBackgroundColor(Color.WHITE);
        newTaskEditText.setTextSize(20);
        newTaskEditText.setMaxLines(1);
        newTaskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newTaskEditText.setTextColor(Color.BLACK);

        newTaskEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (!newTaskEditText.getText().toString().isEmpty()) {
                            title = newTitleEditText.getText().toString();
                            task = newTaskEditText.getText().toString();
                            done = checkBoxDone.isChecked() ? 1 : 0;
                            createTodoHelper = new CreateTodoHelper(task, done, tag, getCurrentDateString());
                            data.add(createTodoHelper);
                            createElements();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        linearLayout.addView(checkBoxDone);
        linearLayout.addView(newTaskEditText);

        box.addView(linearLayout);
        createdElement++;
    }

    private String getCurrentDateString() {
        String date = "";
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM hh:mm");
        date = simpleDateFormat.format(calendar.getTime());
        Log.d(TAG, "getCurrentDateString: " + date);
        return date;
    }

    private void saveTodo() {
        title = newTitleEditText.getText().toString();
        if (!title.isEmpty()) {
            task = newTaskEditText.getText().toString();
            done = checkBoxDone.isChecked() ? 1 : 0;
            tag = TagsHelper.getTag();
            createTodoHelper = new CreateTodoHelper(task, done, tag, getCurrentDateString());
            data.add(createTodoHelper);


            for (int i = 0; i < data.size(); i++) {
                data.get(i).setTag(TagsHelper.getTag());
            }

            todoAdapter = new TodoAdapter(context, title, data);
            todoAdapter.openDB();

            if (!title.isEmpty()) {
                if (data != null && data.size() > 0) {
                    todoAdapter.saveToDB();
                } else
                    Toast.makeText(context, "You can't save empty TODO 😞", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: 05/02/2020 show dialog with information about set title for todo
            }

            todoAdapter.closeDB();
            mainActivity.closeFragment(this, new TodoFragment());

            TagsHelper.setTag("No tag");
        }else
            Toast.makeText(context, "Title can not be empty.", Toast.LENGTH_SHORT).show();
    }
}
