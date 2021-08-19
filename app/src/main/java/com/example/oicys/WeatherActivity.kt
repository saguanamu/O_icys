package com.example.oicys

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class WeatherActivity: AppCompatActivity() {
    private val nx = "60" //위도
    private val ny = "126" //경도
    private val baseDate = "20210819" //조회하고싶은 날짜
    private val baseTime = "0500" //조회하고싶은 시간
    private val type = "json" //조회하고 싶은 type(json, xml 중 고름)




    @Throws(IOException::class, JSONException::class)
    fun lookUpWeather() {

//		참고문서에 있는 url주소
        val apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"
        //         홈페이지에서 받은 키
        val serviceKey = "d8IPeq5KYx2%2BRSqv%2Fct2kyU2sJdAv7H9Z8Bi%2FyEMoi8CME2GPXtVNwjal3g6chI74c88dlDrx7bURoZBLQ6pbQ%3D%3D"
        val urlBuilder = StringBuilder(apiUrl)
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey)
        urlBuilder.append(
            "&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(
                nx,
                "UTF-8"
            )
        ) //경도
        urlBuilder.append(
            "&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(
                ny,
                "UTF-8"
            )
        ) //위도
        urlBuilder.append(
            "&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(
                baseDate,
                "UTF-8"
            )
        ) /* 조회하고싶은 날짜*/
        urlBuilder.append(
            "&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(
                baseTime,
                "UTF-8"
            )
        ) /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append(
            "&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(
                type,
                "UTF-8"
            )
        ) /* 타입 */

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        val url = URL(urlBuilder.toString())
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Content-type", "application/json")
        println("Response code: " + conn.responseCode)
        val rd: BufferedReader
        rd = if (conn.responseCode >= 200 && conn.responseCode <= 300) {
            BufferedReader(InputStreamReader(conn.inputStream))
        } else {
            BufferedReader(InputStreamReader(conn.errorStream))
        }
        val sb = StringBuilder()
        var line: String?
        while (rd.readLine().also { line = it } != null) {
            sb.append(line)
        }
        rd.close()
        conn.disconnect()
        val result = sb.toString()

        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
        val jsonObj_1 = JSONObject(result)
        val response = jsonObj_1.getString("response")

        // response 로 부터 body 찾기
        val jsonObj_2 = JSONObject(response)
        val body = jsonObj_2.getString("body")

        // body 로 부터 items 찾기
        val jsonObj_3 = JSONObject(body)
        val items = jsonObj_3.getString("items")
        Log.i("ITEMS", items)

        // items로 부터 itemlist 를 받기
        var jsonObj_4 = JSONObject(items)
        val jsonArray = jsonObj_4.getJSONArray("item")

        var weather:String?=null
        var tmperature:String?=null
        for (i in 0 until jsonArray.length()) {
            jsonObj_4 = jsonArray.getJSONObject(i)
            val fcstValue = jsonObj_4.getString("fcstValue")
            val category = jsonObj_4.getString("category")
            if (category == "SKY") {
                weather = "현재 날씨는 "
                if (fcstValue == "1") {
                    weather += "맑은 상태로"
                } else if (fcstValue == "2") {
                    weather += "비가 오는 상태로 "
                } else if (fcstValue == "3") {
                    weather += "구름이 많은 상태로 "
                } else if (fcstValue == "4") {
                    weather += "흐린 상태로 "
                }
            }
            if (category == "T3H" || category == "T1H") {
                tmperature = "기온은 $fcstValue℃ 입니다."
            }
            Log.i("WEATHER_TAG", weather + tmperature)
        }
    }
}