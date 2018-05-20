package com.projects.melih.baking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import com.projects.melih.baking.R;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.model.Ingredient;
import com.projects.melih.baking.model.Recipe;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, Recipe recipe, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
        views.setTextViewText(R.id.appwidget_ingredients_info, recipe.getName());

        views.setTextViewText(R.id.appwidget_ingredients_header, addIngredients(context, recipe.getIngredients()));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager, Recipe recipeId, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeId, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service takes care of updating the widgets UI
        for (int appWidgetId : appWidgetIds) {
            Recipe recipe = RecipeAppWidgetConfigure.loadRecipe(context, appWidgetId);
            if (recipe != null) {
                updateAppWidget(context, appWidgetManager, recipe, appWidgetId);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            RecipeAppWidgetConfigure.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static SpannableStringBuilder addIngredients(@NonNull Context context, @Nullable List<Ingredient> ingredients) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (ingredients != null) {
            int size = ingredients.size();
            for (int i = 0; i < size; i++) {
                int startIndex = spannableStringBuilder.length();
                final Ingredient ingredient = ingredients.get(i);
                final String ingredientName = ingredient.getName();
                if (!TextUtils.isEmpty(ingredientName)) {
                    spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(Utils.getNumberFormatter().format(ingredient.getQuantity()));
                    final String blank = " ";
                    spannableStringBuilder.append(blank);
                    spannableStringBuilder.append(ingredient.getMeasure());
                    spannableStringBuilder.setSpan(new BulletSpan(10, R.color.black), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(blank);
                    spannableStringBuilder.append(ingredientName);
                    if (i != size - 1) {
                        spannableStringBuilder.append("\n");
                    }
                }
            }
        }
        return spannableStringBuilder;
    }
}