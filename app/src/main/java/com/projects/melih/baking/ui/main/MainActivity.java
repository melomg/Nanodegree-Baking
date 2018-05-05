package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.util.Log;

import com.projects.melih.baking.R;
import com.projects.melih.baking.ui.base.BaseActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
public class MainActivity extends BaseActivity {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipesViewModel recipesViewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipesViewModel.class);
        recipesViewModel.getRecipesLiveData().observe(this, recipes -> {
            //TODO remove
            Log.d("melo", (recipes == null) ? "Null" : recipes.toString());
        });
    }
}