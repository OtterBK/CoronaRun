package com.main.coronavaccine;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VaccinateInfoActivity extends AppCompatActivity {
    private Button btn_webview;
    private TextView btn_webview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccinateinfo);

        btn_webview = (Button) findViewById(R.id.btn_vaccine_webview);
        btn_webview2 = (TextView) findViewById(R.id.btn_vaccine_webview2);

        btn_webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VaccinationWebviewActivity.class);
                startActivity(intent);
            }
        });
        btn_webview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VaccinationWebviewActivity.class);
                startActivity(intent);
            }
        });
    }
}