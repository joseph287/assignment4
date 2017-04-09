package com.logan20.draganddropgame;

/*Outside codes were used in this project. jstjoy creation*/


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatisticFragment extends DialogFragment{
    private int[] array;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_player_statistic, container, false);
        LineChart lc = (LineChart)v.findViewById(R.id.lc_linechart);
        List<Entry> entryList = new ArrayList<>();

        for (int a=0;a<array.length;a++){
            entryList.add(new Entry(a+1,array[a]));
        }
        LineDataSet lineDataSet = new LineDataSet(entryList,"Times");
        lc.setData(new LineData(lineDataSet));
        lc.invalidate();
        return v;
    }

    public void setArray(int[] array) {
        this.array = array;
    }
}
