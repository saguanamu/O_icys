package com.example.oicys

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class BookActivity : AppCompatActivity() {

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


    private var mDatabaseRef: DatabaseReference? = null
    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        mDatabaseRef = FirebaseDatabase.getInstance().reference.child("lockers")
        mDatabase = FirebaseDatabase.getInstance().reference

        // 사물함 버튼
        var reservArray = arrayOf(reserv_1, reserv_2,reserv_3, reserv_4)

        // 사물함 예약 상태 받아와서 알려주는 코드 필요 (가장 최근의 로그)



        // 사용자 불러오기
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
        }

        val cal = Calendar.getInstance()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val date:Date = df.parse("2021-08-19T12:30:30+0530")
        Toast.makeText(this@BookActivity, "테스트", Toast.LENGTH_SHORT).show()
        // 예약 가능한 사물함에 한하여 클릭 가능하게
        for(i in 0 until 4){ // 버튼 작업 부여
            reservArray[i].setOnClickListener(){
                // 새 창 떠서 비밀번호, 날짜 받아오는 코드

                infoSave(i.toString(), "1234", date, "delivery")
                Toast.makeText(this@BookActivity, "버튼 생성", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun infoSave(num:String?, pw:String?, start:Date?, stat:String?){
        val cal = Calendar.getInstance()

        val user = Firebase.auth.currentUser

        if (user != null) {

            lockerPW = pw
            lockerUser = user.uid
            lockerStart = start
            lockerStat = stat

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
val thisTime = null
        val lockDB = LockerDB(
            lockerNum,
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
               Toast.makeText(this@BookActivity, "예약을 완료했습니다.", Toast.LENGTH_SHORT).show()


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
                    this@BookActivity,
                    "저장을 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            })


    }
}