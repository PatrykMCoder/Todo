package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.R;

import java.util.PrimitiveIterator;

public class PolicyDialog extends DialogFragment {
    private TextView privacyTextView;
    //todo move to string.xml
    private String policyValue = "<h1 style='text-align: center;'><b>TodoNote Privacy Policy</b></h1>\n" +
            "        <p style='text-align: center;'><b>Information gathering and usage</b></p>\n" +
            "        When registering for TodoNote I ask for information such as your nickname and email address.<br/>\n" +
            "        TodoNote collects the email addresses of those who submit information through voluntary activities such as registrations. The user data I collect is used to improve. I only collect personal data that is required to provide our services, and I only store it insofar that it is necessary to deliver these services.<br/>\n" +
            "        <p style='text-align: center;'><b>Your data</b></p>\n" +
            "        TodoNote uses third party vendors and hosting partners to provide the necessary hardware, software, networking, storage, and related technology required to run TodoNote. Although TodoNote owns the code, and all rights to the TodoNote application, you retain all rights to your data. I will never share your personal data with a 3rd party without your prior authorization, and I will never sell data to 3rd parties. I transfer data with third-parties necessary to our ability to provide our services, all of whom are GDPR-compliant and provide the necessary safeguards required if they are outside of the EU.\n" +
            "        <p style='text-align: center;'><b>Ad servers</b></p>\n" +
            "        I do not partner with or have special relationships with any ad server companies.";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View rootView = View.inflate(getContext(), R.layout.dialog_privacy, null);
        privacyTextView = rootView.findViewById(R.id.privacy_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            privacyTextView.setText(Html.fromHtml(policyValue, Html.FROM_HTML_MODE_COMPACT));
        } else {
            privacyTextView.setText(Html.fromHtml(policyValue));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(rootView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });
        return builder.create();
    }
}
