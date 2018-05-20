package com.projects.melih.baking.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.projects.melih.baking.R;
import com.projects.melih.baking.ui.base.BaseActivity;
import com.projects.melih.baking.ui.recipe.RecipeListFragment;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
public class MainActivity extends BaseActivity {

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, RecipeListFragment.newInstance())
                    .addToBackStack("")
                    .commit();
        }
    }
}