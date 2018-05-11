package com.projects.melih.baking.ui.main;

import com.projects.melih.baking.di.ScopeFragment;
import com.projects.melih.baking.ui.recipe.RecipeDetailFragment;
import com.projects.melih.baking.ui.recipe.RecipeListFragment;
import com.projects.melih.baking.ui.step.StepDetailFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
@Module
public abstract class MainActivityFragmentBuildersModule {
    @ScopeFragment
    @ContributesAndroidInjector
    abstract RecipeListFragment bindRecipeListFragment();

    @ScopeFragment
    @ContributesAndroidInjector
    abstract RecipeDetailFragment bindRecipeDetailFragment();

    @ScopeFragment
    @ContributesAndroidInjector
    abstract StepDetailFragment bindStepDetailFragment();
}