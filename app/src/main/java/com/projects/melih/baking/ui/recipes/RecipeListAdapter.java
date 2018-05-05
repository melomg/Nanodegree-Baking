package com.projects.melih.baking.ui.recipes;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.projects.melih.baking.R;
import com.projects.melih.baking.databinding.ItemRecipeListBinding;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.ui.base.ItemClickListener;

import java.util.List;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {
    private final ItemClickListener<Recipe> recipeItemListener;
    private final AsyncListDiffer<Recipe> differ = new AsyncListDiffer<>(this, Recipe.DIFF_CALLBACK);

    RecipeListAdapter(@NonNull ItemClickListener<Recipe> recipeItemListener) {
        this.recipeItemListener = recipeItemListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RecipeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_recipe_list, parent, false), recipeItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bindTo(differ.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    void submitRecipeList(@Nullable List<Recipe> recipeList) {
        differ.submitList(recipeList);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeListBinding binding;
        private final ItemClickListener<Recipe> recipeItemListener;

        RecipeViewHolder(@NonNull ItemRecipeListBinding binding, @NonNull ItemClickListener<Recipe> recipeItemListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.recipeItemListener = recipeItemListener;
        }

        void bindTo(@Nullable final Recipe recipe) {
            if (recipe != null) {
                binding.setRecipe(recipe);
                itemView.setOnClickListener(v -> recipeItemListener.onItemClick(recipe));
            }
        }
    }
}