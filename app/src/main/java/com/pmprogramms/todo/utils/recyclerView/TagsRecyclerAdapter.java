package com.pmprogramms.todo.utils.recyclerView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagsRecyclerAdapter extends RecyclerView.Adapter<TagsRecyclerAdapter.TagRecyclerListHolder> {

    private Activity mainActivity;
    private Context context;
    private ArrayList<String> tags;
    private ArrayList<String> tagsID;

    public TagsRecyclerAdapter(Context context, ArrayList<String> tags, ArrayList<String> tagsID) {
        this.context = context;
        this.tags = tags;
        this.tagsID = tagsID;

        mainActivity = (Activity) context;
    }

    @NonNull
    @Override
    public TagRecyclerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new TagRecyclerListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagRecyclerListHolder holder, int position) {
        holder.textView.setText(tags.get(position));
        holder.removeButton.setOnClickListener(v -> {
            API api = Client.getInstance().create(API.class);
            Call<Void> call = api.deleteCustomTag(new UserData(context).getUserID(), tagsID.get(position));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(!response.isSuccessful()) {
                        new Messages(context).showMessage(response.message());
                    }

                    if(response.code() == 200 || response.code() == 201) {
                        notifyItemRemoved(position);
                        tags.remove(position);
                        tagsID.remove(position);
                    } else {
                        new Messages(context).showMessage("Something wrong, try again");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    new Messages(context).showMessage(t.getMessage());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return tags != null ? tags.size() : 0;
    }


    static class TagRecyclerListHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CardView tagDataHolderCard;
        private ImageButton removeButton;
        public TagRecyclerListHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tag_text);
            removeButton = itemView.findViewById(R.id.remove_tag_button);
            tagDataHolderCard = itemView.findViewById(R.id.card_view);
        }
    }
}
