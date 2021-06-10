package com.main.coronavaccine;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final int PERMISSION_REQUEST_CODE = 100;
    public static ArrayList<String[]> st_center = new ArrayList<>();
    ArrayList<String[]> center = new ArrayList<>();
    ArrayList<Double> distance = new ArrayList<>();
    double latitude, longitude;
    private GpsTracker gpsTracker;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;
    int cnt = 0, j = 0;
    Marker[] markers;
    int[] index;
    Button button;
    Button button2;

    private Button btn_webview;
    private TextView btn_webview2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // 지도 객체 생성
        center = MapActivity.st_center;
        distance.clear();
        cnt = 0;
        j = 0;
        index = new int[0];
        markers = new Marker[0];
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);
        // 위치를 반환하는 구현체인 FusedLocationSource 생성
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
        button = (Button) findViewById(R.id.btn_research);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < markers.length; i++) {
                    markers[i].setMap(null);
                }
                load(mNaverMap);
            }
        });

        button2 = (Button) findViewById(R.id.btn_research2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < markers.length; i++) {
                    markers[i].setMap(null);
                }
                load(mNaverMap);
            }
        });

        btn_webview = (Button) findViewById(R.id.btn_book_vaccine1);
        btn_webview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaBookWebviewActivity.class);
                startActivity(intent);
            }
        });

        btn_webview2 = (TextView) findViewById(R.id.btn_book_vaccine2);
        btn_webview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaBookWebviewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onMapReady(@NonNull NaverMap naverMap) {
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        for(int i=0; i< markers.length;i++) {
            markers[i].setMap(null);
        }
        load(mNaverMap);
    }
    public void load(@NonNull NaverMap naverMap){
        gpsTracker = new GpsTracker(MapActivity.this);
        latitude = gpsTracker.getLatitude();     //위도
        longitude = gpsTracker.getLongitude();   //경도
        System.out.println(latitude);
        distance.clear();
        cnt = 0;
        j = 0;
        index = new int[0];
        markers = new Marker[0];
        distanceResult();
        for(int i=0; i<distance.size(); i++){
            if(distance.get(i) < 10) {
                cnt=cnt+1;
            }
        }
        markers = new Marker[cnt];
        index = new int[cnt];
        // 지도상에 마커 표시
        for(int i=0; i<distance.size(); i++){
            if(distance.get(i) < 10) {       //10km 이내
                Marker marker = new Marker();
                marker.setPosition(new LatLng(geocoding.lat_data.get(i),
                        geocoding.long_data.get(i)));
                marker.setMap(naverMap);
                markers[j] = marker;
                index[j] = i;
                System.out.println(index[j]);
                System.out.println(j);
                j = j+1;
            }
        }
        InfoWindow infoWindow = new InfoWindow();
        // 지도를 클릭하면 정보 창을 닫음
        naverMap.setOnMapClickListener((coord, point) -> {
            infoWindow.close();
        });
        // 마커를 클릭하면:
        Overlay.OnClickListener listener = overlay -> {
            Marker marker = (Marker)overlay;
            if (marker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶

                infoWindow.open(marker);

            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }
            return true;
        };

        com.main.coronavaccine.pointAdapter adapter = new com.main.coronavaccine.pointAdapter(MapActivity.this,
                (ViewGroup) this.getWindow().getDecorView(), markers, index);
        infoWindow.setAdapter(adapter);
        for(int i=0; i< markers.length;i++) {
            markers[i].setOnClickListener(listener);
        }
    }
//    public void onBackPressed(){
//        super.onBackPressed();
//        distance.clear();
//        center.clear();
//        geocoding.lat_data.clear();
//        geocoding.long_data.clear();
//        cnt = 0;
//        j = 0;
//        index = new int[0];
//        markers = new Marker[0];
//        latitude=0;
//        longitude=0;
//    }

    public void onBackPressed(){
        this.finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // request code와 권한획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }
    public void distanceResult(){
        double dis = 0.0;
        int k = 1;
        double g_lat = geocoding.lat_data.get(0);
        double g_lon = geocoding.long_data.get(0);
        for(int i=1; i<geocoding.lat_data.size(); i++){
            if((geocoding.lat_data.get(k) == g_lat) && (geocoding.long_data.get(k) == g_lon)){
                geocoding.lat_data.remove(k);
                geocoding.long_data.remove(k);
            }
            else {
                k = k + 1;
                g_lat = geocoding.lat_data.get(i);
                g_lon = geocoding.long_data.get(i);
            }
        }
        for(int i = 0; i< geocoding.lat_data.size(); i++) {
            g_lat = geocoding.lat_data.get(i);
            g_lon = geocoding.long_data.get(i);
            dis = getDistance(latitude, longitude, g_lat, g_lon);
            distance.add(dis);
        }
    }
    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB) / 1000;

        return distance;
    }
}