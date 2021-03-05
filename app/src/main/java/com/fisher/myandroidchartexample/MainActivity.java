package com.fisher.myandroidchartexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity {

    Button btnJumpDougchunt;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void findView() {
        btnJumpDougchunt = findViewById(R.id.btn_jump_doughunt_chart);
    }

    @Override
    protected void setViewData() {
        btnJumpDougchunt.setOnClickListener(view -> showActivity(ExDoughuntChartActivity.class));
    }

    private void showActivity(@NonNull Class<?> cls){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }

}