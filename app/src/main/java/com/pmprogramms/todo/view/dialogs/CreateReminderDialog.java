package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmprogramms.todo.R;
import com.pmprogramms.todo.utils.reminders.Reminder;
import com.pmprogramms.todo.utils.reminders.ReminderDisplayType;
import com.pmprogramms.todo.utils.reminders.ReminderHelper;
import com.pmprogramms.todo.utils.reminders.ReminderRepeatType;

import java.util.Calendar;

public class CreateReminderDialog extends DialogFragment {

    private TimePicker timePicker;
    private Spinner typeRepeatReminderSpinner;
    private Spinner typeDisplayReminderSpinner;
    private DatePicker datePicker;

    private Context context;

    private ArrayAdapter<String> typeRepeatReminderAdapter;
    private ArrayAdapter<String> typeDisplayReminderAdapter;

    private ReminderRepeatType reminderRepeatType;
    private ReminderDisplayType reminderDisplayType;

    private SharedPreferences remindersTitlePreference;
    private SharedPreferences remindersIdsPreference;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
        remindersIdsPreference = context.getSharedPreferences("reminders_id", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        initSpinnerAdapter();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = View.inflate(getContext(), R.layout.dialog_reminder_create, null);

        timePicker = v.findViewById(R.id.time_picker);
        datePicker = v.findViewById(R.id.date_picker);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(context));

        typeRepeatReminderSpinner = v.findViewById(R.id.reminder_repeat_type);
        typeDisplayReminderSpinner = v.findViewById(R.id.reminder_display_type);

        typeRepeatReminderSpinner.setAdapter(typeRepeatReminderAdapter);
        typeDisplayReminderSpinner.setAdapter(typeDisplayReminderAdapter);


        builder.setTitle("Create reminder");

        builder.setView(v)
                .setPositiveButton("Create", (dialogInterface, i) -> {
                    String title = ReminderHelper.getTitle();

                    Calendar c = Calendar.getInstance();

                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    int year = datePicker.getYear();

                    c.set(Calendar.DAY_OF_MONTH, day);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.YEAR, year);

                    c.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    c.set(Calendar.MINUTE, timePicker.getMinute());

                    if (c.before(Calendar.getInstance()))
                        c.add(Calendar.DATE, 1);

                    getSelectedItemFromSpinners();
                    long time = c.getTimeInMillis();

                    Reminder reminder = new Reminder(context, title, time, reminderRepeatType, reminderDisplayType);
                    reminder.createReminder();

                    remindersTitlePreference.edit().putString(title, title).apply();
                    remindersIdsPreference.edit().putInt(title, (int) time).apply();
                })

                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }

    private void initSpinnerAdapter() {
        typeRepeatReminderAdapter = new ArrayAdapter<>(context, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.reminder_repeat_type));
        typeDisplayReminderAdapter = new ArrayAdapter<>(context, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.reminder_display_type));
    }

    private void getSelectedItemFromSpinners() {
        switch (typeRepeatReminderSpinner.getSelectedItemPosition()) {
            case 1:
                reminderRepeatType = ReminderRepeatType.FOUR_TIMES_PER_DAY;
                break;
            case 2:
                reminderRepeatType = ReminderRepeatType.ONE_TIME_PER_DAY;
                break;
            case 0:
            default:
                reminderRepeatType = ReminderRepeatType.NONE;
                break;
        }

        switch (typeDisplayReminderSpinner.getSelectedItemPosition()) {
            case 1:
                reminderDisplayType = ReminderDisplayType.NOTIFICATION_AND_OPEN_ACTIVITY;
                break;
            case 2:
                reminderDisplayType = ReminderDisplayType.ONLY_OPEN_ACTIVITY;
                break;
            default:
                reminderDisplayType = ReminderDisplayType.ONLY_NOTIFICATION;
                break;
        }
    }

}
