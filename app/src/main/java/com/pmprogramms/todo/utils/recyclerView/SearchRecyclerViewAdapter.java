package com.pmprogramms.todo.utils.recyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.api.retrofit.todo.todo.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;

import java.util.ArrayList;
import java.util.Set;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchListViewHolder> {

    private final Set<Data> data;
    private final ArrayList<String> ids;
    private final ArrayList<String> titles;
    private final ArrayList<Boolean> archives;

    public SearchRecyclerViewAdapter(Set<Data> data) {
        this.data = data;

        ids = new ArrayList<>();
        titles = new ArrayList<>();
        archives = new ArrayList<>();

        for (Data t : data) {
            ids.add(t._id);
            titles.add(t.title);
            archives.add(t.archive);
        }
    }

    @NonNull
    @Override
    public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new SearchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListViewHolder holder, int position) {
        holder.resultTextView.setText(titles.get(position));
        if (archives.get(position))
            holder.archiveStatusImage.setImageResource(R.drawable.ic_outline_archive_24);

        holder.searchCard.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra("id", ids.get(position));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SearchListViewHolder extends RecyclerView.ViewHolder {
        private final TextView resultTextView;
        private final CardView searchCard;
        private final ImageView archiveStatusImage;

        public SearchListViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(R.id.search_result_text);
            searchCard = itemView.findViewById(R.id.card_view);
            archiveStatusImage = itemView.findViewById(R.id.archive_status);
        }
    }
}
