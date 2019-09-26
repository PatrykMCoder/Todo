package com.example.todo.utils;

import android.animation.ValueAnimator;
import android.os.Build;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.TodoDetailsFragment;
import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.objects.TodoObject;

import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoListViewHolder> {
    private ArrayList<TodoObject> data;
    private final static String TAG = "TODORECYCLERVIEW";
    private MainActivity mainActivity;
    private String title, description;
    private int done, id;

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
    public void onBindViewHolder(@NonNull final TodoListViewHolder holder, final int position) {
        mainActivity = (MainActivity)holder.cardView.getContext();
        holder.titleTextView.setText(data.get(position).getTitle());
        holder.descriptionTextView.setText(data.get(position).getDescription());

        title = data.get(position).getTitle();
        description = data.get(position).getDescription();
        done = data.get(position).getDone();

        if(done == 1){
            holder.doneCheckBox.setChecked(true);
        }else  if(done == 0){
            holder.doneCheckBox.setChecked(false);
        }else {
            return;
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   TodoAdapter todoAdapter = new TodoAdapter(view.getContext());
                todoAdapter.openDB();
                id = todoAdapter.getIdColumn(holder.titleTextView.getText().toString(), holder.descriptionTextView.getText().toString());
                mainActivity.initFragment(new TodoDetailsFragment(id), true);
                todoAdapter.closeDB();*/
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        holder.doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TodoAdapter todoAdapter = new TodoAdapter(compoundButton.getContext());
                todoAdapter.openDB();
                id = todoAdapter.getIdColumn(holder.titleTextView.getText().toString(), holder.descriptionTextView.getText().toString());
                Log.d(TAG, "onCheckedChanged: " + compoundButton.getId());
                switch (compoundButton.getId()) {
                    case R.id.todoDoneHomePage:
                        if (b) {
                            todoAdapter.changeStatusTODO(1, id);
                        } else {
                            todoAdapter.changeStatusTODO(0, id);
                        }
                        todoAdapter.closeDB();
                        Log.d(TAG, "onCheckedChanged: " + b);
                        break;
                    default:
                        todoAdapter.closeDB();
                        break;
                }
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
        public CheckBox doneCheckBox;
       // public LinearLayout layoutItemTodo;
        public CardView cardView;

        public TodoListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todoTitle);
            descriptionTextView = itemView.findViewById(R.id.todoDescription);
            doneCheckBox = itemView.findViewById(R.id.todoDoneHomePage);
           // layoutItemTodo = itemView.findViewById(R.id.layoutItemTodo);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}