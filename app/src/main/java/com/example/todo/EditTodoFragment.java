package com.example.todo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.database.TodoAdapter;

import java.util.ArrayList;


public class EditTodoFragment extends Fragment implements View.OnClickListener {

    private int id;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateReamingEditText;
    private Button submitEditButton;

    private MainActivity mainActivity;

    private String title, description, dateReaming;

    public EditTodoFragment(int id){
        this.id = id;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_edit_todo, container, false);

        getData();

        titleEditText = rootView.findViewById(R.id.editTile);
        descriptionEditText = rootView.findViewById(R.id.editDescription);
        dateReamingEditText = rootView.findViewById(R.id.editTimeReaming);
        submitEditButton = rootView.findViewById(R.id.submitEdit);

        titleEditText.setText(title);
        descriptionEditText.setText(description);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submitEdit:
                title = titleEditText.getText().toString();
                description = descriptionEditText.getText().toString();
                dateReaming = dateReamingEditText.getText().toString();

                if(!title.isEmpty() && !description.isEmpty()) {
                    TodoAdapter todoAdapter = new TodoAdapter(getContext());
                    todoAdapter.openDB();
                    todoAdapter.editTODO(titleEditText.getText().toString(), descriptionEditText.getText().toString(), dateReamingEditText.getText().toString(), id);
                    todoAdapter.closeDB();
                    mainActivity.closeFragment(this, new TodoFragment(getContext()));
                }else{
                    Toast.makeText(getContext(), "Title or description can't be empty!", Toast.LENGTH_LONG).show();
                }
                break;
            default:break;
        }
    }
}
