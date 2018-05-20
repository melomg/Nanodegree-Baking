package com.projects.melih.baking.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.projects.melih.baking.AppExecutors;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.repository.local.LocalRecipesDataSource;
import com.projects.melih.baking.repository.remote.BakingService;
import com.projects.melih.baking.repository.remote.DataCallback;
import com.projects.melih.baking.repository.remote.ErrorState;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.projects.melih.baking.common.Constants.UNKNOWN_ERROR;

/**
 * Created by Melih Gültekin on 26.04.2018
 */
@Singleton
public class RecipeRepository {
    private final Context context;
    private final BakingService bakingService;
    private final LocalRecipesDataSource localRecipesDataSource;
    private final AppExecutors appExecutors;

    @Inject
    CountingIdlingResource idlingResource;

    @Inject
    RecipeRepository(@NonNull Context applicationContext, @NonNull BakingService bakingService, @NonNull LocalRecipesDataSource localRecipesDataSource, @NonNull AppExecutors appExecutors) {
        this.context = applicationContext;
        this.bakingService = bakingService;
        this.localRecipesDataSource = localRecipesDataSource;
        this.appExecutors = appExecutors;
    }

    @Nullable
    public Call<List<Recipe>> fetchRecipesFromNetwork(@NonNull DataCallback<List<Recipe>> callback) {
        Call<List<Recipe>> call = null;
        if (!Utils.isNetworkConnected(context)) {
            callback.onComplete(null, ErrorState.NO_NETWORK);
        } else {
            idlingResource.increment();
            call = bakingService.getRecipes();
            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                    final List<Recipe> recipes = response.body();
                    if (CollectionUtils.isNotEmpty(recipes)) {
                        callback.onComplete(recipes, null);

                        // updates local database
                        appExecutors.diskIO().execute(() -> localRecipesDataSource.insertAllRecipes(recipes));
                    } else {
                        callback.onComplete(null, ErrorState.EMPTY);
                    }
                    idlingResource.decrement();
                }

                @Override
                public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                    final String message = t.getMessage();
                    callback.onComplete(null, ErrorState.error((message == null) ? UNKNOWN_ERROR : message));
                    idlingResource.decrement();
                }
            });
        }
        return call;
    }

    public void getCachedRecipes(@NonNull DataCallback<List<Recipe>> callback) {
        idlingResource.increment();
        appExecutors.diskIO().execute(() -> {
            final List<Recipe> recipes = localRecipesDataSource.getAllRecipes();
            // notify on the main thread
            appExecutors.mainThread().execute(() -> {
                if (CollectionUtils.isNotEmpty(recipes)) {
                    callback.onComplete(recipes, null);
                } else {
                    callback.onComplete(null, ErrorState.FAILED);
                }
                idlingResource.decrement();
            });
        });
    }

    public void getRecipeById(long recipeId, @NonNull DataCallback<Recipe> callback) {
        idlingResource.increment();
        appExecutors.diskIO().execute(() -> {
            final Recipe recipe = localRecipesDataSource.getRecipeById(recipeId);
            // notify on the main thread
            appExecutors.mainThread().execute(() -> {
                if (recipe != null) {
                    callback.onComplete(recipe, null);
                } else {
                    callback.onComplete(null, ErrorState.FAILED);
                }
                idlingResource.decrement();
            });
        });
    }
}