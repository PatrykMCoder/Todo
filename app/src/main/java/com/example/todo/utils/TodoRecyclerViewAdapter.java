package com.example.todo.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.utils.reminders.ReminderHelper;
import com.example.todo.view.fragments.TodoDetailsFragment;
import com.example.todo.view.dialogs.CreateReminderDialog;
import com.example.todo.utils.objects.TodoObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoListViewHolder> {
    private ArrayList<TodoObject> data;
    private TodoAdapter todoAdapter;
    private final static String TAG = "TODORECYCLERVIEW";
    private MainActivity mainActivity;
    private String description;
    private int done, id, counter;

    private float percentDone = 0;

    private ArrayList<String> titles;

    private File file;
    private File[] listFile;

    private Context context;

    public TodoRecyclerViewAdapter(ArrayList<TodoObject> data) {
        this.data = data;
    }

    public TodoRecyclerViewAdapter(Context context) {
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
        boolean checkNan;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        holder.titleTextView.setText(titles.get(position).replace("_", " "));
        holder.cardView.setOnClickListener(view -> {
            String title = holder.titleTextView.getText().toString();
            title = title.replace(" ", "_");;
            mainActivity.initFragment(new TodoDetailsFragment(title), true);
        });

        todoAdapter = new TodoAdapter(context, titles.get(position).replace("_", " "));
        todoAdapter.openDB();
        percentDone = todoAdapter.getPercentDoneTask();
        todoAdapter.closeDB();

        holder.allTaskDoneImageView.setVisibility(percentDone >= 100                                  ? View.VISIBLE : View.GONE);
        holder.allTaskDoneImageView.setImageResource((checkNan = Double.valueOf(percentDone).isNaN()) ? R.drawable.ic_error_red_24dp : R.drawable.ic_done_green_24dp);
        holder.percentProgressBar.setVisibility(percentDone < 100                                     ? View.VISIBLE : View.GONE);

        holder.percentTaskTextView.setText(String.format("%s %%", Math.floor(percentDone)));
        holder.percentProgressBar.setProgress((int) percentDone);

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

    static class TodoListViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView percentTaskTextView;
        private ProgressBar percentProgressBar;
        private CheckBox doneCheckBox;
        // public LinearLayout layoutItemTodo;
        private CardView cardView;
        private ImageView allTaskDoneImageView;

        TodoListViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.todoTitle);
            cardView = itemView.findViewById(R.id.cardView);
            percentTaskTextView = itemView.findViewById(R.id.percentDoneTask);
            percentProgressBar = itemView.findViewById(R.id.percentProgress);
            allTaskDoneImageView = itemView.findViewById(R.id.all_task_done_image);
        }
    }
}