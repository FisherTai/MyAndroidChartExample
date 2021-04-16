package com.fisher.myandroidchartexample.doughunt_chart;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fisher.myandroidchartexample.BaseActivity;
import com.fisher.myandroidchartexample.R;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.HashMap;
import java.util.Map;

public class ExDoughuntChartActivity extends BaseActivity {
    private static final String TAG = "ExDoughuntChartActivity";
    MyPieChart doughuntChart;


    @Override
    protected int getLayoutView() {
        return R.layout.activity_doughnut_chart;
    }

    @Override
    protected void findView() {
        doughuntChart = findViewById(R.id.pieChart);
    }

    @Override
    protected void setViewData() {
        legendSetting();
        setDoughuntChartChart();
    }

    /**
     * 圖表下方的標註
     */
    private void legendSetting() {
        Legend legend = doughuntChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(getColor(R.color.black));
        legend.setTextSize(20);
        //圖案樣式
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(20);
        //位置
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 螢幕寬度（畫素）
        int height = metric.heightPixels;   // 螢幕高度（畫素）
        float density = metric.density;      // 螢幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 螢幕密度DPI
        Log.d(TAG, "legendSetting: \n width = " + width + "\nheight = " + height + "\n density = " + density + "\n densityDpi =" + densityDpi);
        legend.setXOffset(0f);
        legend.setYOffset(5f);
        //Item的間距
        Log.d(TAG, doughuntChart.getWidth() + "");
        legend.setXEntrySpace((doughuntChart.getWidth()) / 2f / density);
        legend.setYEntrySpace(20f);
    }


    @Override
    protected void onDestroy() {
        DoughuntChartData.getInstance().clearsDoughunt();
        super.onDestroy();
    }

    private void setDoughuntChartChart(){
        DoughuntChartData.getInstance().clearsDoughunt();
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry((float) (Math.random() * 100), ""));
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry((float) (Math.random() * 100), ""));
        DoughuntChartData.getInstance().addGradientColor(R.color.red,R.color.coustom);
        DoughuntChartData.getInstance().addGradientColor(R.color.green,R.color.coustom2);
        doughuntChart.setGradientColors(DoughuntChartData.getInstance().getGradientCombine());
        doughuntChart.setExtraOffsets(20, 5, 20, 5);//與四周的間距，類似從外側設置Padding
        doughuntChart.setData(DoughuntChartData.getInstance().createPieData(false));
        doughuntChart.setEntryLabelTextSize(15);
        doughuntChart.setUsePercentValues(true);
        doughuntChart.setDrawEntryLabels(false);

        doughuntChart.setDrawRoundedSlices(true);

        doughuntChart.setRotationEnabled(false);

        doughuntChart.setCenterTextSize(15);
        doughuntChart.setCenterText("Center Text");
        doughuntChart.setCenterTextColor(getColor(R.color.black));
        doughuntChart.setEntryLabelColor(getColor(R.color.white));
        doughuntChart.setHoleColor(getColor(R.color.transparent));
        doughuntChart.setHoleRadius(70f);
        doughuntChart.getDescription().setEnabled(false);
        doughuntChart.useGradient(true);

        doughuntChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        Legend legend = doughuntChart.getLegend();
        legend.setEnabled(false);
    }

    public void updateData(View view) {
        DoughuntChartData.getInstance().clearsDoughunt();
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry((float) (Math.random() * 100), ""));
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry((float) (Math.random() * 100), ""));
        DoughuntChartData.getInstance().addGradientColor(R.color.red,R.color.coustom);
        DoughuntChartData.getInstance().addGradientColor(R.color.green,R.color.coustom2);
        doughuntChart.notifyDataSetChanged();
        doughuntChart.invalidate();
    }

}
