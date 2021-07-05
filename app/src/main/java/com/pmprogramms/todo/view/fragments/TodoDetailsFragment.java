package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.todo.Todos;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentTodoDetailsBinding;
import com.pmprogramms.todo.helpers.text.TextFormat;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.EditTodoHelper;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.API.retrofit.todo.todo.edit.JSONHelperEditTodo;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.utils.reminders.ReminderHelper;
import com.pmprogramms.todo.view.dialogs.CreateReminderDialog;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// FIXME: 22/08/2021 make more readable this shit code :)

public class TodoDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private FragmentTodoDetailsBinding fragmentTodoDetailsBinding;
    private Context context;
    private MainActivity mainActivity;

    private TodoNoteViewModel todoNoteViewModel;

    private SharedPreferences remindersTitlePreference;

    private String todoID;
    private Data dataTodo;
    private String userToken;
    private boolean archive;

    private int tmpPosition;

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

        fragmentTodoDetailsBinding.editTODO.setOnClickListener(this);
        fragmentTodoDetailsBinding.archiveTODO.setOnClickListener(this);
        fragmentTodoDetailsBinding.deleteTODO.setOnClickListener(this);
        fragmentTodoDetailsBinding.archiveTODO.setOnClickListener(this);

        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        getTodoData();

        fragmentTodoDetailsBinding.swipeRefresh.setOnRefreshListener(() -> {
            fragmentTodoDetailsBinding.containerTodos.removeAllViews();
            getTodoData();
        });

        fragmentTodoDetailsBinding.scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
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
            if (jsonHelperTodo.data != null) {
                dataTodo = jsonHelperTodo.data.get(0);
                archive = dataTodo.archive;

                dataTodo.todos.sort((o1, o2) -> Boolean.compare(o1.done, o2.done));

                TextFormat textFormat = new TextFormat();
                fragmentTodoDetailsBinding.titlePreview.setText(jsonHelperTodo.data.get(0).title);
                fragmentTodoDetailsBinding.tag.setText(String.format("Tag: %s", textFormat.splitTextTag(jsonHelperTodo.data.get(0).tag)));
                fragmentTodoDetailsBinding.lastEdited.setText(textFormat.formatForTextLastEdit(mainActivity, jsonHelperTodo.data.get(0).updatedAt));
                int index = 0;
                for (Todos t : dataTodo.todos) {
                    createElements(t, index);
                    index++;
                }
                tmpPosition = jsonHelperTodo.data.get(0).todos.size();

                fragmentTodoDetailsBinding.reminderStatus.setImageResource(R.drawable.ic_outline_notifications_off_24);

                remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
                for (Map.Entry<String, ?> s :
                        remindersTitlePreference.getAll().entrySet()) {

                    if (s.getValue().equals(fragmentTodoDetailsBinding.titlePreview.getText().toString()))
                        fragmentTodoDetailsBinding.reminderStatus.setImageResource(R.drawable.ic_notifications_green_24dp);
                }

                initView();
            }
        });
    }

    private void initView() {
        fragmentTodoDetailsBinding.archiveTODO.setImageResource(dataTodo.archive ? R.drawable.ic_baseline_unarchive_24 : R.drawable.ic_archive_white_24dp);
        fragmentTodoDetailsBinding.containerTodos.setBackgroundColor(Color.parseColor(dataTodo.color));
        fragmentTodoDetailsBinding.containerScroll.setBackgroundColor(Color.parseColor(dataTodo.color));
    }

    private void createElements(Todos data, int index) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox doneCheckBox = new CheckBox(context);
        TextView taskTextView = new TextView(context);

        taskTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        taskTextView.setBackgroundColor(Color.TRANSPARENT);
        taskTextView.setTextSize(20);
        taskTextView.setTextColor(Color.BLACK);
        taskTextView.setPadding(0, 20, 0, 20);
        taskTextView.setBackgroundColor(Color.TRANSPARENT);

        taskTextView.setText(data.task);
        doneCheckBox.setChecked(data.done);

        taskTextView.setTag("t_" + index);
        doneCheckBox.setTag("d_" + index);

        doneCheckBox.setOnCheckedChangeListener(this);

        taskTextView.setPaintFlags(doneCheckBox.isChecked() ?
                taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        taskTextView.setTextColor(data.done ? Color.GRAY : Color.BLACK);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskTextView);

        fragmentTodoDetailsBinding.containerScroll.addView(linearLayout);
    }

    private void updateUI(boolean b, String tag) {
        TextView textView;
        textView = fragmentTodoDetailsBinding.getRoot().findViewWithTag(tag);
        textView.setPaintFlags(b ? textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        textView.setTextColor(b ? Color.GRAY : Color.BLACK);
    }

    private void updateTodoStatus() {
        JSONHelperEditTodo jhet;
        TextView taskTextView;
        CheckBox doneCheckBox;
        ArrayList<JSONHelperEditTodo> dataHelper = new ArrayList<>();
        for (int i = 0; i < tmpPosition; i++) {
            taskTextView = fragmentTodoDetailsBinding.getRoot().findViewWithTag("t_" + i);
            doneCheckBox = fragmentTodoDetailsBinding.getRoot().findViewWithTag("d_" + i);
            jhet = new JSONHelperEditTodo(taskTextView.getText().toString(), doneCheckBox.isChecked());
            dataHelper.add(jhet);
        }

        HashMap<String, String> map = new HashMap<>();
        if (TagsHelper.getTag() != null)
            map.put("tag", TagsHelper.getTag());
        map.put("todos", new Gson().toJson(dataHelper, new TypeToken<ArrayList<JSONHelperEditTodo>>() {
        }.getType()));

        todoNoteViewModel.updateSelectedTodo(todoID, map, userToken);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isPressed()) {
            updateTodoStatus();
            updateUI(b, compoundButton.getTag().toString().replace("d", "t"));
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.editTODO) {
            EditTodoHelper editTodoHelper = new EditTodoHelper(dataTodo);

            NavDirections navDirections = TodoDetailsFragmentDirections.actionTodoDetailsFragmentToEditTodoFragment(editTodoHelper);
            Navigation.findNavController(v).navigate(navDirections);

        } else if (id == R.id.create_reminder) {
            DialogFragment dialogFragment = new CreateReminderDialog();
            ReminderHelper.setTitle(fragmentTodoDetailsBinding.titlePreview.getText().toString());
            dialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "create reminder");
        } else if (id == R.id.archiveTODO) {
            archiveAction();
        } else if (id == R.id.deleteTODO) {
            todoNoteViewModel.deleteTodo(todoID, userToken).observe(getViewLifecycleOwner(), code -> {
                if (code == 200 || code == 201) {
                    Navigation.findNavController(v).navigate(R.id.todoFragment);
                } else {
                    new Messages(context).showMessage("Can't delete todo, try again later");
                }
            });
        }
    }

    private void archiveAction() {
        HashMap<String, Boolean> map = new HashMap<>();
        archive = !archive;
        map.put("archive", archive);

        todoNoteViewModel.archiveTodo(todoID, map, userToken).observe(getViewLifecycleOwner(), code -> {
            if (code == 200 || code == 201) {
                String msg = archive ? "Archive" : "Unarchive";
                fragmentTodoDetailsBinding.archiveTODO.setImageResource(archive ? R.drawable.ic_archive_white_24dp : R.drawable.ic_baseline_unarchive_24);
                new Messages(context).showMessage(msg);
            } else
                new Messages(context).showMessage("Something wrong, try again");
        });
    }
}
