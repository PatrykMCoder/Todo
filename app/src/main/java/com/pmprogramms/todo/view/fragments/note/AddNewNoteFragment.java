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
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.text.TypeFormat;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewNoteFragment extends Fragment {

    private EditText titleEditText, contentsEditText;
    private ImageButton boldImageButton, italicImageButton, underLineImageButton, strikethroughImageButton;

    private View rootView;
    private ProgressDialog progressDialog;
    private FloatingActionButton saveNoteButton;

    private Context context;
    private MainActivity mainActivity;

    private API api;
    private String userToken;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_new_note, container, false);

        titleEditText = rootView.findViewById(R.id.new_title_note);
        contentsEditText = rootView.findViewById(R.id.new_contents_note);
        boldImageButton = rootView.findViewById(R.id.bold_edit_button);
        italicImageButton = rootView.findViewById(R.id.italic_edit_button);
        underLineImageButton = rootView.findViewById(R.id.under_line_edit_button);
        strikethroughImageButton = rootView.findViewById(R.id.strikethrough_edit_button);

        saveNoteButton = rootView.findViewById(R.id.save_note);
        userToken = new UserData(context).getUserToken();

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

        saveNoteButton.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(context, "Save...", "Please wait..");

            String title = titleEditText.getText().toString().trim();
            String contents = Html.toHtml(contentsEditText.getText());

            HashMap<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("contents", contents);

            saveNote(map);
        });

        return rootView;
    }

    private void saveNote(HashMap<String, String> data) {
        api = Client.getInstance().create(API.class);

        Call<Void> call = api.createNewNote(data, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    mainActivity.closeFragment(AddNewNoteFragment.this, new NoteFragment());
                } else
                    new Messages(context).showMessage(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.cancel();
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }
}
