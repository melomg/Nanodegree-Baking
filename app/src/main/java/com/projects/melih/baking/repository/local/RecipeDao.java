package com.projects.melih.baking.repository.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.projects.melih.baking.model.Recipe;

import java.util.List;

/**
 * Created by Melih Gültekin on 13.05.2018
 */
@Dao
public interface RecipeDao {

    @Query("SELECT * from Recipes")
    List<Recipe> getAll();

    @Query("SELECT * from Recipes where id = :id LIMIT 1")
    Recipe getRecipe(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Recipe> recipes);

    @Query("DELETE FROM Recipes")
    void deleteAll();
}