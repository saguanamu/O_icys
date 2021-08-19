package com.example.oicys.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.oicys.LockerDB
import com.example.oicys.MainActivity
import com.example.oicys.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.view.*
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
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // 전달될 정보 값
    var lockerNum: Int? = 0 // 사물함 번호
    var lockerPW: String? = null // 사물함 비밀번호 (초기 마스터 비밀번호: #*#12345678#*#)
    var lockerStat: String? = null // 사물함 상태 (대여(배달, 거래, 임시보관), 연체 등)
    var lockerUser: String? = null // 사물함 예약자
    var lockerStart: Date? = null // 사물함 예약 시작
    var lockerEnd: Date? = null // 사물함 반납 날짜

    // 불러올 정보 값
    var nowStat: String? = null // 사물함 상태 (대여(배달, 거래, 임시보관), 연체 등)

    // 사물함 상태 저장 (배달, 거래, 임시보관, 대여 가능, 연체)
    var statArray = arrayOf("delivery", "transaction", "store", "empty", "delay")
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    val df2: DateFormat = SimpleDateFormat("d일 HH시간 mm분")

    private var mDatabaseRef: DatabaseReference? = null
    private var mDatabase: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater!!.inflate(R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)


        mDatabaseRef = FirebaseDatabase.getInstance().reference.child("lockers")
        mDatabase = FirebaseDatabase.getInstance().reference

        // 사물함 버튼
        val reservArray = arrayOf(view.reserv_1, view.reserv_2,
            view.reserv_3, view.reserv_4)
        // 사물함 상태
        var sttArray = arrayOf("null","null","null","null")




        val cal = Calendar.getInstance()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val date: Date = df.parse("2021-08-19T12:30:30+0530")
        Toast.makeText(context, "테스트", Toast.LENGTH_SHORT).show()

        // 예약 가능한 사물함에 한하여 클릭 가능하게
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post : String? = dataSnapshot.child("lockerStatus").getValue(String::class.java)
                val num : String? = dataSnapshot.child("lockerNumber").getValue(String::class.java)

                val nm:Int?
                if (num != null) {
                    nm= parseInt(num)
                    if (post != null) {
                        sttArray[nm-1] = post
                        Log.i("TAG: value is ", post)
                        Log.i("TAG: nm is ", num)
                        // 사물함 적용
                        if(post == statArray[0]){ // 배달
                            reservArray[nm-1].setBackgroundResource(R.drawable.btshadow_non)
                            reservArray[nm-1].isEnabled = false
                            Log.i("TAG: this value is ", statArray[0])
                        } else if (post == statArray[1]){ // 거래
                            reservArray[nm-1].setBackgroundResource(R.drawable.btshadow_non)
                            reservArray[nm-1].isEnabled = false
                            Log.i("TAG: this value is ", statArray[1])
                        }else if (post == statArray[2]){ // 임시보관
                            reservArray[nm-1].setBackgroundResource(R.drawable.btshadow_non)
                            reservArray[nm-1].isEnabled = false
                            Log.i("TAG: this value is ", statArray[2])
                        }else {
                            Log.i("TAG: this value is ", "empty")
                        }
                    }
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG: ", "loadPost:onCancelled", databaseError.toException())
            }
        }

        // 사물함 상태 불러오기
        mDatabaseRef!!.child("1").addValueEventListener(postListener)
        mDatabaseRef!!.child("2").addValueEventListener(postListener)
        mDatabaseRef!!.child("3").addValueEventListener(postListener)
        mDatabaseRef!!.child("4").addValueEventListener(postListener)



        for(i in 0 until 4){ // 버튼 작업 부여
            reservArray[i].setOnClickListener(){
                // 새 창 떠서 비밀번호, 날짜 받아오는 코드

                val args = this.arguments
                val inputData = args?.get("pw")
                val reservData = args?.get("stat")
                infoSave(i+1, inputData.toString(), date, reservData.toString())
                Toast.makeText(context, "버튼 생성", Toast.LENGTH_SHORT).show()
            }
        }


        // 사용자 정보 업데이트



// 사용자 불러오기
        val user = Firebase.auth.currentUser

        if (user != null) {
            // User is signed in
            val userInfo1Listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 유저 예약 정보 불러오기

                    val user1 = Firebase.auth.currentUser
                    val uid = user?.uid
                    val post : String? = dataSnapshot.child("users/$uid/status").getValue(String::class.java)
                    val num : String? = dataSnapshot.child("users/$uid/lockerNumber").getValue(Long::class.java).toString()
                    val testDate : String? = dataSnapshot.child("lockers/1/lockerEndDate").getValue(String::class.java)

                    Log.i("TAG: ", uid+post+num+testDate+"go")
                    val nm:Int?
                    if (num != null && num != "null") {
                        nm= parseInt(num)
                        if (post != null) {
                            view.tv_userInfo.text = "현재 "+num+"번 사물함을 이용 중입니다."

                            // 남은 시간 안내
                            val endDate : String? = dataSnapshot.child("lockers/$nm/lockerEndDate").getValue(String::class.java)
                            val endDate2: Date? = df.parse(endDate)
                            val now: Long = System.currentTimeMillis()
                            val date = Date(now)

                            val cal = endDate2?.time?.minus(date?.time)
                            val calInfo = df2.format(cal)

                            val spannable = SpannableStringBuilder("대여 만료까지  남았습니다.")
                            spannable.setSpan(
                                ForegroundColorSpan(Color.BLUE),
                                7, // start
                                8, // end
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                            spannable.insert(8, calInfo)

                            view.tv_timeInfo.text = spannable

                            Log.i("TAG: ", "success")

                            // 예약된 정보가 있으므로, 나머지 사물함 예약 불가
                            // 사물함에 적용
                            for(i in 0 until 4){ // 예약 상태에 따라 스타일 변화
                                if(nm-1 == i){ // 예약된 사물함
                                    reservArray[i].setBackgroundResource(R.drawable.btshadow_select)
                                    reservArray[i].isEnabled = false
                                } else {
                                    reservArray[i].setBackgroundResource(R.drawable.btshadow_non)
                                    reservArray[i].isEnabled = false
                                }
                            }
                        }
                        Log.i("TAG: ", "정보 없음")
                    }
                    Log.i("TAG: ", "num 없음")
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG: ", "loadPost:onCancelled", databaseError.toException())
                }
            }
            mDatabase!!.addValueEventListener(userInfo1Listener)
        } else {
            // No user is signed in
            Log.i("TAG: ", "no user")
        }


        // 새로고침 버튼
        view.bt_refresh.setOnClickListener {

            val intent = Intent(context,MainActivity::class.java)

            intent.apply {
                this.putExtra("status","refresh") // 데이터 넣기
                this.putExtra("fragment",2) // 데이터 넣기
            }

            startActivity(intent)

        }



        return view

    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft2: FragmentTransaction = fragmentManager.beginTransaction()
        // ft2.detach(fragment).attach(fragment).commit()


        // fragment reload
        ft2 = childFragmentManager.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft2.setReorderingAllowed(false)
        }
        ft2.detach(fragment).attach(fragment).commit()

    }



    fun infoSave(num:Int?, pw:String?, start: Date?, stat:String?){
        val cal = Calendar.getInstance()
        val user = Firebase.auth.currentUser


        if (user != null) {

            lockerPW = pw
            lockerUser = user.uid
            lockerStart = start
            lockerStat = stat
            lockerNum = num

            // time
            cal.time = lockerStart
            when(lockerStat){
                statArray[0]-> cal.add(Calendar.HOUR_OF_DAY, 2)
                statArray[1]-> cal.add(Calendar.HOUR_OF_DAY, 24)
                statArray[2]-> cal.add(Calendar.HOUR_OF_DAY, 72)
                else -> false
            }
            lockerEnd = cal.time

            // 정보 업데이트 (로그를 추가하는 방식으로)
            writeNewLog(lockerNum, lockerPW, lockerStat, lockerUser, lockerStart, lockerEnd)
        } else {
            // No user is signed in
        }
    }

    fun writeNewLog(lockNB: Int?, lockPW: String?, lockSTT: String?,
                    lockUS: String?, lockST: Date?, lockED: Date?){

        // String key = mDatabase.child("rooms").push().getKey();
        val stringStart = df.format(lockST)
        val stringEnd = df.format(lockED)
// 현재시간을 가져오기
        val now: Long = System.currentTimeMillis()
// 현재 시간을 Date 타입으로 변환
        val date = Date(now)
// 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val thisTime:String? = df.format(date)

        val lockDB = LockerDB(
            lockNB,
            lockerPW,
            lockerStat,
            lockerUser,
            stringStart,
            stringEnd,
            thisTime
        )
        val lockValues: Map<String, Any?> = lockDB.toMap()
        val childUpdates: MutableMap<String, Any?> = HashMap()

        childUpdates["/lockers/$lockerNum"] = lockValues

        mDatabase?.updateChildren(childUpdates)
            ?.addOnSuccessListener(OnSuccessListener<Void?> {
                Toast.makeText(context, "예약을 완료했습니다.", Toast.LENGTH_SHORT).show()


                // 파이어베이스에 예약한 사용자 정보 추가
                val database = FirebaseDatabase.getInstance()
                val user = FirebaseAuth.getInstance().currentUser // 로그인한 유저 정보 가져오기
                val uid = user?.uid // 로그인한 유저의 고유 uid 가져오기
                val mDatabaseRef =
                    database.getReference("/users/$uid") // 유저 찾기



                mDatabaseRef.child("status").setValue(lockerStat)
                mDatabaseRef.child("lockerNumber").setValue(lockerNum)



            })
            ?.addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    context,
                    "저장을 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
