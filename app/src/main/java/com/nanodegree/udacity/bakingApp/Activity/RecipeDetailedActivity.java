/*
 * PROJECT LICENSE
 *
 * This project was submitted by Shubham Prakash as part of the Android Developer Nanodegree At Udacity.
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

package com.nanodegree.udacity.bakingApp.Activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.nanodegree.udacity.bakingApp.Utils.Constants;
import com.nanodegree.udacity.bakingApp.Model.RecipeIngredients;
import com.nanodegree.udacity.bakingApp.Model.Recipe;
import com.nanodegree.udacity.bakingApp.Model.Step;
import com.nanodegree.udacity.bakingApp.Fragment.RecipeDetailedFragment;
import com.nanodegree.udacity.bakingApp.Fragment.StepDetailFragment;
import com.nanodegree.udacity.bakingApp.Adapters.RecipeDetailedAdapter;
import com.nanodegree.udacity.bakingApp.R;
import com.nanodegree.udacity.bakingApp.Widgets.Widget;

import java.util.ArrayList;

import butterknife.BindString;

import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_INDEX;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_STEP_LIST;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_FRAGMENT_ARGUMENT;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_FRAGMENT_FULLSCREEN_ARGUMENT;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_FRAGMENT_VIDEO_POSITION_ARGUMENT;

public class RecipeDetailedActivity extends AppCompatActivity implements RecipeDetailedAdapter.ListItemClickListener {

    private Recipe recipe;
    private FragmentManager fragmentManager;
    private RecipeDetailedFragment recipeDetailFragment;

    @BindString(R.string.toast_message_widget)
    String mWidgetToastString;
    public CharSequence toastString = "Widget added";
    private String mRecipeName;
    private ArrayList<RecipeIngredients> mIngredients;


    private static final String SAVED_STEP_SELECTED_INDEX_KEY = "saved_step_selected_index";
    private static final String SAVED_RECIPE_KEY = "saved_recipe";
    private int stepSelectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            loadDataFromExtras();
            return;
        }
        loadFromSavedInstanceState(savedInstanceState);
    }

    private void loadDataFromExtras() {
        Intent intent = getIntent();
        if (!intent.hasExtra(Constants.RECIPE_DETAILS)) {
            return;
        }
        Bundle data = intent.getExtras();
        assert data != null;
        recipe = data.getParcelable(Constants.RECIPE_DETAILS);
        mRecipeName = recipe.getName();
        mIngredients = (ArrayList<RecipeIngredients>) recipe.getIngredients();
        updateActionBar();
        openRecipeDetailFragment();
        if (isLargeScreen()) {
            openStepDetailFragment(stepSelectedIndex);
        }
    }

    private void loadFromSavedInstanceState(Bundle savedInstanceState) {
        recipe = savedInstanceState.getParcelable(SAVED_RECIPE_KEY);
        recipeDetailFragment = (RecipeDetailedFragment) fragmentManager.
                findFragmentById(R.id.recipe_details_fragment_container);
        stepSelectedIndex = savedInstanceState.getInt(SAVED_STEP_SELECTED_INDEX_KEY, 0);
        recipeDetailFragment.setSelectionIndex(stepSelectedIndex);
    }

    private void updateActionBar() {
        assert recipe != null;
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(recipe.getName());
    }

    private void openRecipeDetailFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RECIPE_DETAILS_FRAGMENT_ARGUMENT, recipe);
        recipeDetailFragment = new RecipeDetailedFragment();
        recipeDetailFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.recipe_details_fragment_container, recipeDetailFragment)
                .commit();
    }

    @Override
    public void onListItemClick(int index) {
        if (isLargeScreen()) {
            this.stepSelectedIndex = index;
            openStepDetailFragment(index);
            return;
        }
        Intent intent = new Intent(this, StepDetailActivity.class);
        intent.putParcelableArrayListExtra(STEP_DETAILS_STEP_LIST, new ArrayList<>(recipe.getSteps()));
        intent.putExtra(STEP_DETAILS_INDEX, index);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.widget_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.widget:
                createWidget();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void createWidget() {

        String ingredientsList = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mIngredients.size(); i++) {
            RecipeIngredients ingredient = mIngredients.get(i);
            String name = ingredient.getIngredient();
            String measure = ingredient.getMeasure();
            float quantity = ingredient.getQuantity();
            String quantityString = Float.toString(quantity);
            String ing = String.format("%s %s %s \n", quantityString, measure, name);
            sb.append(ing);
            ingredientsList = sb.toString();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.baking_app_widget);
        ComponentName thisWidget = new ComponentName(this, Widget.class);
        remoteViews.setTextViewText(R.id.appwidget_head_text, mRecipeName);
        remoteViews.setTextViewText(R.id.appwidget_text, ingredientsList);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        Toast.makeText(this, toastString,
                Toast.LENGTH_LONG).show();
    }

    private void openStepDetailFragment(int index) {
        Step step = recipe.getSteps().get(index);
        recipeDetailFragment.setSelectionIndex(index);
        Bundle args = new Bundle();
        args.putParcelable(STEP_DETAILS_FRAGMENT_ARGUMENT, step);
        args.putBoolean(STEP_DETAILS_FRAGMENT_FULLSCREEN_ARGUMENT, false);
        args.putLong(STEP_DETAILS_FRAGMENT_VIDEO_POSITION_ARGUMENT, C.TIME_UNSET);
        final StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.step_details_fragment_container, stepDetailFragment)
                .commit();
    }

    private boolean isLargeScreen() {
        return findViewById(R.id.activity_recipe_detail).getTag() != null &&
                findViewById(R.id.activity_recipe_detail).getTag().equals("sw600");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_RECIPE_KEY, recipe);
        outState.putInt(SAVED_STEP_SELECTED_INDEX_KEY, stepSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}