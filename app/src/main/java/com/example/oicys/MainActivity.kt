package com.example.oicys

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.oicys.adapter.MainFragmentStatePagerAdapter
import com.example.oicys.databinding.ActivityMainBinding
import com.example.oicys.fragment.ReservationFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) // status hide
        // bottomnavi
        configureBottomNavigation()

        // 프래그먼트 전환
        supportFragmentManager.beginTransaction().replace(R.id.main, ReservationFragment()).commit()
    }

    private fun configureBottomNavigation(){
        vp_ac_main_frag_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 3) // 연결한 프래그먼트 갯수
        tl_ac_main_bottom_menu.setupWithViewPager(vp_ac_main_frag_pager)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        tl_ac_main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_weather_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_peronsal_tab) as RelativeLayout
    }


}