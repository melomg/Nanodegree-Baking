package com.projects.melih.baking.ui.main;

import com.projects.melih.baking.ui.recipes.RecipeListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
@Module
public abstract class MainActivityFragmentBuildersModule {
    @ContributesAndroidInjector
    abstract RecipeListFragment bindRecipeListFragment();
}