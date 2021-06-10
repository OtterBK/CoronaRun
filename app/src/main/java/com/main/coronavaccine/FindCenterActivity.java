package com.main.coronavaccine;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMapSdk;

public class FindCenterActivity extends AppCompatActivity {

    //사용하는 버튼들

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_center);

        if(Config.isDarkTheme){ //어두운 테마 선택시 적용
            ConstraintLayout base = findViewById(R.id.Base);
            base.setBackgroundColor(Config.darkGray);
        }

    }
}