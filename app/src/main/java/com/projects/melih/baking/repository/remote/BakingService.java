package com.projects.melih.baking.repository.remote;

import com.projects.melih.baking.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Melih Gültekin on 23.04.2018
 */
public interface BakingService {
    String BASE_URL = "http://go.udacity.com/";

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}