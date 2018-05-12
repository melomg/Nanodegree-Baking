package com.projects.melih.baking.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import dagger.android.support.DaggerFragment;

/**
 * Created by Melih Gültekin on 22.04.2018
 */

public class BaseFragment extends DaggerFragment {

    protected Context context;
    protected NavigationListener navigationListener;
    private AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.navigationListener = (NavigationListener) context;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //shows or hides toolbar and status bar in case it is hided for landscape orientation
        showToolbarAndStatusBar(true);
    }

    protected void showToast(@StringRes int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * shows or hides toolbar and status bar
     *
     * @param show whether it will show the toolbar and status bar or hide
     */
    protected void showToolbarAndStatusBar(boolean show) {
        final ActionBar actionBar = getSupportActionBar();
        View decorView = activity.getWindow().getDecorView();
        int uiOptions;
        if (show) {
            uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            actionBar.show();
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            actionBar.hide();
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    @NonNull
    private ActionBar getSupportActionBar() {
        if (activity == null) {
            activity = (AppCompatActivity) getActivity();
        }
        //noinspection ConstantConditions
        return activity.getSupportActionBar();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationListener = null;
    }
}