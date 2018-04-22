package com.projects.melih.baking.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
@SuppressWarnings("unused")
public class Step {

    @SerializedName("id")
    private long id;

    @SerializedName("shortDescription")
    @Nullable
    private String shortDescription;

    @SerializedName("description")
    @Nullable
    private String description;

    @SerializedName("videoURL")
    @Nullable
    private String videoUrl;

    @SerializedName("thumbnailURL")
    @Nullable
    private String thumbnailUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(@Nullable String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(@Nullable String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Nullable
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(@Nullable String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}