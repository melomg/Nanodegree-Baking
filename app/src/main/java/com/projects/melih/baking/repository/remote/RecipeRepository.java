package com.projects.melih.baking.repository.remote;

import android.support.annotation.NonNull;

import com.projects.melih.baking.model.Recipe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Melih Gültekin on 26.04.2018
 */
@Singleton
public class RecipeRepository {
    private BakingService bakingService;

    @Inject
    RecipeRepository(BakingService bakingService) {
        this.bakingService = bakingService;
    }

    public Call<List<Recipe>> loadRecipes(DataCallback<List<Recipe>> callback) {
        Call<List<Recipe>> call = bakingService.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                callback.onComplete(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                callback.onComplete(null);
            }
        });
        return call;
    }
}