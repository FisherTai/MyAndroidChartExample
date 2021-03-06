package com.fisher.myandroidchartexample.doughunt_chart;

import android.util.DisplayMetrics;
import android.util.Log;

import com.fisher.myandroidchartexample.AppMain;
import com.fisher.myandroidchartexample.BaseActivity;
import com.fisher.myandroidchartexample.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class ExDoughuntChartActivity extends BaseActivity {
    private static final String TAG = "ExDoughuntChartActivity";
    PieChart doughuntChart;


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
    private void legendSetting(){
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
        Log.d(TAG, "legendSetting: \n width = "+width +"\nheight = "+height+"\n density = "+density+"\n densityDpi ="+densityDpi);
        legend.setXOffset(0f);
        legend.setYOffset(5f);
        //Item的間距
        legend.setXEntrySpace((width-40)/2f/density);
        legend.setYEntrySpace(20f);
    }


    private void setDoughuntChartChart(){
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry(40f, "多方"));
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry(10f, "空方"));
        DoughuntChartData.getInstance().addColor(R.color.red);
        DoughuntChartData.getInstance().addColor(R.color.green);
        doughuntChart.setData(DoughuntChartData.getInstance().createPieData());
        doughuntChart.setExtraOffsets(20, 5, 20, 5);//與四周的間距，類似從外側設置Padding
        doughuntChart.setEntryLabelTextSize(15);
        doughuntChart.setUsePercentValues(true);
        doughuntChart.setDrawEntryLabels(false);
        doughuntChart.setEntryLabelColor(getColor(R.color.white));

//        doughuntChart.setCenterTextSize(15);
//        doughuntChart.setCenterText("Center Text");
//        doughuntChart.setCenterTextColor(getColor(R.color.black));

        doughuntChart.setHoleColor(getColor(R.color.transparent));
        doughuntChart.setHoleRadius(70f);
        doughuntChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        doughuntChart.getDescription().setEnabled(false);
    }


    @Override
    protected void onDestroy() {
        DoughuntChartData.getInstance().clearsDoughunt();
        super.onDestroy();
    }
}
