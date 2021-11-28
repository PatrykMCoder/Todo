package com.pmprogramms.todo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_alarm);

        TextView titleTextView = findViewById(R.id.title_todo);
        Button buttonOpenApp = findViewById(R.id.open_app);

        titleTextView.setText(getResources().getString(R.string.reminder_for), TextView.BufferType.valueOf(getIntent().getStringExtra("todoName")));
        buttonOpenApp.setOnClickListener(this);

        mediaPlayer = setUpAlarmSong();

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private MediaPlayer setUpAlarmSong() {
        MediaPlayer mediaPlayer;
        Uri uriSongAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (uriSongAlarm == null)
            return null;

        mediaPlayer = MediaPlayer.create(this, uriSongAlarm);
        mediaPlayer.setLooping(true);
        return mediaPlayer;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.open_app) {
            mediaPlayer.stop();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
