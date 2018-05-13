package com.projects.melih.baking.repository.local;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.projects.melih.baking.model.Recipe;

/**
 * Created by Melih Gültekin on 13.05.2018
 */
@Database(entities = {Recipe.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class RecipesDatabase extends RoomDatabase {
    private static final Object lock = new Object();

    @SuppressLint("StaticFieldLeak")
    private static RecipesDatabase instance;

    public abstract RecipeDao recipeDao();

    public static RecipesDatabase getInstance(@NonNull Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipesDatabase.class, "Recipes.db")
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return instance;
        }
    }
}