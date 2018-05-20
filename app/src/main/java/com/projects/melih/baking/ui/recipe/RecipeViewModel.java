package com.projects.melih.baking.ui.recipe;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.model.Step;
import com.projects.melih.baking.repository.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 12.05.2018
 */
public class RecipeViewModel extends ViewModel {

    @VisibleForTesting
    private final MutableLiveData<Recipe> selectedRecipeLiveData;
    @VisibleForTesting
    private final LiveData<List<Step>> stepListLiveData;
    @VisibleForTesting
    private final MutableLiveData<Integer> selectedStepPositionLiveData;
    private final RecipeRepository recipeRepository;

    @Inject
    public RecipeViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
        selectedRecipeLiveData = new MutableLiveData<>();
        stepListLiveData = Transformations.map(selectedRecipeLiveData, Recipe::getSteps);
        selectedStepPositionLiveData = new MutableLiveData<>();
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

    public void getRecipeById(long recipeId) {
        recipeRepository.getRecipeById(recipeId, (recipe, errorState) -> {
            if (recipe != null) {
                setSelectedRecipe(recipe);
            }
        });
    }
}