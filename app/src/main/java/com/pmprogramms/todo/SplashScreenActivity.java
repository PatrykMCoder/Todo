package com.pmprogramms.todo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pmprogramms.todo.helpers.tools.Network;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.note.pdf.PDFGenerator;
import com.pmprogramms.todo.utils.notifications.Notification;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Notification notification = new Notification(getApplicationContext());
        notification.createNotificationsChanel();

        PDFGenerator pdfGenerator = new PDFGenerator();
        pdfGenerator.generateSpaceForPDF(this);

        launchActivity();
    }

    private void launchActivity() {
        Intent intent;
        if (new Network(this).connected()) {
            if (new UserData(this).getUserToken() != null)
                intent = new Intent(this, MainActivity.class);
            else
                intent = new Intent(this, LoginActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            showAlertDialog();
        }

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Needed internet connection")
                .setMessage("Please, check your internet connection")
                .setPositiveButton("Try again", (dialog, which) -> launchActivity())
                .setIcon(R.drawable.ic_baseline_warning_24);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
