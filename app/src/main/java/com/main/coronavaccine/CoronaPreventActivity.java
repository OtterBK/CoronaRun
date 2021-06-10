package com.main.coronavaccine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CoronaPreventActivity extends AppCompatActivity {

    private Button btn_webview;
    private TextView btn_webview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coronaprevent);

        btn_webview = (Button) findViewById(R.id.btn_webview);
        btn_webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaPreventWebviewActivity.class);
                startActivity(intent);
            }
        });

        btn_webview2 = (TextView) findViewById(R.id.btn_webview2);
        btn_webview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaPreventWebviewActivity.class);
                startActivity(intent);
            }
        });
    }

}
