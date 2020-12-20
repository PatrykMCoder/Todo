package com.pmprogramms.todo.utils.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.API.retrofit.note.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.view.fragments.note.NotePreviewFragment;

import java.util.ArrayList;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.NoteListViewHolder> {

    private final ArrayList<Data> arrayNotes;
    private final Context context;
    private final String userID;

    private MainActivity mainActivity;

    public NoteRecyclerViewAdapter(Context context, ArrayList<Data> arrayNotes, String userID) {
        this.arrayNotes = arrayNotes;
        this.context = context;
        this.userID = userID;
    }

    @NonNull
    @Override
    public NoteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new NoteListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListViewHolder holder, int position) {
        holder.titleTextView.setText(arrayNotes.get(position).title);

        holder.cardView.setOnClickListener(v -> {
                mainActivity = (MainActivity) context;
                mainActivity.initFragment(new NotePreviewFragment(arrayNotes.get(position)._id), true);
        });
    }

    @Override
    public int getItemCount() {
        return arrayNotes != null ? arrayNotes.size() : 0;
    }

    class NoteListViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private CardView cardView;

        NoteListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
