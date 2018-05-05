package com.projects.melih.baking.common;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.projects.melih.baking.R;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
public class DataBindingUtils {

    private DataBindingUtils() {
        //no-op
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .frame(1000)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder);
        Glide.with(context)
                .asBitmap()
                .apply(options)
                .load(url)
                .thumbnail(0.1f)
                .into(imageView);
    }
}