package com.example.administrator.achi.dataModel

import java.time.LocalDateTime

object Analyzer{

    private lateinit var today : LocalDateTime
    private var duration : Int = -1
    private var sec_per_tooth : Array<Int> = Array<Int>(50,{0})
    private var bad_pressure : Int = 0
    private var score : Int = 100
    private var comment : String = "comment"

    private const val numOfTeeth : Int = 28
    private var diff_time_per_tooth : Array<Int> = Array<Int>(50,{0})


    // 이 함수는 어디에다가 넣어야 좋을까,,,,
    fun timeToString(time : Int) : String {
        var min = time / 60
        var sec =  time % 60

        var strMin : String
        var strSecond : String

        if (min < 10)
            strMin = "0" + min.toString()
        else
            strMin = min.toString()

        if (sec < 10)
            strSecond = "0" + sec.toString()
        else
            strSecond = sec.toString()

        return "$strMin : $strSecond"
    }

    // TODO Analyzer에서 각각 이빨 하나하나 시간 측정
    fun secPerTooth(time : Int, tooth_num : Int) {      // 치아 하나하나 접근
        sec_per_tooth[tooth_num] = time
    }

    fun pressure() {
        bad_pressure++
    }


    // TODO 각 이빨 계산과 코멘트 저장
    fun analyze(date : LocalDateTime, time : Int) {
        today = date
        duration = time

        /* 인제 시간과 치아당 시간, bad_pressure을 기준으로
           점수 매기고 comment 저장 */

        // 총 양치 시간
        calculate_duration()

        // 각 이빨 계산
//        calculate_sec_per_tooth()

        // 압력
        calculate_pressure()

        // comment 저장
        set_comment()


        if (score < 0)
            score = 0


        // record를 Record와 DataCenter에 저장
        var record = Record(today, duration, sec_per_tooth, bad_pressure, score, comment)
        DataCenter.records.add(0, record)


        init()
    }

    private fun calculate_duration() {
        var minusScore : Int
        if (duration < 170) { // 2분 50초
            minusScore = (170 - duration)     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
        else if (duration > 190) {   // 3분 10초
            minusScore = (duration - 190)     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
    }

    private fun calculate_sec_per_tooth() {
        var expected_sec_per_tooth : Int = duration / numOfTeeth

        for (i in 0..sec_per_tooth.size - 1) {
            diff_time_per_tooth[i] = sec_per_tooth[i] - expected_sec_per_tooth
        }
    }

    private fun calculate_pressure() {
        score -= bad_pressure * 5
        if (score < 0)
            score = 0
    }

    private fun set_comment() {
        comment +=  today.toString()
    }

    private fun init() {
        today = LocalDateTime.now()
        duration = -1
        sec_per_tooth = Array<Int>(50,{0})
        bad_pressure = 0
        score = 100
        comment = "comment"
        diff_time_per_tooth = Array<Int>(50, {0})
    }


}