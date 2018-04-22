package com.projects.melih.baking.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
@SuppressWarnings("unused")
public class Ingredient {

    @SerializedName("quantity")
    private float quantity;

    @SerializedName("measure")
    @Nullable
    private String measure;

    @SerializedName("ingredient")
    @Nullable
    private String name;

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    @Nullable
    public String getMeasure() {
        return measure;
    }

    public void setMeasure(@Nullable String measure) {
        this.measure = measure;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }
}