package com.projects.melih.baking.repository.local;

import com.projects.melih.baking.model.Recipe;

import java.util.List;

/**
 * Created by Melih Gültekin on 13.05.2018
 */
public interface RecipesDataSource {
    /**
     * Gets all the recipes from the data source.
     *
     * @return all the recipes from the data source.
     */
    List<Recipe> getAllRecipes();

    Recipe getRecipeById(long id);

    /**
     * Inserts the recipes in the data source, or, if it is an existing recipe list, it updates it.
     *
     * @param recipes the recipes to be inserted or updated.
     */
    void insertAllRecipes(List<Recipe> recipes);

    /**
     * Deletes all recipes from the data source.
     */
    void deleteAllRecipes();
}
