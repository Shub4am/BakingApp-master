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

package com.nanodegree.udacity.bakingApp.Fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.udacity.bakingApp.Utils.Constants;
import com.nanodegree.udacity.bakingApp.Model.Recipe;
import com.nanodegree.udacity.bakingApp.R;
import com.nanodegree.udacity.bakingApp.Activity.RecipeDetailedActivity;
import com.nanodegree.udacity.bakingApp.Adapters.RecipeDetailedAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailedFragment extends Fragment {

    @BindView(R.id.recipe_details_recyclerview)
    RecyclerView recyclerView;

    private RecipeDetailedAdapter recipeDetailedAdapter;
    private Bundle savedInstanceState;
    private static final String SAVED_LAYOUT_MANAGER_KEY = "saved_layout_manager_detail";
    private int stepSelectedIndex = -1;

    public RecipeDetailedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);
        this.savedInstanceState = savedInstanceState;
        applyConfiguration();
        return rootView;
    }

    private void applyConfiguration() {
        Recipe recipe = getArguments().getParcelable(Constants.RECIPE_DETAILS_FRAGMENT_ARGUMENT);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recipeDetailedAdapter = new RecipeDetailedAdapter(getContext(), (RecipeDetailedActivity) getActivity());
        recyclerView.setAdapter(recipeDetailedAdapter);
        recipeDetailedAdapter.setRecipeData(recipe);
        restoreViewState();
        restoreSelectionIndex();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void restoreViewState() {
        if (savedInstanceState == null) {
            return;
        }
        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER_KEY);
        recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }

    private void restoreSelectionIndex() {
        if (stepSelectedIndex != -1) {
            recipeDetailedAdapter.setSelectionIndex(stepSelectedIndex);
        }
    }

    public void setSelectionIndex(int index) {
        stepSelectedIndex = index;
        if (recipeDetailedAdapter != null) {
            recipeDetailedAdapter.setSelectionIndex(index);
        }
    }

}