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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.udacity.bakingApp.Model.Recipe;
import com.nanodegree.udacity.bakingApp.R;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecyclerViewHolder> {
    private ArrayList<Recipe> mDataSource;
    private final ListItemClickListener mListItemClickListener;

    public RecipeAdapter(ArrayList<Recipe> dataSource, ListItemClickListener listItemClickListener) {
        mDataSource = dataSource;
        mListItemClickListener = listItemClickListener;
    }

    public void setDataSource(ArrayList<Recipe> dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();
    }

    @Override
    public @NonNull
    RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mDataSource == null) {
            return 0;
        }
        return mDataSource.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(Recipe recipe);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_recipe)
        TextView mRecipeTextView;
        @BindView(R.id.tv_serving)
        TextView mServingsTextView;
        @BindString(R.string.serves)
        String mServes;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        void bind(int listIndex) {
            Recipe recipe = mDataSource.get(listIndex);
            mRecipeTextView.setText(recipe.getName());
            String servingCount = Integer.toString(recipe.getServings());
            String servings = String.format("%s %s", mServes, servingCount);
            mServingsTextView.setText(servings);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Recipe recipe = mDataSource.get(position);
            mListItemClickListener.onListItemClick(recipe);
        }
    }
}

