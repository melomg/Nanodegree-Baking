package com.projects.melih.baking.ui.base;

import android.support.annotation.NonNull;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public interface ItemClickListener<T> {
    void onItemClick(@NonNull T object);
}