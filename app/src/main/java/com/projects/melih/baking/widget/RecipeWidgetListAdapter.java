package com.projects.melih.baking.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.melih.baking.R;
import com.projects.melih.baking.model.Recipe;

import java.util.List;

/**
 * Created by Melih Gültekin on 19.05.2018
 */
public class RecipeWidgetListAdapter extends ArrayAdapter<Recipe> {
    private List<Recipe> recipes;

    static class RecipeViewHolder {
        TextView textViewRecipeName;
    }

    RecipeWidgetListAdapter(@NonNull Context context, List<Recipe> recipes) {
        super(context, R.layout.item_widget_recipe_list, recipes);
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_widget_recipe_list, parent, false);
            // configure view holder
            RecipeViewHolder viewHolder = new RecipeViewHolder();
            viewHolder.textViewRecipeName = view.findViewById(R.id.recipe_name);
            view.setTag(viewHolder);
        }

        // fill data
        RecipeViewHolder holder = (RecipeViewHolder) view.getTag();
        final Recipe recipe = recipes.get(position);
        if (recipe != null) {
            holder.textViewRecipeName.setText(recipe.getName());
        }

        return view;
    }
}