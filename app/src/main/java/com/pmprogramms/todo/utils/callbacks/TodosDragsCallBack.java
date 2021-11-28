package com.pmprogramms.todo.utils.callbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

// TODO: 27/11/2021  
public class TodosDragsCallBack extends ItemTouchHelper.Callback {

    public TodosDragsCallBack() {
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface ItemMoveHelper {
        void onRowMove(int selectedPosition, int targetPosition);
    }
}
