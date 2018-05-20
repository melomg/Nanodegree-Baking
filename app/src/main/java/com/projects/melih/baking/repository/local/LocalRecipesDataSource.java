package com.projects.melih.baking.repository.local;

import android.support.annotation.NonNull;

import com.projects.melih.baking.model.Recipe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Melih Gültekin on 13.05.2018
 */
@Singleton
public class LocalRecipesDataSource implements RecipesDataSource {
    private final RecipeDao recipeDao;

    @Inject
    public LocalRecipesDataSource(@NonNull RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeDao.getAll();
    }

    @Override
    public Recipe getRecipeById(long id) {
        return recipeDao.getRecipe(id);
    }

    @Override
    public void insertAllRecipes(List<Recipe> recipes) {
        recipeDao.insertAll(recipes);
    }

    @Override
    public void deleteAllRecipes() {
        recipeDao.deleteAll();
    }
}