package com.projects.melih.baking.ui.main;

import android.os.Bundle;

import com.projects.melih.baking.R;
import com.projects.melih.baking.ui.base.BaseActivity;
import com.projects.melih.baking.ui.recipe.RecipeListFragment;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
public class MainActivity extends BaseActivity {

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