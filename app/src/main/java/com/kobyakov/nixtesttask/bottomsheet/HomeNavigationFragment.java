package com.kobyakov.nixtesttask.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kobyakov.nixtesttask.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeNavigationFragment extends BottomSheetDialogFragment {

    private final String TAG = getClass().getSimpleName();

    private MenuItemClickListener mMenuItemClickListener;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_categories, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public interface MenuItemClickListener {
        void onMenuItemClick(int fragmentId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            mMenuItemClickListener.onMenuItemClick(menuItem.getItemId());
            dismiss();
            return false;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mMenuItemClickListener = (MenuItemClickListener) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Log.d(TAG, "onDestroyView");
    }
}