package com.projects.melih.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.projects.melih.baking.BuildConfig;
import com.projects.melih.baking.R;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.model.Ingredient;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.repository.local.LocalRecipesDataSource;
import com.projects.melih.baking.repository.local.RecipesDatabase;

import java.util.List;

/**
 * Created by Melih Gültekin on 15.05.2018
 */
public class RecipeIngredientsService extends IntentService {
    public static final String ACTION_UPDATE_INGREDIENTS = BuildConfig.APPLICATION_ID + ".ACTION.UPDATE.INGREDIENTS";
    public static final String EXTRA_RECIPE_ID = BuildConfig.APPLICATION_ID + ".EXTRA.RECIPE_ID";

    public RecipeIngredientsService() {
        super("RecipeIngredientsService");
    }

    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdatePlantWidgets(Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeIngredientsService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startService(intent);
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_INGREDIENTS.equals(action)) {
                final long recipeId = intent.getLongExtra(EXTRA_RECIPE_ID, 0L);
                handleActionUpdateIngredients(recipeId);
            }
        }
    }


    /**
     * Handle action UpdatePlantWidgets in the provided background thread
     */
    private void handleActionUpdateIngredients(long id) {
        //Query to get the plant that's most in need for water (last watered)
        RecipesDatabase recipesDatabase = RecipesDatabase.getInstance(getApplicationContext());
        LocalRecipesDataSource dataSource = new LocalRecipesDataSource(recipesDatabase.recipeDao());
        Recipe recipe = dataSource.getRecipeById(id);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_ingredients_header);
        //Now update all widgets
        RecipeWidgetProvider.updatePlantWidgets(this, appWidgetManager, recipe, appWidgetIds);
    }

    private void addIngredients(@NonNull List<Ingredient> ingredients) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
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
                spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.red)), startIndex, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(blank);
                spannableStringBuilder.append(ingredientName);
                if (i != size - 1) {
                    spannableStringBuilder.append("\n");
                }
            }
        }
    }
}
