package com.projects.melih.baking.repository.remote;

import android.support.annotation.Nullable;

public interface DataCallback<T extends Object> {
    void onComplete(@Nullable T data);//TODO , @Nullable RPPException error);
}