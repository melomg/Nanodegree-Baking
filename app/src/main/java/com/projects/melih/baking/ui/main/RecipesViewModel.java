package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.repository.remote.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public class RecipesViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loadingLiveData;
    private final MutableLiveData<Boolean> errorLiveData;
    private final MutableLiveData<List<Recipe>> recipesLiveData;
    private Call<List<Recipe>> callRecipes;

    @Inject
    public RecipesViewModel(RecipeRepository recipeRepository) {
        loadingLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        recipesLiveData = new MutableLiveData<>();
        loadingLiveData.setValue(true);
        callRecipes = recipeRepository.loadRecipes(data -> {
            loadingLiveData.setValue(false);
            if (data == null) {
                errorLiveData.setValue(true);
            } else {
                recipesLiveData.setValue(data);
            }
        });
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<Boolean> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (callRecipes != null) {
            callRecipes.cancel();
        }
    }
}