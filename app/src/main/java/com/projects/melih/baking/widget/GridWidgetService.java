package com.projects.melih.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.projects.melih.baking.R;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.model.Ingredient;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.repository.local.LocalRecipesDataSource;
import com.projects.melih.baking.repository.local.RecipesDatabase;

import java.util.ArrayList;

import static com.projects.melih.baking.widget.RecipeWidgetProvider.EXTRA_RECIPE;
import static com.projects.melih.baking.widget.RecipeWidgetProvider.BUNDLE;

/**
 * Created by Melih Gültekin on 19.05.2018
 */
public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE);
        Recipe recipe = bundle.getParcelable(EXTRA_RECIPE);
        return new GridRemoteViewsFactory(this.getApplicationContext(), recipe);
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Recipe recipe;
    private Context context;

    GridRemoteViewsFactory(Context applicationContext, Recipe recipe) {
        context = applicationContext;
        this.recipe = recipe;
    }

    @Override
    public void onCreate() {
        //no-op
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        RecipesDatabase recipesDatabase = RecipesDatabase.getInstance(context);
        LocalRecipesDataSource dataSource = new LocalRecipesDataSource(recipesDatabase.recipeDao());
        this.recipe = dataSource.getRecipeById(recipe.getId());
    }

    @Override
    public void onDestroy() {
        //no-op
    }

    @Override
    public int getCount() {
        return CollectionUtils.size(recipe.getIngredients());
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final ArrayList<Ingredient> ingredients = recipe.getIngredients();
        if (CollectionUtils.size(ingredients) == 0) {
            return null;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_item_widget);

        // Update the ingredient
        Ingredient ingredient = ingredients.get(position);
        final String ingredientName = ingredient.getName();
        final int startIndex = 0;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(ingredientName)) {
            spannableStringBuilder.append(Utils.getNumberFormatter().format(ingredient.getQuantity()));
            final String blank = " ";
            spannableStringBuilder.append(blank);
            spannableStringBuilder.append(ingredient.getMeasure());
            spannableStringBuilder.setSpan(new BulletSpan(10, R.color.black), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(blank);
            spannableStringBuilder.append(ingredientName);
        }
        views.setTextViewText(R.id.ingredient_info, spannableStringBuilder);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}