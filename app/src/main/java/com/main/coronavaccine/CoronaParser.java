package com.main.coronavaccine;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CoronaParser {

    private static final String TAG = "CoronaParser";

    private static String infected_myKey = "pZIM3WTjj9tP%2FmyJtWPrKKnuOCAmpzNfOmj%2BjxYX0whNj899DFHonv%2BN4awa%2BVeHWOFA2ZVHbku691BHkjxWfA%3D%3D";
    private static String infected_requestUrl = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey=";


    private static String vaccine_myKey = ""; //키 없어도 됨
    private static String vaccine_requestUrl = "https://nip.kdca.go.kr/irgd/cov19stats.do?list=all";

    private static String corona_area_myKey = "TOp46i4r0bSMZAVr2Nq5Bz2olGyh4G4d0mmtZyn30L3CHGn76t84RiPqwhWhHnLZY8Wqr9IEwg5rb9%2ByqWf8uQ%3D%3D";
    private static String corona_area_requestUrl = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson?serviceKey="+corona_area_myKey+"&pageNo=1&numOfRows=10";

    private static String vaccine_area_myKey = "TOp46i4r0bSMZAVr2Nq5Bz2olGyh4G4d0mmtZyn30L3CHGn76t84RiPqwhWhHnLZY8Wqr9IEwg5rb9%2ByqWf8uQ%3D%3D";
    private static String vaccine_area_requestUrl = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?page=1&perPage=20&returnType=XML&cond%5BbaseDate%3A%3AGT%5D=";

    private String corona_areas[] = {"제주", "경남", "경북", "전남", "전북", "충남", "충북", "강원", "경기", "세종", "울산", "대전"
            , "광주", "인천", "대구", "부산", "서울"};
    private String vaccine_areas[] = {"서울특별시", "부산광역시", "대구광역시", "인천광역시", "대전광역시", "광주광역시", "울산광역시", "세종특별자치시", "강원도", "충청북도",
            "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"};

    private String date = "20210603";
    private String yesterDay = "20210602";
    public CoronaParser(){

        Calendar cal = Calendar.getInstance();
//        if(cal.get(Calendar.HOUR_OF_DAY) < 9 || (cal.get(Calendar.HOUR_OF_DAY) == 9 && cal.get(Calendar.MINUTE) < 40)){ //금일 10시 이전이면 어제자로
//            cal.add(Calendar.DATE, -1);
//        }
        if(cal.get(Calendar.HOUR_OF_DAY) < 10) cal.add(Calendar.DATE, -1);


        setDay(cal);

    }

    public String setDay(Calendar calendar){

        String dayString = "";

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dayString += year;
        if(month < 10){
            dayString += "0";
        }
        dayString += month;

        if(day < 10){
            dayString += "0";
        }
        dayString += day;

        this.date = dayString;

        String yesterDay_dayString = "";
        calendar.add(Calendar.DATE, -1);

        int yet_year = calendar.get(Calendar.YEAR);
        int yet_month = calendar.get(Calendar.MONTH) + 1;
        int yet_day = calendar.get(Calendar.DAY_OF_MONTH);

        yesterDay_dayString += yet_year;
        if(yet_month < 10){
            yesterDay_dayString += "0";
        }
        yesterDay_dayString += yet_month;

        if(yet_day < 10){
            yesterDay_dayString += "0";
        }
        yesterDay_dayString += yet_day;

        this.yesterDay = yesterDay_dayString;

        return this.date;

    }

    public CoronaData getCoronaData(boolean involveAreaData){
        CoronaData coronaData = new CoronaData();

        int result_yesterday[] = request(REQUEST_DAY_TYPE.YESTERDAY);
        coronaData.yesterDay_infected_total = result_yesterday[0];
        //coronaData.yesterDay_vaccine_total = result_yesterday[1];

        int result_today[] = request(REQUEST_DAY_TYPE.TODAY);
        coronaData.today_infected_total = result_today[0];
        coronaData.toDay_vaccine = result_today[1];
        coronaData.toDay_vaccine_total = result_today[2];

        if(involveAreaData){ //지역 데이터도 필요하다면
            requestAreaAndSet(coronaData, REQUEST_DATA_TYPE.CORONA);
            requestAreaAndSet(coronaData, REQUEST_DATA_TYPE.VACCINE);
        }


        return coronaData;
    }

    public CoronaData requestAreaAndSet(CoronaData coronaData, REQUEST_DATA_TYPE request_data_type){

        try {
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();

            String requestDate = this.date;
            String url = "";
            if(request_data_type == REQUEST_DATA_TYPE.CORONA){
                url = corona_area_requestUrl + "&startCreateDt="+requestDate+"&endCreateDt="+requestDate;
            } else if(request_data_type == REQUEST_DATA_TYPE.VACCINE){
                //백신의 경우 날짜를 ㅇㅇㅇㅇ-ㅇㅇ-ㅇㅇ 로 해야함
                String year = requestDate.substring(0, 4);
                String month = requestDate.substring(4, 6);
                String day = requestDate.substring(6, 8);

                requestDate = year + "-" + month + "-" + day;
                url = vaccine_area_requestUrl + requestDate + "&serviceKey=" + vaccine_area_myKey;
            }

            Log.d(TAG, url);

            Document doc = dBuilder.parse(url);

            doc.getDocumentElement().normalize();
            Log.d(TAG, "Root element: " + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("item");
            Log.d(TAG, "파싱할 리스트 수 : "+ nList.getLength());  // 파싱할 리스트 수

            if(request_data_type == REQUEST_DATA_TYPE.CORONA){
                for(int i = 0; i < nList.getLength(); i++){
                    Node nNode = nList.item(i);
                    if(nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element eElement = (Element) nNode;
                        String areaName = MyUtility.getTagValue("gubun", eElement);
                        if(coronaData.area_coronaMap.containsKey(areaName)){ //있는 지역이면
                            String infectedStr = MyUtility.getTagValue("defCnt", eElement);
                            int infectedNum = (int) Double.parseDouble(infectedStr);
                            coronaData.area_coronaMap.put(areaName, infectedNum); //해당 지역의 누적 감염자 수 put
                        }

                    }
                }
            } else if(request_data_type == REQUEST_DATA_TYPE.VACCINE){ //백신은 파싱 과정이 까다로움
                for(int i = 0; i < nList.getLength(); i++){
                    Node nNode = nList.item(i);
                    if(nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element eElement = (Element) nNode;
                        NodeList data = eElement.getChildNodes();

                        String areaName = "";
                        String firstCnt = "";
                        String secondCnt = "";
                        String totalFirstCnt = "";
                        String totalSecondCnt = "";
                        for(int j = 0; j < data.getLength(); j++){
                            Element tmpEle = (Element) data.item(j);
                            String dataName = tmpEle.getAttribute("name");
                            if(dataName.equals("sido")){ //지역명 데이터면
                                areaName = tmpEle.getFirstChild().getNodeValue(); //값을 가져옴
                            } else if(dataName.equals("firstCnt")){
                                firstCnt = tmpEle.getFirstChild().getNodeValue();
                            } else if(dataName.equals("secondCnt")){
                                secondCnt = tmpEle.getFirstChild().getNodeValue();
                            } else if(dataName.equals("totalFirstCnt")){
                                totalFirstCnt = tmpEle.getFirstChild().getNodeValue();
                            } else if(dataName.equals("totalSecondCnt")){
                                totalSecondCnt = tmpEle.getFirstChild().getNodeValue();
                            }
                        }


                        if(!areaName.equals("")  && !firstCnt.equals("") && !secondCnt.equals("")
                                && !totalFirstCnt.equals("") && !totalSecondCnt.equals("")){ //셋 다 뭔가 데이터가 있다면
                            if(coronaData.area_vaccineMap.containsKey(areaName)){ //있는 지역이면

                                int vaccineArr[] = new int[4];

                                vaccineArr[0] = (int) Double.parseDouble(firstCnt);
                                vaccineArr[1] = (int) Double.parseDouble(secondCnt);
                                vaccineArr[2] = (int) Double.parseDouble(totalFirstCnt);
                                vaccineArr[3] = (int) Double.parseDouble(totalSecondCnt);


                                coronaData.area_vaccineMap.put(areaName, vaccineArr); //해당 지역의 2차 접종자 수 put
                            }
                        }
                    }
                }
            }


        }catch (Exception e){

        }

        return coronaData;

    }

    public int[] request(REQUEST_DAY_TYPE request_day_type){

        int result[] = new int[3];

        try {
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();

            String requestDate = "";
            if(request_day_type == REQUEST_DAY_TYPE.TODAY){
                requestDate = this.date;
            } else if(request_day_type == REQUEST_DAY_TYPE.YESTERDAY){
                requestDate = this.yesterDay;
            }

            String url = infected_requestUrl + infected_myKey;
            url += "&pageNo=1&numOfRows=10&startCreateDt="+requestDate+"&endCreateDt="+requestDate;
            Log.d(TAG, url);

            Document doc = dBuilder.parse(url);

            doc.getDocumentElement().normalize();
            Log.d(TAG, "Root element: " + doc.getDocumentElement().getNodeName());


            NodeList nList = doc.getElementsByTagName("item");
            Log.d(TAG, "파싱할 리스트 수 : "+ nList.getLength());  // 파싱할 리스트 수

            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    String infectedStr = MyUtility.getTagValue("decideCnt", eElement);
                    result[0] = (int) Double.parseDouble(infectedStr);

                }
            }

            if(request_day_type == REQUEST_DAY_TYPE.TODAY){ //백신은 오늘자 조회시에만 확인 가능, API 응답값이 이래서 어쩔 수 없음
                url = vaccine_requestUrl + vaccine_myKey;
                url += "&pageNo=1&numOfRows=10&startCreateDt="+requestDate;
                Log.d(TAG, url);

                doc = dBuilder.parse(url);

                doc.getDocumentElement().normalize();
                Log.d(TAG, "Root element: " + doc.getDocumentElement().getNodeName());


                nList = doc.getElementsByTagName("item");
                Log.d(TAG, "파싱할 리스트 수 : "+ nList.getLength());  // 파싱할 리스트 수


                Node nNode = nList.item(0); //첫 번째 값만 필요
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String vaccineStr = MyUtility.getTagValue("secondCnt", eElement);
                    result[1] = (int) Double.parseDouble(vaccineStr);
                }

                nNode = nList.item(2); //세 번째 값만 필요
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String vaccineStr = MyUtility.getTagValue("secondCnt", eElement);
                    result[2] = (int) Double.parseDouble(vaccineStr);
                }
            }
            

        }catch(Exception e){
            Log.d(TAG, "error1 - url 요청실패");
            e.printStackTrace();
        }

        return result;

    }

    public class CoronaData{

        public int yesterDay_infected_total;
        public int today_infected_total;
        public int toDay_vaccine;
        public int toDay_vaccine_total;



        public HashMap<String, Integer> area_coronaMap = new HashMap<String, Integer>();
        public HashMap<String, int[]> area_vaccineMap = new HashMap<String, int[]>();

        public CoronaData(){
            for(String areaName : corona_areas){ //지역별 데이터 초기화
                area_coronaMap.put(areaName, 0);
            }

            for(String areaName : vaccine_areas){ //지역별 데이터 초기화
                area_vaccineMap.put(areaName, null);
            }
        }

    }

    enum REQUEST_DAY_TYPE{

        YESTERDAY, TODAY

    }

    enum REQUEST_DATA_TYPE{

        CORONA, VACCINE

    }

}


