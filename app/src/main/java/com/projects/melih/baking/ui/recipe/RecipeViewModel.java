package com.projects.melih.baking.ui.recipe;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.model.Step;

import java.util.List;

/**
 * Created by Melih Gültekin on 12.05.2018
 */
public class RecipeViewModel extends ViewModel {

    private final MutableLiveData<Recipe> selectedRecipeLiveData;
    private final LiveData<List<Step>> stepListLiveData;
    private final MutableLiveData<Integer> selectedStepPositionLiveData;

    public RecipeViewModel() {
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
}