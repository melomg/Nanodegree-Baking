package com.projects.melih.baking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
@SuppressWarnings("unused")
public class Recipe implements Parcelable {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    @Nullable
    private String name;

    @SerializedName("ingredients")
    @Nullable
    private List<Ingredient> ingredients;

    @SerializedName("steps")
    @Nullable
    private List<Step> steps;

    @SerializedName("servings")
    private int servingCount;

    @SerializedName("image")
    @Nullable
    private String image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(@Nullable List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Nullable
    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(@Nullable List<Step> steps) {
        this.steps = steps;
    }

    public int getServingCount() {
        return servingCount;
    }

    public void setServingCount(int servingCount) {
        this.servingCount = servingCount;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    public static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recipe>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldRecipe, @NonNull Recipe newRecipe) {
            return oldRecipe.getId() == newRecipe.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldRecipe, @NonNull Recipe newRecipe) {
            return oldRecipe.equals(newRecipe);
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeList(this.ingredients);
        dest.writeList(this.steps);
        dest.writeInt(this.servingCount);
        dest.writeString(this.image);
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.ingredients = new ArrayList<>();
        in.readList(this.ingredients, Ingredient.class.getClassLoader());
        this.steps = new ArrayList<>();
        in.readList(this.steps, Step.class.getClassLoader());
        this.servingCount = in.readInt();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}