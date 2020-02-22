package com.example.todo.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.database.TodoAdapterV2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AddNewTodoFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private EditText newTitleEditText;
    private LinearLayout box;
    private CheckBox checkBox;
    private EditText newTaskEditText;
    private FloatingActionButton saveTodoButton;

    ArrayList<String> data;

    private TodoAdapterV2 todoAdapter;
    private MainActivity mainActivity;

    private String title;
    private String description;
    private final static String TAG = "AddNewTodoFragment";
    private int createdElement = 1;

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
        if(view.getId() == R.id.save_todo){
            saveTodo();
        }
    }

    private void createElements() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        checkBox = new CheckBox(context);
        newTaskEditText = new EditText(context);
        newTaskEditText.setId(createdElement);
        newTaskEditText.setHint("Enter task(press enter to submit end create other edit text)");
        newTaskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newTaskEditText.setBackgroundColor(Color.WHITE);

        newTaskEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        data.add(newTaskEditText.getText().toString());
                        createElements();
                        return true;
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

        linearLayout.addView(checkBox);
        linearLayout.addView(newTaskEditText);

        box.addView(linearLayout);
        createdElement++;
    }

    private void saveTodo(){
        title = newTitleEditText.getText().toString();
        data.add(newTaskEditText.getText().toString());
        todoAdapter = new TodoAdapterV2(context, title, data);
        todoAdapter.openDB();

        if(!title.isEmpty()){
            if(data != null && data.size() > 0) {
                todoAdapter.saveToDB();
            }
            else
                Toast.makeText(context, "You can't save empty TODO 😞", Toast.LENGTH_SHORT).show();
        }else {
            // TODO: 05/02/2020 show dialog with information about set title for todo
        }

        todoAdapter.closeDB();
        mainActivity.closeFragment(this, new TodoFragment());
    }
}
