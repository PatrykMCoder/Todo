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

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.search.TitleSearchHandle;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTitles;

import java.util.ArrayList;
import java.util.Set;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchListViewHolder> {

    private Set<JSONHelperTitles> data;
    private ArrayList<String> ids;
    private ArrayList<String> titles;
    private ArrayList<Boolean> archives;

    public SearchRecyclerViewAdapter(Set<JSONHelperTitles> data) {
        this.data = data;

        ids = new ArrayList<>();
        titles = new ArrayList<>();
        archives = new ArrayList<>();

        for (JSONHelperTitles t : data) {
            ids.add(t.id);
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

        holder.searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                TitleSearchHandle.setTitle(titles.get(position));
                TitleSearchHandle.setId(ids.get(position));
                TitleSearchHandle.setArchive(archives.get(position));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SearchListViewHolder extends RecyclerView.ViewHolder {
        private TextView resultTextView;
        private CardView searchCard;
        private ImageView archiveStatusImage;

        public SearchListViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(R.id.search_result_text);
            searchCard = itemView.findViewById(R.id.card_view);
            archiveStatusImage = itemView.findViewById(R.id.archive_status);
        }
    }
}
