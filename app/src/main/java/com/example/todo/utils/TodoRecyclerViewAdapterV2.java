package com.example.todo.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.database.TodoAdapterV2;
import com.example.todo.fragments.TodoDetailsFragment;
import com.example.todo.fragments.TodoFragment;
import com.example.todo.utils.objects.TodoObject;
import com.example.todo.utils.setteings.Settings;

import java.io.File;
import java.util.ArrayList;

public class TodoRecyclerViewAdapterV2 extends RecyclerView.Adapter<TodoRecyclerViewAdapterV2.TodoListViewHolder> {
    private ArrayList<TodoObject> data;
    private final static String TAG = "TODORECYCLERVIEW";
    private MainActivity mainActivity;
    private String description;
    private int done, id, counter;

    private ArrayList<String> titles;

    private File file;
    private File[] listFile;

    private Context context;

    public TodoRecyclerViewAdapterV2(ArrayList<TodoObject> data) {
        this.data = data;
    }

    public TodoRecyclerViewAdapterV2(Context context) {
        this.context = context;

        // TODO: 2020-02-18 update else statement
        String path = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            path = context.getDataDir() + "/databases";
        }else{
            path = "";
        }

        file = new File(path);
        listFile = file.listFiles();
    }

    @NonNull
    @Override
    public TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new TodoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TodoListViewHolder holder, final int position) {
        mainActivity = (MainActivity) context;

        holder.titleTextView.setText(titles.get(position).replace("_", " "));

        holder.cardView.setOnClickListener(view -> {
            String title = holder.titleTextView.getText().toString();
            title =title.replace(" ", "_");;
            mainActivity.initFragment(new TodoDetailsFragment(title), true);
        });

        holder.cardView.setOnLongClickListener(view -> true);
    }

    @Override
    public int getItemCount() {
        counter = 0;
        titles = new ArrayList<>();
        if(listFile != null)
            for(File f: listFile){
                String name = f.getName();
                String nameWithoutExtension;
                nameWithoutExtension = name.split("\\.")[0];
                titles.add(nameWithoutExtension);
                if(name.endsWith(".db")){
                    counter++;
                }
            }

        Log.d(TAG, "onBindViewHolder: " + titles);
        return counter;
    }

    public class TodoListViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public CheckBox doneCheckBox;
        // public LinearLayout layoutItemTodo;
        public CardView cardView;

        public TodoListViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.todoTitle);
            descriptionTextView = itemView.findViewById(R.id.todoDescription);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}