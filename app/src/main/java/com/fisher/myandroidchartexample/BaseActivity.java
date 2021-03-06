package com.fisher.myandroidchartexample;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        findView();
        setViewData();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
    }

    abstract protected int getLayoutView();
    abstract protected void findView();
    abstract protected void setViewData();
}

