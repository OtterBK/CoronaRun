package com.main.coronavaccine;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class CoronaCompareActivity extends AppCompatActivity {

    PieChart coronaChart;
    PieChart vaccineChart;
    PieChart totalChart;

    String val = "total",text=null;
    private int pos = 1;

    private CoronaParser coronaParser = new CoronaParser();
    private CoronaParser.CoronaData coronaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coronaChart = (PieChart)findViewById(R.id.piechart);

        vaccineChart = (PieChart)findViewById(R.id.piechart);

        totalChart = (PieChart)findViewById(R.id.piechart);


        Switch sw = (Switch)findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Spinner s = (Spinner)findViewById(R.id.spinner2);
                    s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0){
                                val = "corona";
                                setPieChart(coronaChart);

                            }
                            else{
                                val = "vaccine";
                                pos = position;

                                setPieChart(vaccineChart);

                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
                else{
                    val = "total";
                    setPieChart(totalChart);

                }
            }
        });

        Thread tmpTh = new loadCoronaData();
        tmpTh.start();
        try {
            tmpTh.join();
            setPieChart(totalChart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private class loadCoronaData extends Thread implements Runnable{
        public void run() {
            try {
                coronaData = coronaParser.getCoronaData(true); //?????? ???????????? ???????????? ????????? ?????? ????????? ??????

                setPieChart(totalChart);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setPieChart(PieChart pieChart){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        if(val.equals("corona")){

            for(String areaName : coronaData.area_coronaMap.keySet()){
                yValues.add(new PieEntry(coronaData.area_coronaMap.get(areaName), areaName));
            }

            text = "????????? ????????? ???";
        }
        else if(val.equals("vaccine")){


            int vaccineNum = pos -1;

            for(String areaName : coronaData.area_vaccineMap.keySet()){
                yValues.add(new PieEntry(coronaData.area_vaccineMap.get(areaName)[vaccineNum], areaName));
            }

            if (pos > 2){text = "????????? ??????"+ (pos-1) +"??? ?????? ??????";}
            else{text = "?????????"+ pos +"??? ?????? ??????";}
        }
        else if(val.equals("total")){
                yValues.add(new PieEntry(coronaData.today_infected_total - coronaData.yesterDay_infected_total,"?????????"));
                yValues.add(new PieEntry(coronaData.toDay_vaccine,"????????????"));
                text = "?????? ?????????, ?????? 2????????? ?????? ???";
        }
        Description description = new Description();
        description.setText(text); //??????
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EaseInOutCubic); //???????????????

        PieDataSet dataSet = new PieDataSet(yValues,"SIDO");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(17f);
        color cl=new color();
        cl.setColor(17);
        dataSet.setColors(cl.colors);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);

    }


    public void onBackPressed(){
        this.finish();
    }

}

