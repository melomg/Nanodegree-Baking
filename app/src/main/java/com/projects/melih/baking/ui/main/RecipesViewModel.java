package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.projects.melih.baking.common.SingleLiveEvent;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.repository.remote.ErrorState;
import com.projects.melih.baking.repository.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public class RecipesViewModel extends ViewModel {
    private final SingleLiveEvent<ErrorState> errorLiveData;
    @VisibleForTesting
    private final MutableLiveData<Boolean> loadingLiveData;
    private final MutableLiveData<Boolean> triggerListData;
    @VisibleForTesting
    private final MediatorLiveData<List<Recipe>> recipesLiveData;
    private final RecipeRepository recipeRepository;
    private Call<List<Recipe>> callRecipes;

    @SuppressWarnings("WeakerAccess")
    @Inject
    public RecipesViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
        errorLiveData = new SingleLiveEvent<>();
        loadingLiveData = new MutableLiveData<>();
        triggerListData = new MutableLiveData<>();
        recipesLiveData = new MediatorLiveData<>();
        recipesLiveData.addSource(triggerListData, state -> fetchRecipeListFromNetwork());
        getRecipeList();
    }

    private void fetchRecipeListFromNetwork() {
        loadingLiveData.setValue(true);
        if (callRecipes != null) {
            callRecipes.cancel();
        }
        callRecipes = recipeRepository.fetchRecipesFromNetwork((data, errorState) -> {
            loadingLiveData.setValue(false);
            if (data == null) {
                errorLiveData.setValue(errorState);
            } else {
                recipesLiveData.setValue(data);
            }
        });
    }

    private void getRecipeList() {
        loadingLiveData.setValue(true);
        recipeRepository.getCachedRecipes((data, errorState) -> {
            if (data == null) {
                fetchRecipeListFromNetwork();
            } else {
                loadingLiveData.setValue(false);
                recipesLiveData.setValue(data);
            }
        });
    }

    public LiveData<ErrorState> getErrorLiveData() {
        return errorLiveData;
    }

    @VisibleForTesting
    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    @VisibleForTesting
    public LiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public void fetchData() {
        triggerListData.setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (callRecipes != null) {
            callRecipes.cancel();
        }
    }
}