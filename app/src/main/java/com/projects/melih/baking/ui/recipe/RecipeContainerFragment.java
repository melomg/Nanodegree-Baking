package com.projects.melih.baking.ui.recipe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.melih.baking.R;
import com.projects.melih.baking.ui.base.BaseFragment;

/**
 * Created by Melih Gültekin on 12.05.2018
 */
public class RecipeContainerFragment extends BaseFragment {

    public static RecipeContainerFragment newInstance() {
        return new RecipeContainerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_container, container, false);
    }
}