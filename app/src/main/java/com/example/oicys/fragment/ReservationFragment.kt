package com.example.oicys.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.oicys.R
import com.example.oicys.adapter.SendMessage
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_reserv.*

class ReservationFragment : Fragment() {
    private lateinit var sm : SendMessage
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserv, container, false)
        val btn : Button = view.findViewById(R.id.save)
        btn.setOnClickListener {
            // 사용자 이름
            val editText1 : EditText = view.findViewById(R.id.editText1)
            val usrname = editText1.setText("파라송")

            // 비번
            val editText2 : EditText = view.findViewById(R.id.editText2)
            val pwd = editText2.text.toString()

            // 물품 선택
            val reservId = radio_group.checkedRadioButtonId // 아이디값
            val reservString = resources.getResourceEntryName(reservId) // 내용

            radio_group.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                //익명객체를 사용
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        R.id.status_delivery -> reservString.toString()
                        R.id.status_sell -> reservString.toString()
                        R.id.status_tempo -> reservString.toString()
                        //상수를 통해 View의 id를 지칭할때는 이 포맷을 사용
                    }
                }
            })
            // 번들
            val bundle = Bundle()
            bundle.putString("pw", pwd)
            bundle.putString("stat", reservString)

            val fragment = HomeFragment()
            fragment.arguments = bundle
            fragmentManager?.beginTransaction()?.replace(R.id.main, fragment)?.commit()
        }
        return view
    }
}
