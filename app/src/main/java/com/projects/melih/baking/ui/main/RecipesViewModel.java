package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.projects.melih.baking.common.CollectionUtils;
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
    private final LiveData<List<Step>> stepListLiveData;
    private final MutableLiveData<Integer> selectedStepPositionLiveData;
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
        stepListLiveData = Transformations.map(selectedRecipeLiveData, Recipe::getSteps);
        selectedStepPositionLiveData = new MutableLiveData<>();
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

    public LiveData<List<Step>> getStepListLiveData() {
        return stepListLiveData;
    }

    public LiveData<Integer> getSelectedStepPositionLiveData() {
        return selectedStepPositionLiveData;
    }

    public void setSelectedStepPosition(int selectedStepPosition) {
        this.selectedStepPositionLiveData.postValue(selectedStepPosition);
    }

    public void fetchData() {
        triggerListData.setValue(true);
    }

    public void goToNextStep() {
        List<Step> steps = stepListLiveData.getValue();
        Integer position = selectedStepPositionLiveData.getValue();
        if ((position != null) && (position < (CollectionUtils.size(steps) - 1))) {
            position++;
            selectedStepPositionLiveData.setValue(position);
        }
    }

    public void goToPreviousStep() {
        Integer position = selectedStepPositionLiveData.getValue();
        if ((position != null) && (position > 0)) {
            position--;
            selectedStepPositionLiveData.setValue(position);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (callRecipes != null) {
            callRecipes.cancel();
        }
    }
}