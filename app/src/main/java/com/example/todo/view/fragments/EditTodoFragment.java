package com.example.todo.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.todo.service.jsonhelper.JSONHelperEditTodo;
import com.example.todo.service.MongoDBClient;
import com.example.todo.service.jsonhelper.JSONHelperLoadDataTodo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class EditTodoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditTODO";
    private ArrayList<JSONHelperLoadDataTodo> arrayData;
    private int id;
    private EditText titleEditText;
    private EditText taskEditText;
    private CheckBox doneCheckBox;
    private LinearLayout box;
    private MainActivity mainActivity;
    private Context context;
    private String title, task;
    private int done;
    private ArrayList<JSONHelperEditTodo> dataHelper;
    private FloatingActionButton saveTodoButton;

    private ProgressDialog progressDialog;

    private View rootView;
    private LinearLayout l;

    private String userID, todoID, tag;

    private int tmpPosition;

    public EditTodoFragment() {

    }

    public EditTodoFragment(String title, String userID, String todoID, ArrayList<JSONHelperLoadDataTodo> arrayData, String tag) {
        this.title = title;
        this.userID = userID;
        this.todoID = todoID;
        this.arrayData = arrayData;
        this.tag = tag;
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
        rootView = inflater.inflate(R.layout.fragment_edit_todo, container, false);
        box = rootView.findViewById(R.id.box);
        titleEditText = rootView.findViewById(R.id.title_edit);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        saveTodoButton.setOnClickListener(this);
        titleEditText.setText(title.replace("_", " "));
        l = rootView.findViewById(R.id.box_new_item);
        l.setOnClickListener(this);

        loadData();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_todo: {
                updateTodo();
                break;
            }
            case R.id.box_new_item: {
                createElement();
                break;
            }
        }
    }

    private void loadData() {
        for (int i = 0; i < arrayData.size(); i++)
            createElementsWithData(i);

        tmpPosition = arrayData.size();
    }

    private void createElementsWithData(int position) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setTextSize(20);
        taskEditText.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);
        taskEditText.setText(arrayData.get(position).task);
        doneCheckBox.setChecked(arrayData.get(position).done);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);

        box.addView(linearLayout);
    }

    private void createElement() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setTextSize(20);
        taskEditText.requestFocus();
        taskEditText.setMaxLines(1);
        taskEditText.setHint("Enter task");
        taskEditText.setTag("t_" + tmpPosition);
        doneCheckBox.setTag("d_" + tmpPosition);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);
        box.addView(linearLayout);

        tmpPosition++;
    }

    private void updateTodo() {
        progressDialog = ProgressDialog.show(context, "Update...", "Please wait...");
        EditText editText;
        CheckBox checkBox;
        JSONHelperEditTodo jhet;
        dataHelper = new ArrayList<>();
        for (int i = 0; i < tmpPosition; i++) {
            editText = rootView.findViewWithTag("t_" + i);
            checkBox = rootView.findViewWithTag("d_" + i);
            jhet = new JSONHelperEditTodo(editText.getText().toString(), checkBox.isChecked());

            dataHelper.add(jhet);

        }

        EditTodoAsync editTodoAsync = new EditTodoAsync();
        editTodoAsync.execute();
    }

    private String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        return SimpleDateFormat.getDateTimeInstance().format(calendar.getTime());
    }

    class EditTodoAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            int code = mongoDBClient.editTodo(userID, todoID, dataHelper, tag);
            if(code == 200 || code == 201)
                return "done";

            return "notDone";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s.equals("done")) {
                mainActivity.closeFragment(EditTodoFragment.this, new TodoDetailsFragment(userID, todoID, title));
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Todo not updated, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
