package com.fisher.myandroidchartexample.doughunt_chart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fisher.myandroidchartexample.AppMain;
import com.fisher.myandroidchartexample.R;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class DoughuntChartData {
    private static DoughuntChartData instance;
    private ArrayList<Integer> colorArray;
    private ArrayList<PieEntry> pieEntries;

    private static final String TAG = "DoughuntChartData";

   private DoughuntChartData (){
       colorArray = new ArrayList<>();
       pieEntries = new ArrayList<>();
   }

    public static DoughuntChartData getInstance() {
        if(null == instance){
            instance = new DoughuntChartData();
        }
        return instance;
    }

    public ArrayList<Integer> getColorArray() {
        return colorArray;
    }


    public void addColor(int colorId) {
        colorArray.add(AppMain.getApp().getColor(colorId));
    }

    public void addColorByIndex(int index,int colorId){
       if (index > colorArray.size()){
           Log.e(TAG,"Color Array IndexOut Of Bounds!!");
           return;
       }
        colorArray.add(index, AppMain.getApp().getColor(colorId));
    }

    public void setColorArray(@NonNull ArrayList<Integer> colorArray) {
        this.colorArray = colorArray;
    }

    public ArrayList<PieEntry> getPieEntries() {
        return pieEntries;
    }

    public void addPieEntrie(PieEntry pieEntry) {
        pieEntries.add(pieEntry);
    }

    public void addPieEntrieByIndex(int index,PieEntry pieEntry){
        if (index > pieEntries.size()){
            Log.e(TAG,"PieEntries IndexOut Of Bounds!!");
            return;
        }
        pieEntries.add(index, pieEntry);
    }

    public void setPieEntries(@NonNull ArrayList<PieEntry> pieEntries) {
        this.pieEntries = pieEntries;
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


    public void clearsDoughunt(){
        colorArray.clear();
        pieEntries.clear();
    }
}
