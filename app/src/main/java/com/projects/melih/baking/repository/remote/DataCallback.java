package com.projects.melih.baking.repository.remote;

import android.support.annotation.Nullable;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public interface DataCallback<T> {
    void onComplete(@Nullable T data);
}