package com.main.coronavaccine;

import android.util.Log;

import com.main.coronavaccine.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class geocoding extends Thread{
    static public String clientId = "igvructlaz"; //애플리케이션 클라이언트 아이디값"
    static public String clientSecret = "mm4nt0unCdnuXDSzTJtLyEbJ3U5LBLK1UsmGQXj5"; //애플리케이션 클라이언트 시크릿값"
    public static ArrayList<Double> lat_data = new ArrayList<>();
    public static ArrayList<Double> long_data = new ArrayList<>();
    public void run(){
        main();
    }

    public static void main() {
        String text = null;
        for(int i = 0; i < MapActivity.st_center.size(); i++) {
            try {
                text = URLEncoder.encode(MapActivity.st_center.get(i)[6], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("검색어 인코딩 실패", e);
            }

            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + text;    // json 결과

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-NCP-APIGW-API-KEY-ID", clientId);
            requestHeaders.put("X-NCP-APIGW-API-KEY", clientSecret);
            String responseBody = get(apiURL, requestHeaders);
            parseData(responseBody);
        }

        Config.geoLoadDone = true;
        MainActivity.btn_find_center.setTextColor(Config.white);

        Log.d("t1","lat_data 로드완료");

    }

    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }

        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static void parseData(String responseBody) {
        double title1, title2;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseBody);
            JSONArray jsonArray = jsonObject.getJSONArray("addresses");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                title1 = Double.parseDouble(item.getString("y"));
                title2 = Double.parseDouble(item.getString("x"));
                lat_data.add(title1);
                long_data.add(title2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
