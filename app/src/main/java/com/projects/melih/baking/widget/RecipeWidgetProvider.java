package com.projects.melih.baking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.projects.melih.baking.R;
import com.projects.melih.baking.model.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, Recipe recipe, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
        views.setTextViewText(R.id.appwidget_ingredients_info, recipe.getName());
        views.setTextViewText(R.id.appwidget_ingredients_header, String.valueOf(recipe.getIngredients()));
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
}