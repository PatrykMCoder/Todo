package com.pmprogramms.todo.utils.recyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.view.fragments.TodoArchiveFragmentDirections;
import com.pmprogramms.todo.view.fragments.TodoFragmentDirections;

import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoListViewHolder> {

    private final ArrayList<Data> arrayTodos;

    public TodoRecyclerViewAdapter(ArrayList<Data> arrayTodos) {
        this.arrayTodos = arrayTodos;
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
        holder.bind(arrayTodos.get(position));
        if (arrayTodos.get(position).color != null && !arrayTodos.get(position).color.equals("")) {
            holder.cardView.setCardBackgroundColor(Color.parseColor(arrayTodos.get(position).color));
        }

        holder.cardView.setOnClickListener(view -> {
            NavDirections navDirections;

            if (arrayTodos.get(position).archive) {
                navDirections = TodoArchiveFragmentDirections.actionTodoArchiveFragmentToTodoDetailsFragment(arrayTodos.get(position)._id);
            } else {
                navDirections = TodoFragmentDirections.actionTodoFragmentToTodoDetailsFragment(arrayTodos.get(position)._id);
            }

            Navigation.findNavController(view).navigate(navDirections);
        });
    }

    @Override
    public int getItemCount() {
        return arrayTodos.size();
    }

    class TodoListViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView todoTagView;
        private CardView cardView;

        TodoListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todoTitle);
            cardView = itemView.findViewById(R.id.cardView);
            todoTagView = itemView.findViewById(R.id.todo_tag);
        }

        private void bind(Data todoData) {
            titleTextView.setText(todoData.title);
            if (todoData.tag != null) {
                if (todoData.tag.equals("") || todoData.tag.equals("no tag"))
                    todoTagView.setVisibility(View.INVISIBLE);
                else todoTagView.setText(todoData.tag);
            } else {
                todoTagView.setVisibility(View.INVISIBLE);
            }

            if (todoData.color != null && !todoData.color.equals("")) {
                cardView.setCardBackgroundColor(Color.parseColor(todoData.color));
            }
        }
    }
}