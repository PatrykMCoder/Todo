package com.example.todo.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.objects.TodoObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoListViewHolder> {
    private ArrayList<TodoObject> data;
    private final static String TAG = "TODORECYCLERVIEW";

    public TodoRecyclerViewAdapter(ArrayList<TodoObject> data){
        this.data = data;
    }

    @NonNull
    @Override
    public TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new TodoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoListViewHolder holder, int position) {
        holder.titleTextView.setText(data.get(position).getTitle());
        holder.descriptionTextView.setText(data.get(position).getDescription());
        holder.dateCreateTextView.setText(data.get(position).getDateCreate());
        holder.dateReamingTextView.setText(data.get(position).getDateReaming());

        if(data.get(position).getDone() == 0){
            holder.doneCheckBox.setChecked(false);
        }else if(data.get(position).getDone() == 1){
            holder.doneCheckBox.setChecked(true);
        }else Log.d(TAG, "Error with checked");

        holder.layoutItemTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo get info about selected todo item: data create, finished, description(need update data base!)
                // TODO: 23/08/2019 -> on long tap -> remove
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TodoListViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView dateCreateTextView;
        public TextView dateReamingTextView;
        public CheckBox doneCheckBox;
        public LinearLayout layoutItemTodo;

        public TodoListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todoTitle);
            descriptionTextView = itemView.findViewById(R.id.todoDescription);
            dateCreateTextView = itemView.findViewById(R.id.todoDateCreate);
            dateReamingTextView = itemView.findViewById(R.id.todoDateReaming);
            doneCheckBox = itemView.findViewById(R.id.todoDone);
            layoutItemTodo = itemView.findViewById(R.id.layoutItemTodo);
        }
    }

}