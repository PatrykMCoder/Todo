package com.pmprogramms.todo;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderAlarmActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "ReminderAlarmActivity";
    private TextView titleTextView;
    private ImageView animatedView;
    private Button buttonOpenApp;
    private Uri uriSongAlarm;

    private MediaPlayer mediaPlayer;

    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_alarm);

        titleTextView = findViewById(R.id.title_todo);
        buttonOpenApp = findViewById(R.id.open_app);

        titleTextView.setText("Reminder for: \n" + getIntent().getStringExtra("todoName"));
        buttonOpenApp.setOnClickListener(this);

        mediaPlayer = setUpAlarmSong();

        if (mediaPlayer == null) {
            Toast.makeText(this, "Can't play song :(", Toast.LENGTH_SHORT).show();
        } else
            mediaPlayer.start();
    }

    private MediaPlayer setUpAlarmSong() {
        MediaPlayer mediaPlayer;
        uriSongAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (uriSongAlarm == null)
            return null;

        mediaPlayer = MediaPlayer.create(this, uriSongAlarm);
        mediaPlayer.setLooping(true);
        return mediaPlayer;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_app: {
                mediaPlayer.stop();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
