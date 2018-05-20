package com.projects.melih.baking.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.google.gson.Gson;
import com.projects.melih.baking.R;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.databinding.RecipeAppwidgetConfigureBinding;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.repository.remote.ErrorState;
import com.projects.melih.baking.ui.base.BaseActivity;
import com.projects.melih.baking.ui.main.RecipesViewModel;

import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 19.05.2018
 */
public class RecipeAppWidgetConfigure extends BaseActivity {
    private static final String PREFS_FILE_NAME = "prefs";
    private static final String PREFS_RECIPE = "pref_recipe";
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private RecipeAppwidgetConfigureBinding binding;
    private RecipesViewModel recipesViewModel;
    private RecipeWidgetListAdapter adapter;
    private int appWidgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.recipe_appwidget_configure);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.recipe_appwidget_configure);
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.choose_recipe));
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        } else {
            recipesViewModel = ViewModelProviders.of(Objects.requireNonNull(this), viewModelFactory).get(RecipesViewModel.class);
            recipesViewModel.getLoadingLiveData().observe(this, isLoading -> {
                if ((isLoading != null) && isLoading) {
                    //binding.loading.show();
                } else {
                    //binding.loading.setVisibility(View.GONE);
                }
            });
            recipesViewModel.getErrorLiveData().observe(this, errorState -> {
                if (errorState != null) {
                    switch (errorState.getState()) {
                        case ErrorState.STATE_NO_NETWORK:
                            showToast(getString(R.string.network_error));
                            break;
                        default:
                            showToast(getString(R.string.unknown_error));
                            break;
                    }
                }
            });

            recipesViewModel.getRecipesLiveData().observe(this, recipes -> {
                if (CollectionUtils.isNotEmpty(recipes)) {
                    adapter = new RecipeWidgetListAdapter(this, recipes);
                    binding.listView.setAdapter(adapter);
                    binding.listView.setOnItemClickListener((parent, view, position, id) -> {
                        Recipe recipe = recipes.get(position);
                        final RecipeAppWidgetConfigure context = RecipeAppWidgetConfigure.this;
                        saveRecipe(context.getApplicationContext(), appWidgetId, recipe);

                        // Push widget update to surface with newly selected recipe
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        RecipeWidgetProvider.updateAppWidget(context, appWidgetManager, recipe, appWidgetId);

                        // Make sure we pass back the original appWidgetId
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    });
                }
            });
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipe(Context context, int appWidgetId, Recipe recipe) {
        String recipeJson = new Gson().toJson(recipe);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
        prefs.putString(PREFS_RECIPE + appWidgetId, recipeJson);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static Recipe loadRecipe(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        String recipeJson = prefs.getString(PREFS_RECIPE + appWidgetId, "");
        return new Gson().fromJson(recipeJson, Recipe.class);
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
        prefs.remove(PREFS_RECIPE + appWidgetId);
        prefs.apply();
    }
}