package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.api.retrofit.todo.todo.Data;
import com.pmprogramms.todo.api.retrofit.todo.todo.Todos;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentTodoDetailsBinding;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.EditTodoHelper;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.api.retrofit.todo.todo.edit.JSONHelperEditTodo;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.utils.recyclerView.TodoDetailsRecyclerViewAdapter;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.reminders.ReminderHelper;
import com.pmprogramms.todo.view.dialogs.CreateReminderDialog;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME: 22/08/2021 make more readable this shit code :)

public class TodoDetailsFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, TodoDetailsRecyclerViewAdapter.onItemClickListener {
    private FragmentTodoDetailsBinding fragmentTodoDetailsBinding;
    private Context context;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;

    private TodoNoteViewModel todoNoteViewModel;

    private SharedPreferences remindersTitlePreference;

    private String todoID;
    private Data dataTodo;
    private String userToken;
    private boolean archive;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        todoID = TodoDetailsFragmentArgs.fromBundle(getArguments()).getTodoID();
        userToken = new UserData(context).getUserToken();

        fragmentTodoDetailsBinding = FragmentTodoDetailsBinding.inflate(inflater);
        recyclerView = fragmentTodoDetailsBinding.todosRecycler;
        fragmentTodoDetailsBinding.editTODO.setOnClickListener(this);
        fragmentTodoDetailsBinding.deleteTODO.setOnClickListener(this);
        fragmentTodoDetailsBinding.deleteTODO.setOnLongClickListener(this);
        fragmentTodoDetailsBinding.appBar.archiveButton.setOnClickListener(this);
        fragmentTodoDetailsBinding.appBar.notificationButton.setOnClickListener(this);
        fragmentTodoDetailsBinding.appBar.backButton.setOnClickListener(v -> mainActivity.onBackPressed());

        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);
        getTodoData();

        fragmentTodoDetailsBinding.swipeRefresh.setOnRefreshListener(() -> {
            getTodoData();
            fragmentTodoDetailsBinding.swipeRefresh.setRefreshing(false);
        });

        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            fragmentTodoDetailsBinding.swipeRefresh.setEnabled(scrollY == 0);
            if (scrollY > oldScrollY) {
                if (fragmentTodoDetailsBinding.menuCard.getVisibility() != View.INVISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_down);
                    fragmentTodoDetailsBinding.menuCard.setVisibility(View.INVISIBLE);
                    fragmentTodoDetailsBinding.menuCard.startAnimation(animation);
                }
            } else if (scrollY < oldScrollY) {
                if (fragmentTodoDetailsBinding.menuCard.getVisibility() != View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.menu_card_slide_up);
                    fragmentTodoDetailsBinding.menuCard.setVisibility(View.VISIBLE);
                    fragmentTodoDetailsBinding.menuCard.startAnimation(animation);
                }
            }
        });

        return fragmentTodoDetailsBinding.getRoot();
    }

    private void getTodoData() {
        todoNoteViewModel.getSelectedTodo(todoID, userToken).observe(getViewLifecycleOwner(), jsonHelperTodo -> {
            Log.d("TAG", "getTodoData: " + jsonHelperTodo);
            if (jsonHelperTodo.data != null) {
                dataTodo = jsonHelperTodo.data.get(0);
                archive = dataTodo.archive;

                dataTodo.todos.sort((o1, o2) -> Boolean.compare(o1.done, o2.done));

                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);

                TodoDetailsRecyclerViewAdapter todoDetailsRecyclerViewAdapter = new TodoDetailsRecyclerViewAdapter(dataTodo.todos);
                todoDetailsRecyclerViewAdapter.setListener(this);
                recyclerView.setAdapter(todoDetailsRecyclerViewAdapter);

                TextFormat textFormat = new TextFormat();
                fragmentTodoDetailsBinding.appBar.titleTodo.setText(jsonHelperTodo.data.get(0).title);
                fragmentTodoDetailsBinding.tag.setText(String.format("Tag: %s", textFormat.splitTextTag(jsonHelperTodo.data.get(0).tag)));
                fragmentTodoDetailsBinding.lastEdited.setText(textFormat.formatForTextLastEdit(mainActivity, jsonHelperTodo.data.get(0).updatedAt));
                fragmentTodoDetailsBinding.appBar.notificationButton.setImageResource(R.drawable.ic_outline_notifications_off_24);

                remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
                for (Map.Entry<String, ?> s :
                        remindersTitlePreference.getAll().entrySet()) {

                    if (s.getValue().equals(fragmentTodoDetailsBinding.appBar.titleTodo.getText().toString()))
                        fragmentTodoDetailsBinding.appBar.notificationButton.setImageResource(R.drawable.ic_notifications_green_24dp);
                }
            }

            fragmentTodoDetailsBinding.appBar.container.setBackgroundColor(Color.parseColor(dataTodo.color));
            fragmentTodoDetailsBinding.appBar.archiveButton.setImageResource(dataTodo.archive ? R.drawable.ic_baseline_unarchive_24 : R.drawable.ic_baseline_archive_24);
            fragmentTodoDetailsBinding.containerTodos.setBackgroundColor(Color.parseColor(dataTodo.color));
        });
    }

    private void updateTodoStatus( List<Todos> todos) {
        HashMap<String, String> map = new HashMap<>();
        if (TagsHelper.getTag() != null)
            map.put("tag", TagsHelper.getTag());
        map.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperEditTodo>>() {
        }.getType()));

        todoNoteViewModel.updateSelectedTodo(todoID, map, userToken);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit_TODO) {
            EditTodoHelper editTodoHelper = new EditTodoHelper(dataTodo);

            NavDirections navDirections = TodoDetailsFragmentDirections.actionTodoDetailsFragmentToEditTodoFragment(editTodoHelper);
            Navigation.findNavController(v).navigate(navDirections);

        } else if (id == R.id.notification_button) {
            DialogFragment dialogFragment = new CreateReminderDialog();
            ReminderHelper.setTitle(fragmentTodoDetailsBinding.appBar.titleTodo.getText().toString());
            dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "create reminder");
        } else if (id == R.id.archive_button) {
            archiveAction();
        } else if (id == R.id.delete_TODO) {
            new Messages(context).showMessage("For confirm delete, please long press on this button");
        }
    }

    private void archiveAction() {
        HashMap<String, Boolean> map = new HashMap<>();
        archive = !archive;
        map.put("archive", archive);

        todoNoteViewModel.archiveTodo(todoID, map, userToken).observe(getViewLifecycleOwner(), code -> {
            if (code == 200 || code == 201) {
                String msg = archive ? "Archive" : "Unarchive";
                fragmentTodoDetailsBinding.appBar.archiveButton.setImageResource(archive ? R.drawable.ic_baseline_unarchive_24 : R.drawable.ic_baseline_archive_24);
                new Messages(context).showMessage(msg);
            } else
                new Messages(context).showMessage("Something wrong, try again");
        });
    }

    @Override
    public boolean onLongClick(View view) {
        if(view.getId() == R.id.delete_TODO) {
            todoNoteViewModel.deleteTodo(todoID, userToken).observe(getViewLifecycleOwner(), code -> {
                if (code == 200 || code == 201) {
                    Navigation.findNavController(view).navigate(R.id.todoFragment);
                } else {
                    new Messages(context).showMessage("Can't delete todo, try again later");
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(int position, boolean value) {
        List<Todos> todos = dataTodo.todos;
        todos.get(position).done = value;

        updateTodoStatus(todos);
    }
}
