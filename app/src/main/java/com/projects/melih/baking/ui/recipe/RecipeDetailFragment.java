package com.projects.melih.baking.ui.recipe;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.melih.baking.R;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.databinding.FragmentRecipeDetailBinding;
import com.projects.melih.baking.model.Ingredient;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.model.Step;
import com.projects.melih.baking.ui.base.BaseFragment;
import com.projects.melih.baking.ui.main.RecipesViewModel;
import com.projects.melih.baking.ui.step.StepDetailFragment;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 06.05.2018
 */
public class RecipeDetailFragment extends BaseFragment {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentRecipeDetailBinding binding;
    private RecipesViewModel recipesViewModel;
    private StepListAdapter adapter;

    public static RecipeDetailFragment newInstance() {
        return new RecipeDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);
        recipesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), viewModelFactory).get(RecipesViewModel.class);

        recipesViewModel.getSelectedRecipeLiveData().observe(this, this::updateUI);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new StepListAdapter(position -> {
            recipesViewModel.setSelectedStepPosition(position);
            //TODO dont replace if tablet
            navigationListener.replaceFragment(StepDetailFragment.newInstance());
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerViewSteps.setLayoutManager(layoutManager);
        binding.recyclerViewSteps.setHasFixedSize(true);
        binding.recyclerViewSteps.setAdapter(adapter);
    }

    private void updateUI(@Nullable Recipe selectedRecipe) {
        if (selectedRecipe != null) {
            final List<Ingredient> ingredients = selectedRecipe.getIngredients();
            if (CollectionUtils.isNotEmpty(ingredients)) {
                addIngredients(ingredients);
            }

            final List<Step> steps = selectedRecipe.getSteps();
            if (CollectionUtils.isNotEmpty(steps)) {
                addSteps(steps);
                binding.stepsPart.setVisibility(View.VISIBLE);
            } else {
                binding.stepsPart.setVisibility(View.GONE);
            }
        }
    }

    private void addIngredients(@NonNull List<Ingredient> ingredients) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int size = ingredients.size();
        for (int i = 0; i < size; i++) {
            int startIndex = spannableStringBuilder.length();
            final Ingredient ingredient = ingredients.get(i);
            final String ingredientName = ingredient.getName();
            if (!TextUtils.isEmpty(ingredientName)) {
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(Utils.getNumberFormatter().format(ingredient.getQuantity()));
                final String blank = " ";
                spannableStringBuilder.append(blank);
                spannableStringBuilder.append(ingredient.getMeasure());
                spannableStringBuilder.setSpan(new BulletSpan(10, R.color.black), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(blank);
                spannableStringBuilder.append(ingredientName);
                if (i != size - 1) {
                    spannableStringBuilder.append("\n");
                }
            }
        }

        binding.ingredientsPart.setVisibility((spannableStringBuilder.length() == 0) ? View.GONE : View.VISIBLE);
        binding.ingredientsInfo.setText(spannableStringBuilder);
    }

    private void addSteps(@NonNull List<Step> steps) {
        adapter.submitStepList(steps);
    }
}