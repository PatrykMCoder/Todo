package com.example.todo.utils.recyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.search.TitleSearchHandle;
import com.example.todo.API.jsonhelper.JSONHelperLoadTitles;

import java.util.ArrayList;
import java.util.Set;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchListViewHolder> {

    private Set<JSONHelperLoadTitles> data;
    private ArrayList<String> ids;
    private ArrayList<String> titles;

    public SearchRecyclerViewAdapter(Set<JSONHelperLoadTitles> data) {
        this.data = data;

        ids = new ArrayList<>();
        titles = new ArrayList<>();

        for(JSONHelperLoadTitles t : data){
            ids.add(t.id);
            titles.add(t.title);
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

        holder.searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                TitleSearchHandle.setTitle(titles.get(position));
                TitleSearchHandle.setId(ids.get(position));
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

        public SearchListViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(R.id.search_result_text);
            searchCard = itemView.findViewById(R.id.card_view);
        }
    }
}
