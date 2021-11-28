package com.pmprogramms.todo.utils.recyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.reminders.ReadAboutRemindersFromSharedpreference;
import com.pmprogramms.todo.utils.reminders.Reminder;

import java.util.ArrayList;

public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ReminderRecyclerListHolder> {

    private final ArrayList<String> data;
    private ReadAboutRemindersFromSharedpreference readAboutRemindersFromSharedpreference;

    public ReminderRecyclerAdapter(ArrayList<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ReminderRecyclerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        readAboutRemindersFromSharedpreference = new ReadAboutRemindersFromSharedpreference(parent.getContext());
        return new ReminderRecyclerListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderRecyclerListHolder holder, int position) {
        if (data != null)
            holder.textView.setText(data.get(position));

        holder.removeButton.setOnClickListener(v -> {
            if (data != null) {
                Reminder reminder = new Reminder(holder.removeButton.getContext());
                reminder.removeReminder(readAboutRemindersFromSharedpreference.getID(data.get(position)));
                SharedPreferences titles = holder.removeButton.getContext().getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
                SharedPreferences ids = holder.removeButton.getContext().getSharedPreferences("reminders_id", Context.MODE_PRIVATE);

                SharedPreferences.Editor deleterTitles = titles.edit();
                SharedPreferences.Editor deleterIds = ids.edit();

                deleterTitles.remove(data.get(position)).apply();
                deleterIds.remove(data.get(position)).apply();

                notifyItemRemoved(position);
                data.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ReminderRecyclerListHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageButton removeButton;

        public ReminderRecyclerListHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.reminder_text);
            removeButton = itemView.findViewById(R.id.remove_reminder_button);
        }
    }
}
