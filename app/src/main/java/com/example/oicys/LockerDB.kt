package com.example.oicys

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class LockerDB {

        var lockerNum: String? = null // 사물함 번호
    var lockerPW: String? = null // 사물함 비밀번호 (초기 마스터 비밀번호: #*#12345678#*#)
        var lockerStat: String? = null // 사물함 상태 (대여(배달, 거래, 임시보관), 연체 등)
    var lockerUser: String? = null // 사물함 예약자
        var lockerStart: String? = null // 사물함 예약 시작
        var lockerEnd: String? = null // 사물함 반납 날짜
var lockerNow: String? =null // 현재 시각

        constructor() {}
        constructor(
            lockNB: Int?,
            lockPW: String?,
            lockSTT: String?,
            lockUS: String?,
            lockST: String?,
            lockED: String?,
            thisTm: String?
        ) {
            this.lockerNum = lockNB.toString()
            this.lockerPW = lockPW
            this.lockerStat = lockSTT
            this.lockerUser = lockUS
            this.lockerStart = lockST
            this.lockerEnd = lockED
            this.lockerNow = thisTm
        }

    constructor(lockerNum: Int?) {
        this.lockerNum = lockerNum.toString()
    }

    constructor(lockerPW: String?) {
        this.lockerPW = lockerPW
    }

    @Exclude
        fun toMap(): Map<String, Any?> {
            val result = HashMap<String, Any?>()
            result["lockerNumber"] = lockerNum
            result["lockerPassword"] = lockerPW
            result["lockerStatus"] = lockerStat
            result["lockerUser"] = lockerUser
            result["lockerStartDate"] = lockerStart
            result["lockerEndDate"] = lockerEnd
            result["lockerTime"] = lockerNow

            return result
        }

}