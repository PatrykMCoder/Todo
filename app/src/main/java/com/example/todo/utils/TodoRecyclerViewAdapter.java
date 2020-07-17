package com.example.todo.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.service.jsonhelper.JSONHelperLoadTitles;
import com.example.todo.service.MongoDBClient;
import com.example.todo.view.fragments.TodoDetailsFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoListViewHolder> {
    private TodoAdapter todoAdapter;
    private final static String TAG = "TODORECYCLERVIEW";
    private MainActivity mainActivity;
    private String description;
    private int done, id, counter;
    private String userID;

    private float percentDone = 0;

//    private ArrayList<String> titles;

    private ArrayList<JSONHelperLoadTitles> arrayTodos;

    private Context context;

    public TodoRecyclerViewAdapter(Context context, ArrayList<JSONHelperLoadTitles> arrayTodos, String userID) {
        this.arrayTodos = arrayTodos;
        this.context = context;
        this.userID = userID;
    }

    @NonNull
    @Override
    public TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new TodoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TodoListViewHolder holder, final int position) {
        mainActivity = (MainActivity) context;
        boolean checkNan;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (arrayTodos != null && arrayTodos.size() > 0) {
            holder.titleTextView.setText(arrayTodos.get(position).title);
            holder.cardView.setOnClickListener(view -> {
                mainActivity.initFragment(new TodoDetailsFragment(userID, arrayTodos.get(position).id, arrayTodos.get(position).title), true);
            });
        }
//        todoAdapter = new TodoAdapter(context, .get(position));
//        percentDone = todoAdapter.getPercentDoneTask();

//        holder.allTaskDoneImageView.setVisibility(percentDone >= 100                                  ? View.VISIBLE : View.GONE);
//        holder.allTaskDoneImageView.setImageResource((Double.valueOf(percentDone).isNaN()) ? R.drawable.ic_error_red_24dp : R.drawable.ic_done_green_24dp);
//        holder.percentProgressBar.setVisibility(percentDone < 100                                     ? View.VISIBLE : View.GONE);
//
//        holder.percentTaskTextView.setText(String.format("Done in: %s %%", Math.floor(percentDone)));
//        holder.percentProgressBar.setProgress((int) percentDone);
    }

    @Override
    public int getItemCount() {
        if (arrayTodos != null)
            return arrayTodos.size();
        return 0;
    }

    static class TodoListViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView percentTaskTextView;
        private ProgressBar percentProgressBar;
        private CheckBox doneCheckBox;
        private CardView cardView;
        private ImageView allTaskDoneImageView;

        TodoListViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.todoTitle);
            cardView = itemView.findViewById(R.id.cardView);
            percentTaskTextView = itemView.findViewById(R.id.percentDoneTask);
            percentProgressBar = itemView.findViewById(R.id.percentProgress);
            allTaskDoneImageView = itemView.findViewById(R.id.all_task_done_image);
        }
    }
}