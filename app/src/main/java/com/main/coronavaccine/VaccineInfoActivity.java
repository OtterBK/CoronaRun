package com.main.coronavaccine;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VaccineInfoActivity extends AppCompatActivity {

    private Button btn_vaccination;
    private TextView btn_vaccination1;


  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccineinfo);
       // setContentView(R.layout.activity_vaccinateinfo);
        btn_vaccination =  (Button) findViewById(R.id.btn_vaccinate);
        btn_vaccination1 =  (TextView) findViewById(R.id.btn_vaccinate1);

        btn_vaccination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VaccinateInfoActivity.class);
                startActivity(intent);
               // overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        btn_vaccination1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VaccinateInfoActivity.class);
                startActivity(intent);
               // overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }
}