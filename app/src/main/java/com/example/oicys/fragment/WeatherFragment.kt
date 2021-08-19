package com.example.oicys.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.oicys.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_weather.view.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Integer.parseInt
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : Fragment() {

    val num_of_rows = 10
    val page_no = 1
    val data_type = "JSON"
    var base_time = "1100"
    val base_data = 20210820
    val nx = "60"
    val ny = "127"

    data class WEATHER (
        val response : RESPONSE
    )
    data class RESPONSE (
        val header : HEADER,
        val body : BODY
    )
    data class HEADER(
        val resultCode : Int,
        val resultMsg : String
    )
    data class BODY(
        val dataType : String,
        val items : ITEMS
    )
    data class ITEMS(
        val item : List<ITEM>
    )
    data class ITEM(
        val baseDate : Int,
        val baseTime : Int,
        val category : String,
        val fcstValue: String
    )


    interface WeatherInterface {
        @GET("getVilageFcst?serviceKey=d8IPeq5KYx2%2BRSqv%2Fct2kyU2sJdAv7H9Z8Bi%2FyEMoi8CME2GPXtVNwjal3g6chI74c88dlDrx7bURoZBLQ6pbQ%3D%3D")
        fun GetWeather(
            @Query("dataType") data_type : String,
            @Query("numOfRows") num_of_rows : Int,
            @Query("pageNo") page_no : Int,
            @Query("base_date") base_date : Int,
            @Query("base_time") base_time : String,
            @Query("nx") nx : String,
            @Query("ny") ny : String
        ): Call<WEATHER>
    }


    val retrofit = Retrofit.Builder()
        ?.baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/") // 마지막 / 반드시 들어가야 함
        ?.addConverterFactory(GsonConverterFactory.create()) // converter 지정
        ?.build() // retrofit 객체 생성


    object ApiObject {
        val retrofitService: WeatherInterface? by lazy {
            val retrofit = Retrofit.Builder()
                ?.baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/") // 마지막 / 반드시 들어가야 함
                ?.addConverterFactory(GsonConverterFactory.create()) // converter 지정
                ?.build() // retrofit 객체 생성
            retrofit?.create(WeatherInterface::class.java)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_weather, container, false)
        super.onCreate(savedInstanceState)

        // 현재 시간 구하기
        val cal = Calendar.getInstance()
        val df: DateFormat = SimpleDateFormat("HH00")
        val time: String? = df.format(cal.time)
        base_time = timeChange(time)

        var tem:String=""


        val call = ApiObject.retrofitService?.GetWeather(data_type, num_of_rows, page_no, base_data, base_time, nx, ny)
        if (call != null) {
            call.enqueue(object : retrofit2.Callback<WEATHER>{
                override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                    if (response.isSuccessful){
                        Log.d("TAG: ", response.body().toString())
                        Log.d("TAG: ", response.body()!!.response.body.items.item.toString())
                        Log.d("TAG: ", response.body()!!.response.body.items.item[0].category)

                        var nowTemp = response.body()!!.response.body.items.item[6].fcstValue // T3H
                        var nowReh = response.body()!!.response.body.items.item[3].fcstValue // REH
                        var nowSky = response.body()!!.response.body.items.item[5].fcstValue // SKY
                        var nowRain = response.body()!!.response.body.items.item[1].fcstValue // PTY

                        for(i in 1 until 10){
                            when(response.body()!!.response.body.items.item[i].category) {
                                "T3H" -> nowTemp = response.body()!!.response.body.items.item[i].fcstValue // T3H
                                "REH" -> nowReh = response.body()!!.response.body.items.item[i].fcstValue // REH
                                "SKY" -> nowSky = response.body()!!.response.body.items.item[i].fcstValue // SKY
                                "PTY" -> nowRain = response.body()!!.response.body.items.item[i].fcstValue // PTY
                                else -> false
                            }
                        }


                        // 기온
                        tem = nowTemp
                        var spannable = SpannableStringBuilder("현재 온도: ")
                        spannable.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            6, // start
                            7, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        spannable.insert(7, nowTemp+"°C")
                        view.tv_temp.text = spannable

                        // 습도

                        spannable = SpannableStringBuilder("습도: ")
                        spannable.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            3, // start
                            4, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        spannable.insert(4, nowReh+"%")
                        view.tv_reh.text = spannable

                        // 하늘 / 강수 형태

                        var nowWet:String =""
                        when(nowSky){
                            "1"-> nowWet = "맑음"
                            "2"-> {
                                nowWet = "비"
                                when (nowRain) {
                                    "1"->nowWet = "비"
                                    "2"->nowWet = "비/눈"
                                    "3"->nowWet = "눈"
                                    "4"->nowWet = "비(소나기)"
                                    else -> false

                                }
                            }
                            "3"->  nowWet = "구름 많음"
                            "4"->  nowWet = "흐림"
                            else -> false
                        }
                        spannable = SpannableStringBuilder("날씨: ")
                        spannable.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            3, // start
                            4, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        spannable.insert(4, nowWet)
                        view.tv_sky.text = spannable


                        // 기온 별 옷차림
                        val temp = parseInt(tem)
                        var txCloth:String = ""
                        if(temp>=28){
                            view.image_cloth.setImageResource(R.drawable.cloth_1)
                            txCloth = "민소매, 반팔, 반바지, 원피스"
                        } else if(temp>=23){
                            view.image_cloth.setImageResource(R.drawable.cloth_2)
                            txCloth = "반팔, 얇은 셔츠, 반바지, 면바지"
                        }else if(temp>=20){
                            view.image_cloth.setImageResource(R.drawable.cloth_3)
                            txCloth = "얇은 가디건, 긴팔, 면바지, 청바지"
                        }else if(temp>=17){
                            view.image_cloth.setImageResource(R.drawable.cloth_4)
                            txCloth = "얇은 니트, 맨투맨, 가디건, 청바지"
                        }else if(temp>=12){
                            view.image_cloth.setImageResource(R.drawable.cloth_5)
                            txCloth = "자켓, 가디건, 야상, 청바지, 면바지"
                        }else if(temp>=9){
                            view.image_cloth.setImageResource(R.drawable.cloth_6)
                            txCloth = "자켓, 트렌치코트, 야상, 니트, 청바지"
                        }else if(temp>=5){
                            view.image_cloth.setImageResource(R.drawable.cloth_7)
                            txCloth = "코트, 가죽자켓, 히트텍, 니트, 레깅스"
                        }else {
                            view.image_cloth.setImageResource(R.drawable.cloth_8)
                            txCloth = "패딩, 두꺼운 코트, 목도리, 기모제품"
                        }

                        // 습도
                        spannable = SpannableStringBuilder("오늘은 을(를) 시도해보는 건 어떨까요?")
                        spannable.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            3, // start
                            4, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        spannable.insert(4, txCloth)
                        view.tv_cloth.text = spannable
                    }
                }

                override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                    t.message?.let { Log.d("api fail : ", it) }
                }
            })


        }


        return view
    }

    fun timeChange(time: String?): String {
        // 현재 시간에 따라 데이터 시간 설정(3시간 마다 업데이트) //
        /**
         * 시간은 3시간 단위로 조회해야 한다. 안그러면 정보가 없다고 뜬다.
         * 0200, 0500, 0800 ~ 2300까지
         * 그래서 시간을 입력했을때 switch문으로 조회 가능한 시간대로 변경해주었다.
         */
        var time = time
        time = when (time) {
            "0200", "0300", "0400" -> "0200"
            "0500", "0600", "0700" -> "0500"
            "0800", "0900", "1000" -> "0800"
            "1100", "1200", "1300" -> "1100"
            "1400", "1500", "1600" -> "1400"
            "1700", "1800", "1900" -> "1700"
            "2000", "2100", "2200" -> "2000"
            else -> "2300"
        }
        return time
    }
}




