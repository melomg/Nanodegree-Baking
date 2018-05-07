package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.projects.melih.baking.common.SingleLiveEvent;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.model.Step;
import com.projects.melih.baking.repository.remote.ErrorState;
import com.projects.melih.baking.repository.remote.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public class RecipesViewModel extends ViewModel {
    private final SingleLiveEvent<ErrorState> errorLiveData;
    private final MutableLiveData<Boolean> loadingLiveData;
    private final MutableLiveData<Boolean> triggerListData;
    private final MediatorLiveData<List<Recipe>> recipesLiveData;
    private final MutableLiveData<Recipe> selectedRecipeLiveData;
    private final MutableLiveData<Step> selectedStepLiveData;
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
        recipesLiveData.addSource(triggerListData, state -> getRecipeList());
        selectedRecipeLiveData = new MutableLiveData<>();
        selectedStepLiveData = new MutableLiveData<>();
        getRecipeList();
    }

    private void getRecipeList() {
        loadingLiveData.setValue(true);
        callRecipes = recipeRepository.loadRecipes((data, errorState) -> {
            loadingLiveData.setValue(false);
            if (data == null) {
                errorLiveData.setValue(errorState);
            } else {
                recipesLiveData.setValue(data);
            }
        });
    }

    public LiveData<ErrorState> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public LiveData<Recipe> getSelectedRecipeLiveData() {
        return selectedRecipeLiveData;
    }

    public void setSelectedRecipe(@NonNull Recipe selectedRecipe) {
        this.selectedRecipeLiveData.postValue(selectedRecipe);
    }

    public LiveData<Step> getSelectedStepLiveData() {
        return selectedStepLiveData;
    }

    public void setSelectedStep(@NonNull Step selectedStep) {
        this.selectedStepLiveData.postValue(selectedStep);
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