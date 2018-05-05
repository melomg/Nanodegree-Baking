package com.projects.melih.baking.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.model.Recipe;

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

    @Inject
    RecipeRepository(Context applicationContext, BakingService bakingService) {
        this.context = applicationContext;
        this.bakingService = bakingService;
    }

    @Nullable
    public Call<List<Recipe>> loadRecipes(DataCallback<List<Recipe>> callback) {
        Call<List<Recipe>> call = null;
        if (!Utils.isNetworkConnected(context)) {
            callback.onComplete(null, ErrorState.NO_NETWORK);
        } else {
            call = bakingService.getRecipes();
            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                    callback.onComplete(response.body(), null);
                }

                @Override
                public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                    final String message = t.getMessage();
                    callback.onComplete(null, ErrorState.error((message == null) ? UNKNOWN_ERROR : message));
                }
            });
        }
        return call;
    }
}