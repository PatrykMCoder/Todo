package com.example.todo;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.objects.TodoObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoDetailsFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "detailsfragment";

    private int id = 0;

    private TodoAdapter todoAdapter;
    private TodoObject todoObject;

    private String title;
    private String description;
    private String done;
    private String dataCreate;
    private String dataReaming;

    private TextView titleTextView;
    private TextView descriptionTextView;
    private CheckBox doneCheckBox;
    private TextView dataCreateTextView;
    private TextView dataReamingTextView;

    private FloatingActionButton editFAB;
    private FloatingActionButton archiveFAB;
    private FloatingActionButton deleteFAB;

    private Context context;
    private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity)context;
    }

    public TodoDetailsFragment(int id) {
        // Required empty public constructor
        this.id = id;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_todo_details, container, false);

        titleTextView = rootView.findViewById(R.id.titleDetails);
        descriptionTextView = rootView.findViewById(R.id.descriptionDetails);
        doneCheckBox = rootView.findViewById(R.id.doneDetails);
        dataCreateTextView = rootView.findViewById(R.id.dataCreateDetails);
        dataReamingTextView = rootView.findViewById(R.id.dataReamingDetails);

        editFAB = rootView.findViewById(R.id.editTODO);
        archiveFAB = rootView.findViewById(R.id.archiveTODO);
        deleteFAB = rootView.findViewById(R.id.deleteTODO);

        editFAB.setOnClickListener(this);
        archiveFAB.setOnClickListener(this);
        deleteFAB.setOnClickListener(this);

        getDataToShow();

        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editTODO:
                mainActivity.initFragment(new EditTodoFragment(), false);
                break;
            case R.id.archiveTODO:
                Toast.makeText(context, "Available in future :)", Toast.LENGTH_SHORT).show();
                break;
            case R.id.deleteTODO:
                TodoAdapter todoAdapter = new TodoAdapter(context);
                todoAdapter.openDB();
                todoAdapter.deleteTODO(todoAdapter.getIdColumn(title, description));
                todoAdapter.closeDB();
                mainActivity.closeFragment(this, new TodoFragment(context));
                break;
            default: break;
        }
    }

    private void getDataToShow(){
        todoAdapter = new TodoAdapter(context);
        todoAdapter.openDB();
        ArrayList<String> d = todoAdapter.getRowTODO(id);
        title = d.get(0);
        description = d.get(1);
        done = d.get(2);
        dataCreate = d.get(3);
        dataReaming= d.get(4);
        todoAdapter.closeDB();

        updateUI();
    }

    private void updateUI(){
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        dataCreateTextView.setText("Create at: \n" + dataCreate);
        dataReamingTextView.setText("Reaming at: \n" + dataReaming);

        int doneInt = Integer.parseInt(done);

        if(doneInt == 0){
            doneCheckBox.setChecked(false);
            doneCheckBox.setText("Not done");
        }else if( doneInt == 1){
            doneCheckBox.setChecked(true);
            doneCheckBox.setText("Done");
        }
    }
}
