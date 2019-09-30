package com.example.todo.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EditTodoFragment extends Fragment implements View.OnClickListener {

    private int id;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateReamingEditText;
    private Button submitEditButton;

    private MainActivity mainActivity;
    private Context context;

    private String title, description, dateReaming;

    public EditTodoFragment(){

    }

    public EditTodoFragment(int id){
        this.id = id;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_edit_todo, container, false);

        getData();

        titleEditText = rootView.findViewById(R.id.editTitle);
        descriptionEditText = rootView.findViewById(R.id.editDescription);
        dateReamingEditText = rootView.findViewById(R.id.editTimeReaming);
        submitEditButton = rootView.findViewById(R.id.submitEdit);

        titleEditText.setText(title);
        descriptionEditText.setText(description);

        dateReamingEditText.setFocusable(false);
        dateReamingEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFromPicker(context);
            }
        });

        dateReamingEditText.setText(dateReaming);

        submitEditButton.setOnClickListener(this);

        return rootView;
    }

    private void getData(){
        TodoAdapter todoAdapter = new TodoAdapter(getContext());
        todoAdapter.openDB();
        ArrayList<String> data = todoAdapter.getRowTODO(id);
        todoAdapter.closeDB();

        title = data.get(0);
        description = data.get(1);
        dateReaming = data.get(4);
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
                dateReamingEditText.setText(date);
                dateReaming = dateReamingEditText.getText().toString();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        setTime.create();
        setTime.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submitEdit:
                updateTODO();
                break;
            default:break;
        }
    }

    private void updateTODO() {
        title = titleEditText.getText().toString();
        description = descriptionEditText.getText().toString();
        dateReaming = dateReamingEditText.getText().toString();

        if (!title.isEmpty() && !description.isEmpty()) {
            if (!dateReaming.isEmpty())
                if (checkDateReaming(dateReaming)) {
                    TodoAdapter todoAdapter = new TodoAdapter(getContext());
                    todoAdapter.openDB();
                    todoAdapter.editTODO(titleEditText.getText().toString(), descriptionEditText.getText().toString(), dateReamingEditText.getText().toString(), id);
                    todoAdapter.closeDB();
                    mainActivity.closeFragment(this, new TodoFragment(getContext()));
                } else {
                    Toast.makeText(context, "Check your date reaming. It is ok?", Toast.LENGTH_SHORT).show();
                }
            else {
                TodoAdapter todoAdapter = new TodoAdapter(getContext());
                todoAdapter.openDB();
                todoAdapter.editTODO(titleEditText.getText().toString(), descriptionEditText.getText().toString(), null, id);
                todoAdapter.closeDB();
                mainActivity.closeFragment(this, new TodoFragment(getContext()));
            }
        } else {
            Toast.makeText(context, "Title or description can't be empty!", Toast.LENGTH_LONG).show();
        }
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
