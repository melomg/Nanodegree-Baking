package com.projects.melih.baking.ui.recipe;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.melih.baking.R;
import com.projects.melih.baking.common.Constants;
import com.projects.melih.baking.components.GridAutoFitLayoutManager;
import com.projects.melih.baking.databinding.FragmentRecipeListBinding;
import com.projects.melih.baking.ui.base.BaseFragment;
import com.projects.melih.baking.ui.main.RecipesViewModel;

import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public class RecipeListFragment extends BaseFragment {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentRecipeListBinding binding;
    private RecipesViewModel recipesViewModel;
    private RecipeListAdapter adapter;

    public static RecipeListFragment newInstance() {
        return new RecipeListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        recipesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), viewModelFactory).get(RecipesViewModel.class);
        recipesViewModel.getLoadingLiveData().observe(this, isLoading -> {
            if ((isLoading != null) && isLoading) {
                binding.swipeRefresh.setRefreshing(true);
            } else {
                binding.swipeRefresh.setRefreshing(false);
            }
        });
        recipesViewModel.getErrorLiveData().observe(this,
                errorState -> showToast((errorState == null) ? Constants.UNKNOWN_ERROR : errorState.getErrorMessage()));

        recipesViewModel.getRecipesLiveData().observe(this, recipes -> adapter.submitRecipeList(recipes));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.swipeRefresh.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        adapter = new RecipeListAdapter(recipe -> startActivity(RecipeActivity.newIntent(context, recipe)));

        GridAutoFitLayoutManager layoutManager = new GridAutoFitLayoutManager(context, R.dimen.list_item_width);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(false);
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> recipesViewModel.fetchData());
    }
}