package com.fisher.myandroidchartexample.doughunt_chart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fisher.myandroidchartexample.AppMain;
import com.fisher.myandroidchartexample.R;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoughuntChartData {
    private static DoughuntChartData instance;
    private ArrayList<Integer> colorArray;
    private ArrayList<PieEntry> pieEntries;
    private Map<Integer,Integer> gradientCombine;

    private static final String TAG = "DoughuntChartData";

   private DoughuntChartData (){
       colorArray = new ArrayList<>();
       pieEntries = new ArrayList<>();
       gradientCombine = new HashMap<>();
   }

    public static DoughuntChartData getInstance() {
        if(null == instance){
            instance = new DoughuntChartData();
        }
        return instance;
    }


    public void addColor(int colorId) {
        colorArray.add(AppMain.getApp().getColor(colorId));
    }

    /**
     * 設置要進行漸層的顏色
     * @param colorId 初始顏色
     * @param gradientId 漸層顏色
     */
    public void addGradientColor(int colorId,int gradientId) {
        colorArray.add(AppMain.getApp().getColor(colorId));
        gradientCombine.put(AppMain.getApp().getColor(colorId),AppMain.getApp().getColor(gradientId));
    }

    public void addPieEntrie(PieEntry pieEntry) {
        pieEntries.add(pieEntry);
    }

    /**
     * @return
     */
    public PieData createPieData( boolean notSlice){

        while (colorArray.size() < pieEntries.size()){
            addColor(R.color.black);
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colorArray);
        pieDataSet.setSelectionShift(0f);
        pieDataSet.setAutomaticallyDisableSliceSpacing(true);
        pieDataSet.setSliceSpace(notSlice ? 0f : 1f);
        pieDataSet.setValueTextSize(0);
        pieDataSet.setValueTextColor(AppMain.getApp().getColor(R.color.white));
        return new PieData(pieDataSet);
    }

    public Map<Integer, Integer> getGradientCombine() {
        Log.d(TAG, "getGradientCombine: "+gradientCombine.size());
        return gradientCombine;
    }

    public void clearsDoughunt(){
        colorArray.clear();
        pieEntries.clear();
        gradientCombine.clear();
    }
}
