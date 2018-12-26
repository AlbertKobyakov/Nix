package com.kobyakov.nixtesttask;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.Objects;

public class RecyclerViewEmptyObserver extends RecyclerView.AdapterDataObserver {
    private View emptyView;
    private View filledView;
    private RecyclerView recyclerView;

    public RecyclerViewEmptyObserver(RecyclerView recyclerView, View emptyView, View filledView) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.filledView = filledView;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        Log.d("RV",  Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() + " kkkkkkk");
        if (emptyView != null && recyclerView.getAdapter() != null/* && count > 0*/) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            filledView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onChanged() {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
    }
}
