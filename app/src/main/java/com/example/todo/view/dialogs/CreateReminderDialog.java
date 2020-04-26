package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.R;
import com.example.todo.utils.reminders.Reminder;
import com.example.todo.utils.reminders.ReminderDisplayType;
import com.example.todo.utils.reminders.ReminderHelper;
import com.example.todo.utils.reminders.ReminderRepeatType;

import java.util.Calendar;

public class CreateReminderDialog extends DialogFragment {

    private static final String TAG = "CreateReminder";

    private TimePicker timePicker;
    private Spinner typeRepeatReminderSpinner;
    private Spinner typeDisplayReminderSpinner;
    private DatePicker datePicker;

    private Context context;

    private ArrayAdapter<String> typeRepeatReminderAdapter;
    private ArrayAdapter<String> typeDisplayReminderAdapter;

    private long timePickerTime;

    private ReminderRepeatType reminderRepeatType;
    private ReminderDisplayType reminderDisplayType;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // add date select! not only time!
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

        builder.setView(v);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = ReminderHelper.getTitle();

                Calendar c = Calendar.getInstance();

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.YEAR, year);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    c.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    c.set(Calendar.MINUTE, timePicker.getMinute());
                } else {
                    c.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    c.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                }

                if (c.before(Calendar.getInstance()))
                    c.add(Calendar.DATE, 1);

                boolean repeatReminder = false;
                getSelectedItemFromSpinners();

                Reminder reminder = new Reminder(context, title, c.getTimeInMillis(), repeatReminder, reminderRepeatType, reminderDisplayType);
                reminder.createReminder();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }

    private int dialogWidth() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        if (display != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            return displayMetrics.widthPixels;
        }
        return 0;
    }


    private void initSpinnerAdapter() {
        typeRepeatReminderAdapter = new ArrayAdapter<String>(context, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.reminder_repeat_type));
        typeDisplayReminderAdapter = new ArrayAdapter<String>(context, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.reminder_display_type));
    }

    private void getSelectedItemFromSpinners() {
        switch (typeRepeatReminderSpinner.getSelectedItemPosition()) {
            case 0:
                reminderRepeatType = ReminderRepeatType.NONE;
                break;
            case 1:
                reminderRepeatType = ReminderRepeatType.FOUR_TIMES_PER_DAY;
                break;
            case 2:
                reminderRepeatType = ReminderRepeatType.ONE_TIME_PER_DAY;
                break;
            default:
                reminderRepeatType = ReminderRepeatType.NONE;
                break;
        }

        switch (typeDisplayReminderSpinner.getSelectedItemPosition()) {
            case 0:
                reminderDisplayType = ReminderDisplayType.ONLY_NOTIFICATION;
                break;
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
