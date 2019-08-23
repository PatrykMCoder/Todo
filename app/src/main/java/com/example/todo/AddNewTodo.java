package com.example.todo;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.todo.database.TodoAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewTodo extends Fragment implements View.OnClickListener {

    private Context context;

    private EditText newTitleEditText;
    private EditText newDescriptionEditText;
  //  private EditText newDateCreateEditText;
    private EditText newDateLimitEditText;
    private Button newButtonSubmit;

    private TodoAdapter todoAdapter;
    private MainActivity mainActivity;

    public AddNewTodo() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity)context;
        todoAdapter = new TodoAdapter(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_add_new_todo, container, false);

        newTitleEditText = rootView.findViewById(R.id.new_title_todo);
        newDescriptionEditText = rootView.findViewById(R.id.new_description_todo);
        //newDateCreateEditText = rootView.findViewById(R.id.new_date_create);
        newDateLimitEditText = rootView.findViewById(R.id.new_limit_date);
        newButtonSubmit = rootView.findViewById(R.id.submit_button);

        newButtonSubmit.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_button:
                saveTodo();
                break;
        }
    }

    private void saveTodo(){
        String title;
        String description;
        String createDate;
        String reamingDate;

        title  = newTitleEditText.getText().toString();
        description = newDescriptionEditText.getText().toString();
        createDate = getCurrentDate();
        reamingDate = newDateLimitEditText.getText().toString();


        todoAdapter.openDB();
        todoAdapter.insertDataTodo(title, description, createDate, reamingDate, 0);
        todoAdapter.closeDB();

        mainActivity.closeFragment(this, new TodoFragment(mainActivity.getApplicationContext()));
    }

    private String getCurrentDate(){
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(currentDate);
    }
}
