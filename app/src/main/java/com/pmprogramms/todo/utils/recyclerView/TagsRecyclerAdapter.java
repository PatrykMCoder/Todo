package com.pmprogramms.todo.utils.recyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.user.UserData;

import java.util.ArrayList;

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
            RemoveTagAsync removeTagAsync = new RemoveTagAsync();
            removeTagAsync.execute(new UserData(context).getUserID(), tagsID.get(position), String.valueOf(position));
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

    class RemoveTagAsync extends AsyncTask<String, String, TaskState> {
        private int position;
        @Override
        protected TaskState doInBackground(String... strings) {
            APIClient APIClient = new APIClient();
            position = Integer.parseInt(strings[2]);
            int code = APIClient.deleteCustomTag(strings[0], strings[1]);
            if (code == 200 || code == 201) {
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    notifyItemRemoved(position);
                    tags.remove(position);
                    tagsID.remove(position);
                    break;
                }
                case NOT_DONE: {
                    new Handler().post(() -> {
                       Toast.makeText(context, "Something wrong, try again", Toast.LENGTH_SHORT).show();
                    });
                    break;
                }
            }
        }
    }
}
