package com.fisher.myandroidchartexample.doughunt_chart;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fisher.myandroidchartexample.AppMain;
import com.fisher.myandroidchartexample.BaseActivity;
import com.fisher.myandroidchartexample.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExDoughuntChartActivity extends BaseActivity {
    private static final String TAG = "ExDoughuntChartActivity";
    MyPieChart doughuntChart;
    RecyclerView recyclerView;
    Button button;

    DoughuntChartListAdapter mAdapter;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_doughnut_chart;
    }

    @Override
    protected void findView() {
        recyclerView = findViewById(R.id.rvlist);
        doughuntChart = findViewById(R.id.pieChart);
        button = findViewById(R.id.btn_update);
    }

    @Override
    protected void setViewData() {
//        legendSetting();
//        setDoughuntChartChart();
        mAdapter = new DoughuntChartListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);
        button.setOnClickListener(view -> {
            update();
        });
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


    private void setDoughuntChartChart() {
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry(40f, "多方"));
        DoughuntChartData.getInstance().addPieEntrie(new PieEntry(10f, "空方"));
        DoughuntChartData.getInstance().addColor(R.color.red);
        DoughuntChartData.getInstance().addColor(R.color.green);
//        doughuntChart.setData(DoughuntChartData.getInstance().createPieData());
        doughuntChart.setExtraOffsets(20, 5, 20, 5);//與四周的間距，類似從外側設置Padding
        doughuntChart.setEntryLabelTextSize(15);
        doughuntChart.setUsePercentValues(true);
        doughuntChart.setDrawEntryLabels(false);
        doughuntChart.setEntryLabelColor(getColor(R.color.white));

        doughuntChart.setCenterTextSize(15);
        doughuntChart.setCenterText("Center Text");
        doughuntChart.setCenterTextColor(getColor(R.color.black));

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


    public class DoughuntChartListAdapter extends RecyclerView.Adapter<DoughuntChartListAdapter.ChartVH> {

        private Context mCtx;
        ArrayList<Bean> itemList = new ArrayList<>();

        public DoughuntChartListAdapter(Context mCtx) {
            this.mCtx = mCtx;

            itemList.add(new Bean("XXXX", 0f, 100f));
            itemList.add(new Bean("QQQQ", 1f, 98f));
        }

        public void setItemList(ArrayList<Bean> itemList) {
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public ChartVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(mCtx).inflate(R.layout.item_dc, viewGroup, false);
            return new DoughuntChartListAdapter.ChartVH(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ChartVH chartVH, int i) {
            Bean item = itemList.get(i);
            DoughuntChartData.getInstance().clearsDoughunt();
            chartVH.tvBull.setText(item.centerValue);
            chartVH.pieChart.setCenterText(item.centerValue);
            DoughuntChartData.getInstance().addPieEntrie(new PieEntry(item.buy, "多头"));
            DoughuntChartData.getInstance().addColor(R.color.coustom);
            DoughuntChartData.getInstance().addPieEntrie(new PieEntry(item.sell, "空头"));
            DoughuntChartData.getInstance().addColor(R.color.coustom2);

            Map<Integer, String> map = new HashMap<>();
            map.put(AppMain.getApp().getColor(R.color.coustom), "#ff3b0a");
            map.put(AppMain.getApp().getColor(R.color.coustom2), "#121e55");
            chartVH.pieChart.setGradientColors(map);

            chartVH.pieChart.setData(DoughuntChartData.getInstance().createPieData(item.buy * item.sell == 0));
            chartVH.pieChart.setEntryLabelTextSize(15);
            chartVH.pieChart.setUsePercentValues(true);
            chartVH.pieChart.setDrawEntryLabels(false);

            if (item.buy / (item.buy + item.sell) * 100 <= 2 ||
                    item.buy / (item.buy + item.sell) * 100 >= 98) {
                chartVH.pieChart.setDrawRoundedSlices(false);
            } else {
                chartVH.pieChart.setDrawRoundedSlices(true);
            }
            chartVH.pieChart.setRotationEnabled(false);

            chartVH.pieChart.setCenterTextSize(15);
            chartVH.pieChart.setCenterTextColor(AppMain.getApp().getColor(R.color.black));

            chartVH.pieChart.setHoleColor(AppMain.getApp().getColor(R.color.transparent));
            chartVH.pieChart.setHoleRadius(70f);
            chartVH.pieChart.getDescription().setEnabled(false);
            chartVH.pieChart.useGradient(true);
            Legend legend = chartVH.pieChart.getLegend();
            legend.setEnabled(false);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }


        class ChartVH extends RecyclerView.ViewHolder {
            MyPieChart pieChart;
            TextView tvBull;

            ChartVH(View itemView) {
                super(itemView);
                tvBull = itemView.findViewById(R.id.home_doughunt_tv_bull);
                pieChart = itemView.findViewById(R.id.pieChart);
            }
        }
    }

    int intflag = 0;

    private void update() {
        Log.i(TAG, "update: ");
        ArrayList<Bean> itemList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Bean bean = new Bean(intflag++ + "", (float) (Math.random() * 100), (float) (Math.random() * 100));
            itemList.add(bean);
        }

        mAdapter.setItemList(itemList);
        mAdapter.notifyDataSetChanged();
    }


    public class Bean {
        String centerValue;

        public Bean(String centerValue, float sell, float buy) {
            this.centerValue = centerValue;
            this.sell = sell;
            this.buy = buy;
        }

        float sell;
        float buy;
    }
}
