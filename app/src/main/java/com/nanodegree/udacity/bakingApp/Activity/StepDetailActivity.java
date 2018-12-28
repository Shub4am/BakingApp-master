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

import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.nanodegree.udacity.bakingApp.Model.Step;
import com.nanodegree.udacity.bakingApp.Fragment.StepDetailFragment;
import com.nanodegree.udacity.bakingApp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_INDEX;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_STEP_LIST;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_FRAGMENT_ARGUMENT;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_FRAGMENT_FULLSCREEN_ARGUMENT;
import static com.nanodegree.udacity.bakingApp.Utils.Constants.STEP_DETAILS_FRAGMENT_VIDEO_POSITION_ARGUMENT;

public class StepDetailActivity extends AppCompatActivity {

    @BindView(R.id.fragment_step)
    FrameLayout frameLayout;
    @BindView(R.id.button_step_prev)
    Button prevButton;
    @BindView(R.id.step_indicator)
    TextView stepIndicator;
    @BindView(R.id.button_step_next)
    Button nextButton;
    @BindView(R.id.tools_step)
    View stepNavigationView;

    private List<Step> stepList;
    private int activePosition;
    private StepDetailFragment stepDetailFragment;
    private FragmentManager fragmentManager;

    @OnClick(R.id.button_step_prev)
    public void onPrevButtonClick(View view) {
        if (activePosition > 0) {
            updateStep(activePosition - 1, false, false);
        }
    }

    @OnClick(R.id.button_step_next)
    public void onNextButtonClick(View view) {
        if (activePosition < (stepList.size() - 1)) {
            updateStep(activePosition + 1, false, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);
        applyConfiguration(savedInstanceState);
    }

    private void applyConfiguration(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        activePosition = 0;
        stepList = new ArrayList<>();
        if (frameLayout != null) {
            if (savedInstanceState == null) {
                loadDataFromExtras();
            } else {
                loadDataFromSavedInstance(savedInstanceState);
            }
        }
        updatePrevNextButtonStatus();
        updateStep(activePosition, false, true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadDataFromExtras() {
        if (getIntent().getExtras() != null) {
            List<Step> tempList = getIntent().getExtras().getParcelableArrayList(STEP_DETAILS_STEP_LIST);
            if (tempList != null) {
                stepList = tempList;
            }
            activePosition = getIntent().getExtras().getInt(STEP_DETAILS_INDEX);
        }
        openStepDetailFragment(isFullScreen());
    }

    private void loadDataFromSavedInstance(Bundle savedInstanceState) {
        List<Step> tempList = savedInstanceState.getParcelableArrayList(STEP_DETAILS_STEP_LIST);
        if (tempList != null) {
            stepList = tempList;
        }
        activePosition = savedInstanceState.getInt(STEP_DETAILS_INDEX);
        stepDetailFragment = (StepDetailFragment) fragmentManager.findFragmentById(
                frameLayout.getId());
        stepDetailFragment.setFullScreen(isFullScreen());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STEP_DETAILS_STEP_LIST, new ArrayList<Parcelable>(stepList));
        outState.putInt(STEP_DETAILS_INDEX, activePosition);
        super.onSaveInstanceState(outState);
    }

    private void updateLayoutFullscreenMode(boolean fullScreen) {
        if (fullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
        }
        stepNavigationView.setVisibility(fullScreen ? View.GONE : View.VISIBLE);
    }

    private void updatePrevNextButtonStatus() {
        boolean prevStatus = activePosition > 0;
        prevButton.setClickable(prevStatus);
        prevButton.setVisibility(prevStatus ? View.VISIBLE : View.INVISIBLE);
        boolean nextStatus = activePosition < (stepList.size() - 1);
        nextButton.setClickable(nextStatus);
        nextButton.setVisibility(nextStatus ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFullScreen() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                && !TextUtils.isEmpty(stepList.get(activePosition).getVideoUrl());
    }

    private void updateStep(int currentPosition, boolean forceExitFullScreen, boolean fromStart) {
        activePosition = currentPosition;
        stepIndicator.setText(String.format("%s/%s", activePosition, (stepList.size() - 1)));
        setTitle(stepList.get(activePosition).getShortDescription());
        updatePrevNextButtonStatus();
        boolean fullScreen = !forceExitFullScreen && isFullScreen();
        if (!fromStart) {
            openStepDetailFragment(fullScreen);
        }
        if (!TextUtils.isEmpty(stepList.get(activePosition).getVideoUrl())) {
            updateLayoutFullscreenMode(fullScreen);
        }
    }

    private void openStepDetailFragment(boolean fullScreen) {
        Bundle args = new Bundle();
        args.putParcelable(STEP_DETAILS_FRAGMENT_ARGUMENT, stepList.get(activePosition));
        args.putBoolean(STEP_DETAILS_FRAGMENT_FULLSCREEN_ARGUMENT, fullScreen);
        args.putLong(STEP_DETAILS_FRAGMENT_VIDEO_POSITION_ARGUMENT, C.TIME_UNSET);
        stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(frameLayout.getId(), stepDetailFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen() && stepDetailFragment.isFullScreen()) {
            updateStep(activePosition, true, false);
        } else {
            super.onBackPressed();
        }
    }
}