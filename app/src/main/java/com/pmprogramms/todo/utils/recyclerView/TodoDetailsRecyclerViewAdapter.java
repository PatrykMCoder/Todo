package com.pmprogramms.todo.utils.recyclerView;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.api.retrofit.todo.todo.Todos;
import com.pmprogramms.todo.R;

import java.util.List;

public class TodoDetailsRecyclerViewAdapter extends RecyclerView.Adapter<TodoDetailsRecyclerViewAdapter.TodoDetailsRecyclerViewViewHolder> {
    private final List<Todos> data;
    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(int position, boolean value);
    }

    public TodoDetailsRecyclerViewAdapter(List<Todos> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TodoDetailsRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_details_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new TodoDetailsRecyclerViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoDetailsRecyclerViewViewHolder holder, int position) {
        holder.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    class TodoDetailsRecyclerViewViewHolder extends RecyclerView.ViewHolder {
        private TextView todoTextView;
        private final View v;

        TodoDetailsRecyclerViewViewHolder(View v) {
            super(v);
            this.v = v;
        }

        void bind(Todos t, int position) {
            todoTextView = v.findViewById(R.id.todo);
            CheckBox statusTodoCheckBox = v.findViewById(R.id.status);

            todoTextView.setText(t.task);
            statusTodoCheckBox.setChecked(t.done);

            updateUI(statusTodoCheckBox.isChecked());

            statusTodoCheckBox.setOnCheckedChangeListener((compoundButton, value) -> {
                listener.onItemClick(position, value);
                updateUI(value);
            });
        }

        private void updateUI(boolean checked) {
            todoTextView.setPaintFlags(checked ? todoTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                    todoTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            todoTextView.setTextColor(checked ? Color.GRAY : Color.BLACK);
        }
    }
}
