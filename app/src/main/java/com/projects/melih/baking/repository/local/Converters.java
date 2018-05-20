package com.projects.melih.baking.repository.local;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.projects.melih.baking.model.Ingredient;
import com.projects.melih.baking.model.Step;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melih Gültekin on 13.05.2018
 */
public class Converters {
    private Converters() {
        // no-op
    }

    @TypeConverter
    public static ArrayList<Ingredient> fromIngredientString(String value) {
        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromIngredientList(ArrayList<Ingredient> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<Step> fromStepString(String value) {
        Type listType = new TypeToken<List<Step>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStepList(List<Step> list) {
        return new Gson().toJson(list);
    }
}