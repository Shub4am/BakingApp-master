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

package com.nanodegree.udacity.bakingApp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.nanodegree.udacity.bakingApp.Model.Recipe;
import com.nanodegree.udacity.bakingApp.R;
import com.nanodegree.udacity.bakingApp.Utils.NetworkUtils;
import com.nanodegree.udacity.bakingApp.Utils.SimpleIdlingResource;
import com.nanodegree.udacity.bakingApp.Adapters.RecipeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nanodegree.udacity.bakingApp.Utils.Constants.RECIPE_DETAILS;

public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {

    @BindView(R.id.tv_error)
    TextView mErrorTextView;
    @BindView(R.id.prgressBar)
    ProgressBar mLoadingBar;
    @BindView(R.id.recyclerView)
    RecyclerView mBakeRecyclerView;
    @BindString(R.string.json)
    String mRecipeURL;
    @BindString(R.string.error_message)
    String mNetworkError;
    @BindString(R.string.no_internet_connection)
    String mNoNetwork;
    private static ArrayList<Recipe> mDataSource;
    private RecipeAdapter mBakeRecyclerViewAdapter;
    @Nullable
    private SimpleIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        ButterKnife.bind(this);
        mDataSource = new ArrayList<>();
        if (findViewById(R.id.tablet_layout) != null) {
            mBakeRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        } else {
            mBakeRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
            mBakeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mBakeRecyclerViewAdapter = new RecipeAdapter(mDataSource, this);
        mBakeRecyclerView.setHasFixedSize(true);
        mBakeRecyclerView.setAdapter(mBakeRecyclerViewAdapter);
        if (isConnected()) {
            applyIdlingConfiguration();
            fetchRecipeData();

        } else {
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(mNoNetwork);
        }
    }

    private void fetchRecipeData() {
        mLoadingBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = NetworkUtils.getInstance(this).getRequestQueue();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, mRecipeURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        ArrayList<Recipe> recipes = new ArrayList<>();
                        try {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject recipeObject = array.getJSONObject(i);
                                Gson gson = new GsonBuilder().create();
                                Recipe r = gson.fromJson(String.valueOf(recipeObject), Recipe.class);
                                recipes.add(r);
                            }
                            mLoadingBar.setVisibility(View.INVISIBLE);
                            mDataSource = recipes;
                            mBakeRecyclerViewAdapter.setDataSource(mDataSource);
                            if (idlingResource != null) {
                                idlingResource.setIdleState(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mLoadingBar.setVisibility(View.INVISIBLE);
                        mErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("VisibleForTests")
    private void applyIdlingConfiguration() {
        RecipeActivity recipeActivity = RecipeActivity.this;
        idlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        idlingResource.setIdleState(false);
    }

    protected boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnectedOrConnecting());
        }
        return true;
    }

    @Override
    public void onListItemClick(Recipe recipe) {

        Intent intent = new Intent(this, RecipeDetailedActivity.class);
        intent.putExtra(RECIPE_DETAILS, recipe);
        startActivity(intent);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

}

