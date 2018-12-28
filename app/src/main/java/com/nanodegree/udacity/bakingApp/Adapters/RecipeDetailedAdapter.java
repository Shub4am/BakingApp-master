/*
 * PROJECT LICENSE
 *
 * This project was submitted byShubham Prakash as part of the Android Developer Nanodegree At Udacity.
 *
 * As part of Udacity Honor code, your submissions must be your own work, hence
 * submitting this project as yours will cause you to break the Udacity Honor Code
 * and the suspension of your account.
 *
 * Me, the author of the project, allow you to check the code as a reference, but if
 * you submit it, it's your own responsibility if you get expelled.
 *
 * Copyright (c) 2018 Shubham Prakash
 *
 * Besides the above notice, the following license applies and this license notice
 * must be included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nanodegree.udacity.bakingApp.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nanodegree.udacity.bakingApp.Model.RecipeIngredients;
import com.nanodegree.udacity.bakingApp.Model.Recipe;
import com.nanodegree.udacity.bakingApp.Model.Step;
import com.nanodegree.udacity.bakingApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeDetailedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private Recipe recipe;
    private int selectedIndex = -1;

    final private ListItemClickListener listItemClickListener;

    private final int TITLE_TYPE = 0;
    private final int INGREDIENT_TYPE = 1;
    private final int STEP_TYPE = 2;

    public interface ListItemClickListener {
        void onListItemClick(int index);
    }

    public RecipeDetailedAdapter(Context context, ListItemClickListener listItemClickListener) {
        this.context = context;
        this.listItemClickListener = listItemClickListener;
    }

    public void setRecipeData(Recipe recipe) {
        this.recipe = recipe;
        notifyDataSetChanged();
    }

    public void setSelectionIndex(int index) {
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
            case 2:
                return TITLE_TYPE;
            case 1:
                return INGREDIENT_TYPE;
            default:
                return STEP_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        switch (viewType) {
            case TITLE_TYPE:
                return getTitleViewHolder(context, viewGroup);
            case INGREDIENT_TYPE:
                return getIngredientViewHolder(context, viewGroup);
            default:
                return getStepViewHolder(context, viewGroup);
        }
    }

    private TitleViewHolder getTitleViewHolder(Context context, ViewGroup viewGroup) {
        int layoutIdForListItem = R.layout.recipe_detail_title;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TitleViewHolder(view);
    }

    private IngredientViewHolder getIngredientViewHolder(Context context, ViewGroup viewGroup) {
        int layoutIdForListItem = R.layout.recipe_detail_ingredients;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new IngredientViewHolder(view);
    }

    private StepViewHolder getStepViewHolder(Context context, ViewGroup viewGroup) {
        int layoutIdForListItem = R.layout.recipe_detail_step;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TITLE_TYPE:
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                String title = context.getString(position == 0 ? R.string.ingredients : R.string.steps);
                titleViewHolder.textView.setText(title);
                break;
            case INGREDIENT_TYPE:
                break;
            case STEP_TYPE:
                int index = position - 3;
                StepViewHolder stepViewHolder = (StepViewHolder) holder;
                Step step = recipe.getSteps().get(index);
                stepViewHolder.textView.setText(step.getShortDescription());
                int color = selectedIndex == index ? R.color.white : R.color.white;
                stepViewHolder.cardView.setBackgroundColor(context.getResources().getColor(color));
                break;
        }
    }

    @Override
    public int getItemCount() {
        int titlesCount = 2;
        int ingredientsCount = recipe.getIngredients() != null ? 1 : 0;
        int stepsCount = recipe.getSteps() != null ? recipe.getSteps().size() : 0;
        return titlesCount + ingredientsCount + stepsCount;
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_detail_title)
        TextView textView;

        private TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredients_container)
        LinearLayout linearLayout;

        private IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            appendIngredients(linearLayout);
        }

        void appendIngredients(LinearLayout parentLayout) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view;
            for (RecipeIngredients ingredient : recipe.getIngredients()) {
                view = inflater.inflate(R.layout.recipe_detail_ingredients_item, parentLayout, false);
                TextView ingredientText = view.findViewById(R.id.ingredient);
                TextView quantityMeasureText = view.findViewById(R.id.textView);
                ingredientText.setText(ingredient.getIngredient());
                quantityMeasureText.setText(String.format("%s %s", ingredient.getQuantity(), ingredient.getMeasure()));
                parentLayout.addView(view);
            }
        }
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.short_description)
        TextView textView;

        @BindView(R.id.card_view_step)
        CardView cardView;

        private StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            if (index < 3) {
                return;
            }
            listItemClickListener.onListItemClick(index - 3);
        }
    }
}