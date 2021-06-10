package com.main.coronavaccine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.map.NaverMapSdk;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == HandlerMessageType.GEOCODING_LOADING.ordinal())
                Toast.makeText(MainActivity.this, "백신 센터 데이터를 불러오는 중입니다. \n잠시만 기다려주세요.", Toast.LENGTH_LONG).show();
            else if(msg.what == HandlerMessageType.GEOCODING_LOAD_DONE.ordinal())
                Toast.makeText(MainActivity.this, "백신 센터 테이터를 모두 불러왔습니다.", Toast.LENGTH_LONG).show();
        }
    };

    //사용하는 버튼들
    public static Button btn_find_center;
    private Button btn_compare;
    private Button btn_coronaPrevent;
    private Button btn_vaccineInfo;

    private TextView lbl_today;
    private TextView lbl_todayInfected;
    private TextView lbl_todayVaccine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Config.isDarkTheme){ //어두운 테마 선택시 적용
            ConstraintLayout base = findViewById(R.id.Base);
            base.setBackgroundColor(Config.darkGray);
        }

        // 버튼 객체 가져오기
        btn_find_center = (Button) findViewById(R.id.btn_find_center);
        btn_compare = (Button) findViewById(R.id.btn_compare);
        btn_coronaPrevent = (Button) findViewById(R.id.btn_coronaPrevent);
        btn_vaccineInfo = (Button) findViewById(R.id.btn_vaccineInfo);

        MainActivity.btn_find_center.setTextColor(Config.darkGray);
        //버튼별 화면 이동 기능
        btn_find_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Config.geoLoadDone){
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    Message message = handler.obtainMessage(HandlerMessageType.GEOCODING_LOADING.ordinal());
                    handler.sendMessage(message);
                }
            }
        });

        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaCompareActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        btn_coronaPrevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaPreventActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        btn_vaccineInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VaccineInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        lbl_today = (TextView) findViewById(R.id.lbl_today); //오늘 날짜 설정

        String dayString = "";

        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY) < 10) calendar.add(Calendar.DATE, -1);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dow = calendar.get(Calendar.DAY_OF_WEEK);


        if(month < 10){ //한자리 수라면
            dayString += "0";
        }
        dayString += month + ". ";

        if(day < 10){ //한자리 수라면
            dayString += "0";
        }
        dayString += day + ". ";

        String dayName = "일";
        switch(dow){
            case 1: dayName = "일"; break;
            case 2: dayName = "월"; break;
            case 3: dayName = "화"; break;
            case 4: dayName = "수"; break;
            case 5: dayName = "목"; break;
            case 6: dayName = "금"; break;
            case 7: dayName = "토"; break;
            
            default: dayName = "일";
        }

        dayString += "("+dayName+") 10:00 기준";

        lbl_today.setText(dayString);

        lbl_todayInfected = (TextView) findViewById(R.id.lbl_todayInfected); //일일 확진자 수 설정
        lbl_todayVaccine = (TextView) findViewById(R.id.lbl_todayVaccine); //일일 확진자 수 설정

        new Conn().execute();

        new Thread(new Runnable(){
            public void run(){
                loadGeo();
            }
        }).start();

    }

    public void ReadData(ArrayList<String[]> center) throws IOException {
        InputStreamReader is = new InputStreamReader(getResources().openRawResource(R.raw.center), "x-windows-949");
        BufferedReader reader = new BufferedReader(is);
        CSVReader read = new CSVReader(reader);
        String[] record = null;
        while ((record = read.readNext()) != null) {
            String tmp = Arrays.toString(record);
            String[] value= tmp.split(",");
            value[0] = value[0].replace("[", "");
            value[7] = value[7].replace("]", "");
            center.add(value);
        }
    }

    public void loadGeo(){
        ArrayList<String[]> center = MapActivity.st_center;
        try {
            center.clear();
            geocoding.lat_data.clear();
            geocoding.long_data.clear();
            ReadData(center);
            geocoding thread = new geocoding();

            thread.start(); // 지오코딩 데이터 불러오는 중에는 센터 검색 못하게 막기
            thread.join();

            Message message = handler.obtainMessage(HandlerMessageType.GEOCODING_LOAD_DONE.ordinal());
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Conn extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            CoronaParser cp = new CoronaParser();
            CoronaParser.CoronaData coronaData = cp.getCoronaData(false);

            int todayInfected = coronaData.today_infected_total - coronaData.yesterDay_infected_total;
            lbl_todayInfected.setText("▲ "+todayInfected + "\n"+coronaData.today_infected_total);

            int todayVaccine = coronaData.toDay_vaccine;
            lbl_todayVaccine.setText("▲ "+todayVaccine + "\n"+coronaData.toDay_vaccine_total);

            return true;
        }
    }


}