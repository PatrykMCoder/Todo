package com.example.todo.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.helpers.CreateTodoHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add_new_todo, container, false);

        newTitleEditText = rootView.findViewById(R.id.new_title_todo);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        box = rootView.findViewById(R.id.box);

        data = new ArrayList<>();

        saveTodoButton.setOnClickListener(this);

        createElements();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_todo) {
            saveTodo();
        }
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
        newTaskEditText.setMaxLines(1);
        newTaskEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        newTaskEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (!newTaskEditText.getText().toString().isEmpty()) {
                            title = newTitleEditText.getText().toString();
                            task = newTaskEditText.getText().toString();
                            tag = "";
                            done = checkBoxDone.isChecked() ? 1 : 0;
                            createTodoHelper = new CreateTodoHelper(task, done, tag);
                            data.add(createTodoHelper);
                            createElements();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        newTaskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if(s.length() < 1){
//                    createElements();
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        linearLayout.addView(checkBoxDone);
        linearLayout.addView(newTaskEditText);

        box.addView(linearLayout);
        createdElement++;
    }

    private void saveTodo() {
        title = newTitleEditText.getText().toString();
        task = newTaskEditText.getText().toString();
        done = checkBoxDone.isChecked() ? 1 : 0;
        tag = "";
        createTodoHelper = new CreateTodoHelper(task, done, tag);
        data.add(createTodoHelper);
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
    }
}
