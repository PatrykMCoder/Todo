package com.pmprogramms.todo.view.fragments;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentTodoBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.utils.recyclerView.TodoRecyclerViewAdapter;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;

public class TodoFragment extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    private String userToken;
    private TodoNoteViewModel todoNoteViewModel;
    private FragmentTodoBinding fragmentTodoBinding;
    private TodoFragmentArgs args;

    private ArrayList<Data> todosData;
    private TodoRecyclerViewAdapter todoRecyclerViewAdapter;

    public TodoFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);
        userToken = new UserData(requireContext()).getUserToken();
        args = TodoFragmentArgs.fromBundle(getArguments());

        fragmentTodoBinding = FragmentTodoBinding.inflate(inflater);
        fragmentTodoBinding.addNewTodo.setOnClickListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        fragmentTodoBinding.todoListRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(fragmentTodoBinding.todoListRecyclerView);

        fragmentTodoBinding.todoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fragmentTodoBinding.refreshSwipe.setEnabled(dy <= 0);
            }
        });

        fragmentTodoBinding.refreshSwipe.setOnRefreshListener(this::getData);

        if (args.getDetailsIDFromSearch() != null) {
            NavDirections directions = TodoFragmentDirections.actionTodoFragmentToTodoDetailsFragment(args.getDetailsIDFromSearch());
            Navigation.findNavController(fragmentTodoBinding.getRoot()).navigate(directions);
        }

        getData();

        return fragmentTodoBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        new HideAppBarHelper(mainActivity).showBar();
    }

    private void getData() {
        userToken = new UserData(requireContext()).getUserToken();
        todoNoteViewModel.getAllTodos(false, userToken).observe(getViewLifecycleOwner(), jsonHelperTodo -> {
            if (jsonHelperTodo != null) {
                todosData = jsonHelperTodo.data;
                initRecyclerView(todosData);
            }
        });
    }


    private void initRecyclerView(ArrayList<Data> dataTodo) {
        todoRecyclerViewAdapter = new TodoRecyclerViewAdapter(dataTodo);
        fragmentTodoBinding.todoListRecyclerView.setAdapter(todoRecyclerViewAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_todo) {
            NavDirections navDirections = TodoFragmentDirections.actionTodoFragmentToAddNewTodoFragment(userToken);
            Navigation.findNavController(view).navigate(navDirections);
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable runnable;
        private Data deletedItem;
        private int deletedItemPosition;

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                deleteAction(viewHolder);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View item = viewHolder.itemView;

            Paint paint = new Paint();
            if (dX < 0) {
                paint.setARGB(255, 255, 0, 0);
                c.drawRect(item.getRight() + (dX / 4), item.getTop(), item.getRight(), item.getBottom(), paint);
                Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_white_24dp);

                int intrinsicWidth = deleteIcon.getIntrinsicWidth();
                int intrinsicHeight = deleteIcon.getIntrinsicHeight();

                int deleteIconTop = item.getTop() + ((item.getBottom() - item.getTop()) - intrinsicHeight) / 2;
                int deleteIconMargin = ((item.getBottom() - item.getTop()) - intrinsicHeight) / 2;
                int deleteIconLeft = item.getRight() - deleteIconMargin - intrinsicWidth;
                int deleteIconRight = item.getRight() - deleteIconMargin;
                int deleteIconBottom = deleteIconTop + intrinsicHeight;

                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteIcon.draw(c);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        private void deleteAction(RecyclerView.ViewHolder viewHolder) {
            deletedItem = todosData.get(viewHolder.getAdapterPosition());
            deletedItemPosition = viewHolder.getAdapterPosition();

            todosData.remove(viewHolder.getAdapterPosition());
            todoRecyclerViewAdapter.notifyDataSetChanged();

            Snackbar.make(fragmentTodoBinding.getRoot(), R.string.element_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> dismissDeleteAction())
                    .show();
            
            runnable = () -> todoNoteViewModel.deleteTodo(todosData.get(viewHolder.getAdapterPosition())._id, userToken);
            handler.postDelayed(runnable, 2750);

        }

        private void dismissDeleteAction() {
            handler.removeCallbacks(runnable);
            todosData.add(deletedItemPosition, deletedItem);
            todoRecyclerViewAdapter.notifyDataSetChanged();
        }
    };
}
