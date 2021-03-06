package com.projects.melih.baking.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.melih.baking.ui.main.RecipesViewModel;
import com.projects.melih.baking.ui.recipe.RecipeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RecipesViewModel.class)
    abstract ViewModel bindRecipesViewModel(RecipesViewModel recipesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    abstract ViewModel bindRecipeViewModel(RecipeViewModel recipeViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(BakingViewModelFactory viewModelFactory);
}