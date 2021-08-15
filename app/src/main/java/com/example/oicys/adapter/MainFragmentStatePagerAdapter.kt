package com.example.oicys.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.oicys.fragment.*

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return HomeFragment()
            1 -> return PersonalFragment()
            2 -> return WeatherFragment()
            else -> return HomeFragment()
        }
    }
    override fun getCount(): Int = fragmentCount
}