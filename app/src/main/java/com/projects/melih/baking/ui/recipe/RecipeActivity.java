package com.projects.melih.baking.ui.recipe;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.projects.melih.baking.R;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.ui.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 12.05.2018
 */
public class RecipeActivity extends BaseActivity {
    private static final String KEY_RECIPE = "key_recipe";
    private static final String KEY_RECIPE_ID = "key_recipe_id";
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private RecipeViewModel viewModel;
    private Recipe recipe;

    public static Intent newIntent(@NonNull Context context, @NonNull Recipe recipe) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra(RecipeActivity.KEY_RECIPE, recipe);
        return intent;
    }

    public static Intent newIntent(@NonNull Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra(RecipeActivity.KEY_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeViewModel.class);

        final Intent intent = getIntent();
        recipe = intent.getParcelableExtra(KEY_RECIPE);
        if (recipe == null) {
            long recipeId = intent.getLongExtra(KEY_RECIPE_ID, 1);
            viewModel.getRecipeById(recipeId);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, RecipeContainerFragment.newInstance())
                    .addToBackStack("")
                    .commit();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (recipe != null) {
            viewModel.setSelectedRecipe(recipe);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}