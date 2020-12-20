package com.pmprogramms.todo.view.fragments.note;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.text.TypeFormat;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNoteFragment extends Fragment implements View.OnClickListener {
    private String title, contents, userToken, noteID;

    private Context context;
    private MainActivity mainActivity;

    private EditText titleEditText, contentsEditText;
    private FloatingActionButton saveButton;
    private ProgressDialog progressDialog;
    private ImageButton boldImageButton, italicImageButton,underLineImageButton, strikethroughImageButton;

    public EditNoteFragment(String title, String contents, String noteID) {
        this.title = title;
        this.contents = contents;
        this.noteID = noteID;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userToken = new UserData(context).getUserToken();
        View rootView = inflater.inflate(R.layout.fragment_edit_note, container, false);

        titleEditText = rootView.findViewById(R.id.title_edit);
        contentsEditText = rootView.findViewById(R.id.edit_contents);
        boldImageButton = rootView.findViewById(R.id.bold_edit_button);
        italicImageButton = rootView.findViewById(R.id.italic_edit_button);
        underLineImageButton = rootView.findViewById(R.id.under_line_edit_button);
        strikethroughImageButton = rootView.findViewById(R.id.strikethrough_edit_button);

        saveButton = rootView.findViewById(R.id.save_note);
        saveButton.setOnClickListener(this);

        boldImageButton.setOnClickListener(v -> {
            TextFormat textFormat = new TextFormat();
            int startSelected = contentsEditText.getSelectionStart();
            int endSelected = contentsEditText.getSelectionEnd();

            String allText = contentsEditText.getText().toString();
            String selectedText = allText.substring(startSelected, endSelected);
            String formattedText = textFormat.formatSelectedText(selectedText, TypeFormat.BOLD);
            contentsEditText.getEditableText().replace(startSelected, endSelected, Html.fromHtml(formattedText));
        });

        italicImageButton.setOnClickListener(v -> {
            TextFormat textFormat = new TextFormat();
            int startSelected = contentsEditText.getSelectionStart();
            int endSelected = contentsEditText.getSelectionEnd();

            String allText = contentsEditText.getText().toString();
            String selectedText = allText.substring(startSelected, endSelected);
            String formattedText = textFormat.formatSelectedText(selectedText, TypeFormat.ITALIC);
            contentsEditText.getEditableText().replace(startSelected, endSelected, Html.fromHtml(formattedText));
        });

        underLineImageButton.setOnClickListener(v -> {
            TextFormat textFormat = new TextFormat();
            int startSelected = contentsEditText.getSelectionStart();
            int endSelected = contentsEditText.getSelectionEnd();

            String allText = contentsEditText.getText().toString();
            String selectedText = allText.substring(startSelected, endSelected);
            String formattedText = textFormat.formatSelectedText(selectedText, TypeFormat.UNDER_LINE);
            contentsEditText.getEditableText().replace(startSelected, endSelected, Html.fromHtml(formattedText));
        });

        strikethroughImageButton.setOnClickListener(v -> {
            TextFormat textFormat = new TextFormat();
            int startSelected = contentsEditText.getSelectionStart();
            int endSelected = contentsEditText.getSelectionEnd();

            String allText = contentsEditText.getText().toString();
            String selectedText = allText.substring(startSelected, endSelected);
            String formattedText = textFormat.formatSelectedText(selectedText, TypeFormat.STRIKETHROUGH);
            contentsEditText.getEditableText().replace(startSelected, endSelected, Html.fromHtml(formattedText));
        });

        titleEditText.setText(title);
        contentsEditText.setText(Html.fromHtml(contents));

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_note) {
            progressDialog = ProgressDialog.show(context, "Save...", "Please wait..");
            title = titleEditText.getText().toString().trim();

            HashMap<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("contents", Html.toHtml(contentsEditText.getText()));
            editNote(map);
        }
    }

    private void editNote(HashMap<String, String> data) {
        API api = Client.getInstance().create(API.class);
        Call<Void> call = api.editNote(noteID, data, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    new Messages(context).showMessage("Note updated");
                    mainActivity.closeFragment(EditNoteFragment.this, new NotePreviewFragment(noteID));
                } else {
                    progressDialog.cancel();
                    new Messages(context).showMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.cancel();
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }
}
