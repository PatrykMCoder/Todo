package com.example.todo;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.database.TodoAdapter;

import java.text.ParseException;
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
    private EditText newDateLimitEditText;
    private Button newButtonSubmit;

    private TodoAdapter todoAdapter;
    private MainActivity mainActivity;

    private String title;
    private String description;
    private String createDate;
    private String reamingDate;

    private final static String TAG = "AddNewTodo";

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
        newDateLimitEditText = rootView.findViewById(R.id.new_limit_date);
        newButtonSubmit = rootView.findViewById(R.id.submit_button);

        newDateLimitEditText.setFocusable(false);
        newDateLimitEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFromPicker(context);
            }
        });
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

    private void setDateFromPicker(Context context){
        Calendar c = Calendar.getInstance();
        DatePickerDialog setTime = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date d = calendar.getTime();
                String date = sdf.format(d);
                newDateLimitEditText.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        setTime.create();
        setTime.show();
    }

    private void saveTodo() {
        title = newTitleEditText.getText().toString();
        description = newDescriptionEditText.getText().toString();
        createDate = getCurrentDateFormat();
        reamingDate = newDateLimitEditText.getText().toString();

        if (!title.isEmpty() && !description.isEmpty()) {

            if(!reamingDate.isEmpty()) {
                if (checkDateReaming(reamingDate)) {
                    todoAdapter.openDB();
                    todoAdapter.insertDataTodo(title, description, createDate, reamingDate, 0);
                    todoAdapter.closeDB();

                    mainActivity.closeFragment(this, new TodoFragment(mainActivity.getApplicationContext()));
                } else {
                    Toast.makeText(context, "Check your date reaming. It is ok?", Toast.LENGTH_SHORT).show();
                }
            }else{
                todoAdapter.openDB();
                todoAdapter.insertDataTodo(title, description, createDate, reamingDate, 0);
                todoAdapter.closeDB();

                mainActivity.closeFragment(this, new TodoFragment(mainActivity.getApplicationContext()));
            }
        }else{
            Toast.makeText(context, "Title or description can't be empty!", Toast.LENGTH_LONG).show();
        }
    }
    private String getCurrentDateFormat(){
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM- yyyy");
        return sdf.format(currentDate);
    }

    private Date getCurrentDate(){
        Date currentDate = Calendar.getInstance().getTime();
        return currentDate;
    }

    private boolean checkDateReaming(String date){
        String[] check = date.split("-");
        Calendar calendar = Calendar.getInstance();
        Date today = getCurrentDate();

        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(check[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(check[1]));
        calendar.set(Calendar.YEAR, Integer.parseInt(check[2]));

        Date checkDate = calendar.getTime();

        return (checkDate.after(today) || checkDate.equals(today));
    }
}
