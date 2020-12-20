package com.pmprogramms.todo.utils.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTodo;
import com.pmprogramms.todo.view.fragments.TodoDetailsFragment;

import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoListViewHolder> {
    private final static String TAG = "TODORECYCLERVIEW";
    private MainActivity mainActivity;
    private String description;
    private int done, id, counter;
    private String userID;

    private float percentDone = 0;

    private ArrayList<JSONHelperTodo> arrayTodos;

    private Context context;

    public TodoRecyclerViewAdapter(Context context, ArrayList<JSONHelperTodo> arrayTodos, String userID) {
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
        if (arrayTodos != null && arrayTodos.size() > 0) {
            holder.titleTextView.setText(arrayTodos.get(position).title);
            String tag = arrayTodos.get(position).tag == null ? "" : arrayTodos.get(position).tag;
            if (tag.equals("") || tag.toLowerCase().equals("no tag"))
                holder.todoTagView.setVisibility(View.INVISIBLE);
            holder.todoTagView.setText(tag);
            holder.cardView.setOnClickListener(view -> {
                mainActivity.initFragment(new TodoDetailsFragment(userID, arrayTodos.get(position).id, arrayTodos.get(position).title, arrayTodos.get(position).archive), true);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (arrayTodos != null)
            return arrayTodos.size();
        return 0;
    }

    static class TodoListViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView todoTagView;
        private CardView cardView;

        TodoListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todoTitle);
            cardView = itemView.findViewById(R.id.cardView);
            todoTagView = itemView.findViewById(R.id.todo_tag);
        }
    }
}