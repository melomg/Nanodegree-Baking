package com.projects.melih.baking.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Melih Gültekin on 22.04.2018
 */

public class Utils {
    private Utils() {
        // no-op
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return ((connectivityManager != null) && (connectivityManager.getActiveNetworkInfo() != null));
    }

    public static void await(@NonNull final View view) {
        view.setEnabled(false);
        view.postDelayed(() -> view.setEnabled(true), view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
}