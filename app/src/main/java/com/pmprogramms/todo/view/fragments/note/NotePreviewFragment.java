package com.pmprogramms.todo.view.fragments.note;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.note.JSONNoteHelper;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.note.pdf.PDFGenerator;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.DeleteNoteAskDialog;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.FileProvider.getUriForFile;


public class NotePreviewFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private MainActivity mainActivity;

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView titleTextView, contentsTextView, lastEditedTextView;
    private FloatingActionMenu menuButton;
    private FloatingActionButton deleteNoteButton;
    private FloatingActionButton editNoteButton;
    private FloatingActionButton archiveNoteButton;
    private FloatingActionButton generatePDFNote;
    private CardView detailsCardView;
    private ScrollView scrollView;

    private String userToken;
    private String noteID;
    private boolean archive;
    private boolean tmpArchive;
    private JSONNoteHelper jsonNoteHelper;

    private API api;

    public NotePreviewFragment(String noteID) {
        this.noteID = noteID;
    }

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
        view = inflater.inflate(R.layout.fragment_note_preview, container, false);
        api = Client.getInstance().create(API.class);
        userToken = new UserData(context).getUserToken();

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        titleTextView = view.findViewById(R.id.title_preview);
        contentsTextView = view.findViewById(R.id.contents_preview);
        lastEditedTextView = view.findViewById(R.id.last_edited);
        deleteNoteButton = view.findViewById(R.id.delete_note);
        editNoteButton = view.findViewById(R.id.edit_note);
        archiveNoteButton = view.findViewById(R.id.archive_note);
        generatePDFNote = view.findViewById(R.id.generate_pdf_note);
        scrollView = view.findViewById(R.id.note_scroll);
        menuButton = view.findViewById(R.id.menu);
        detailsCardView = view.findViewById(R.id.details);

        deleteNoteButton.setOnClickListener(this);
        editNoteButton.setOnClickListener(this);
        archiveNoteButton.setOnClickListener(this);
        generatePDFNote.setOnClickListener(this);

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipeRefreshLayout.setEnabled(scrollY == 0);
            if (scrollY > oldScrollY) {
                if (detailsCardView.getVisibility() != View.INVISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_down);
                    detailsCardView.setVisibility(View.INVISIBLE);
                    menuButton.setVisibility(View.INVISIBLE);
                    detailsCardView.startAnimation(animation);
                }
            } else if (scrollY < oldScrollY) {
                if (detailsCardView.getVisibility() != View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_up);
                    detailsCardView.setVisibility(View.VISIBLE);
                    menuButton.setVisibility(View.VISIBLE);
                    detailsCardView.startAnimation(animation);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::loadNoteData);
        loadNoteData();
        return view;
    }

    private void getDataToShow() {
        titleTextView.setText(jsonNoteHelper.data.get(0).title);
        contentsTextView.setText(Html.fromHtml(jsonNoteHelper.data.get(0).contents));
        lastEditedTextView.setText(new TextFormat().formatForTextLastEdit(mainActivity, jsonNoteHelper.data.get(0).updatedAt));

        tmpArchive = archive = jsonNoteHelper.data.get(0).archive;
        archiveNoteButton.setImageResource(archive ? R.drawable.ic_baseline_unarchive_24 : R.drawable.ic_archive_white_24dp);
    }

    private void loadNoteData() {
        API api = Client.getInstance().create(API.class);

        Call<JSONNoteHelper> call = api.getUserNoteData(noteID, userToken);
        call.enqueue(new Callback<JSONNoteHelper>() {
            @Override
            public void onResponse(Call<JSONNoteHelper> call, Response<JSONNoteHelper> response) {
                if (response.isSuccessful()) {
                    jsonNoteHelper = response.body();
                    getDataToShow();
                } else
                    new Messages(context).showMessage(response.message());
            }

            @Override
            public void onFailure(Call<JSONNoteHelper> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.delete_note) {
            DialogFragment deleteNoteFragment = new DeleteNoteAskDialog(context, mainActivity, this, titleTextView.getText().toString().trim(), noteID);
            deleteNoteFragment.show(mainActivity.getSupportFragmentManager(), "Delete note");
        } else if (id == R.id.edit_note) {
            mainActivity.initFragment(new EditNoteFragment(titleTextView.getText().toString().trim(), jsonNoteHelper.data.get(0).contents, noteID), true);
        } else if (id == R.id.archive_note) {
            archiveAction();
        } else if (id == R.id.generate_pdf_note) {
            generatePDFNote();
        }
    }

    private void generatePDFNote() {
        boolean pdfCreated = new PDFGenerator().generatePDF(context, titleTextView.getText().toString(), view, contentsTextView);
        if (pdfCreated) {
            Intent openPDFIntent = new Intent();
            File pdfFolderPath = new File(context.getFilesDir(), PDFGenerator.mainFolderName);
            File pdfFile = new File(pdfFolderPath, titleTextView.getText().toString() + ".pdf");
            Uri pdfContentUri = getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);
            openPDFIntent.setDataAndType(pdfContentUri, "application/pdf");
            startActivity(openPDFIntent);
        } else
            new Messages(context).showMessage("Something wrong, try again");
    }

    private void archiveAction() {
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("archive", !tmpArchive);
        Call<Void> call = api.archiveNoteAction(noteID, map, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage("Something wrong, try again");
                }

                new Messages(context).showMessage(tmpArchive ? "Unarchive" : "Archive");
                archiveNoteButton.setImageResource(archive ? R.drawable.ic_archive_white_24dp : R.drawable.ic_baseline_unarchive_24);

                archive = !tmpArchive;
                tmpArchive = archive;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                new Messages(context).showMessage("Something wrong, try again");
            }
        });
    }
}
