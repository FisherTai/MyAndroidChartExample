package com.fisher.myandroidchartexample;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ExDoughuntChartActivity extends BaseActivity {

    PieChart doughuntChart;
    private ArrayList<Integer> colorArray;
    private ArrayList<PieEntry> pieEntries;

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
        pieEntries = new ArrayList<>();
        colorArray = new ArrayList<>();

        colorArray.add(getColor(R.color.teal_200));
        colorArray.add(getColor(R.color.teal_700));

        pieEntries.add(new PieEntry(80f, "item1"));
        pieEntries.add(new PieEntry(20f, "item2"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colorArray);
        pieDataSet.setSelectionShift(10f);
        pieDataSet.setAutomaticallyDisableSliceSpacing(true);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.teal_700));

        PieData pieData = new PieData(pieDataSet);

        doughuntChart.setData(pieData);
        doughuntChart.setExtraOffsets(20, 5, 20, 5);//與四周的間距，類似從外側設置Padding

        doughuntChart.setDrawEntryLabels(false);
        //        pieChart.setEntryLabelTextSize(15);
        doughuntChart.setUsePercentValues(true);
//        BarChart barChart;
        doughuntChart.setCenterTextSize(15);
        doughuntChart.setCenterText("Center Text");
        doughuntChart.setCenterTextColor(getColor(R.color.purple_500));
        doughuntChart.setHoleColor(getColor(R.color.teal_200));
        doughuntChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        Legend legend = doughuntChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(getColor(R.color.purple_200));
        legend.setTextSize(20);
        legend.setFormSize(20);
        legend.setXOffset(10f);
        legend.setYOffset(5f);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(20f);
        doughuntChart.getDescription().setEnabled(false);


    }



}
