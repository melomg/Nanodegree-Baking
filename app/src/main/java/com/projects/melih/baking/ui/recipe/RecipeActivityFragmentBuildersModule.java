package com.projects.melih.baking.ui.recipe;

import com.projects.melih.baking.di.ScopeFragment;
import com.projects.melih.baking.ui.step.StepDetailFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Melih Gültekin on 12.05.2018
 */
@Module
public abstract class RecipeActivityFragmentBuildersModule {
    @ScopeFragment
    @ContributesAndroidInjector
    abstract RecipeContainerFragment bindRecipeContainerFragment();

    @ScopeFragment
    @ContributesAndroidInjector
    abstract RecipeDetailFragment bindRecipeDetailFragment();

    @ScopeFragment
    @ContributesAndroidInjector
    abstract StepDetailFragment bindStepDetailFragment();
}