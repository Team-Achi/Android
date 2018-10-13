package com.example.administrator.achi.fragment

import android.os.Handler
import android.os.SystemClock
import java.time.LocalDateTime

class Analyzer{

    private var today : LocalDateTime = LocalDateTime.now()
    private var score : Int = 100
    private var duration : Int = -1
    private var sec_per_tooth : Array<Int> = Array<Int>(50,{0})
    private var bad_pressure : Int = 0
    private var comment : String = "comment"

    private var numOfTeeth : Int = 28
    private var diff_per_tooth : Array<Int> = Array<Int>(50,{0})

    constructor(today : LocalDateTime) {
        this.today = today
    }

    /***
     * 필요한 함수
     * 1. 한 이빨 양치한 시간과 치아 번호 받아서 add
     * 2. bad pressure 횟수 더하는 함수
     * 3. 최종 분석하는 함수
     */

    fun secPerTooth(time : Int, tooth_num : Int) {
        this.sec_per_tooth[tooth_num] = time
    }

    fun pressure() {
        bad_pressure++
    }

    fun finalAnalysis(duration : Int) {
        this.duration = duration


        /* 인제 시간과 치아당 시간, bad_pressure을 기준으로
           점수 매기고 comment 저장 */
        // 총 양치 시간
        calculate_duration()

        // 각 이빨 계산
        calculate_sec_per_tooth()

        // 압력
        calculate_pressure()

        // comment 저장


        // record를 Record와 Day와 DataCenter에 저장
        var record = Record(today, score, duration, sec_per_tooth, bad_pressure, comment)
        DataCenter.addRecord(record)
        // TODO Day를 어떻게 알고 저장하지???

    }

    private fun calculate_duration() {
        var minusScore : Int
        if (duration < 170000) { // 2분 50초
            minusScore = (170000 - duration) / 1000     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
        else if (duration > 190000) {   // 3분 10초
            minusScore = (duration - 190000) / 1000     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
    }

    private fun calculate_sec_per_tooth() {
        var expected_sec_per_tooth : Int = duration / numOfTeeth

        for (i in 0..49) {
            diff_per_tooth[i] = sec_per_tooth[i] - expected_sec_per_tooth
        }
    }

    private fun calculate_pressure() {
        score -= bad_pressure * 5
        if (score < 0)
            score = 0
    }


}