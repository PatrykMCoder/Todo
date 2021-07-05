package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmprogramms.todo.R;

public class PolicyDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View rootView = View.inflate(getContext(), R.layout.dialog_privacy, null);
        TextView privacyTextView = rootView.findViewById(R.id.privacy_text);

        String policyValue = "<h1 style='text-align: center;'><b>TodoNote Privacy Policy</b></h1>\n" +
                "        <p style='text-align: center;'><b>Information gathering and usage</b></p>\n" +
                "        When registering for TodoNote I ask for information such as your nickname and email address.<br/>\n" +
                "        TodoNote collects the email addresses of those who submit information through voluntary activities such as registrations. The user data I collect is used to improve. I only collect personal data that is required to provide our services, and I only store it insofar that it is necessary to deliver these services.<br/>\n" +
                "        <p style='text-align: center;'><b>Your data</b></p>\n" +
                "        TodoNote uses third party vendors and hosting partners to provide the necessary hardware, software, networking, storage, and related technology required to run TodoNote. Although TodoNote owns the code, and all rights to the TodoNote application, you retain all rights to your data. I will never share your personal data with a 3rd party without your prior authorization, and I will never sell data to 3rd parties. I transfer data with third-parties necessary to our ability to provide our services, all of whom are GDPR-compliant and provide the necessary safeguards required if they are outside of the EU.\n";
        privacyTextView.setText(Html.fromHtml(policyValue, Html.FROM_HTML_MODE_COMPACT));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(rootView);
        builder.setPositiveButton("Ok", (dialog, which) -> { });
        return builder.create();
    }
}
