package com.example.administrator.achi.dataModel

import android.util.Log
import java.lang.Exception
import java.time.LocalDateTime

const val TAG = "Analyzer"

private val THREE_MINUETS = 180
private val ONE_MINUTE = 60
private val NUMBER_OF_TEETH = 28


const val LESS : Int = 0
const val OKAY : Int = 1
const val MORE : Int = 2

object Analyzer{
    private lateinit var today : LocalDateTime
    private var duration : Int = -1
    private var sec_per_tooth : Array<Int> = Array<Int>(50,{0})
    private var section_time : Array<Int> = Array<Int>(6,{0})
    private var bad_pressure : Int = 0
    private var score : Int = 100
    private var comment : String = ""

    private const val numOfTeeth : Int = 28
    private var expected_sec_per_tooth : Int = 0

    var sumDiff : Array<Int> = Array<Int>(6, {0})
    var teethTimeComment : String = ""
    private val sectionName = arrayOf("위쪽 앞니", "위쪽 왼쪽 어금니", "위쪽 오른쪽 어금니",
                                        "아래쪽 앞니", "아래쪽 왼쪽 어금니", "아래쪽 오른쪽 어금니")

    var start : Long = 0
    var end : Long = 0
    var currentTooth : Int = 0


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

        return "$strMin:$strSecond"
    }

    // TODO Analyzer에서 각각 이빨 하나하나 시간 측정
//    fun secPerTooth(time : Int) {
//        sec_per_tooth[currentTooth] += time
//    }

    //////////////////////////////////////////////////////////////////////
    fun startToothTime(toothNum : Int) {
        if (currentTooth != 0) {
            endToothTime()
        }

        start = System.currentTimeMillis()
        currentTooth = toothNum
    }

    fun endToothTime() {
        end = System.currentTimeMillis()
        var durationTooth : Int = ((end - start) / 1000).toInt()

        sec_per_tooth[currentTooth] += durationTooth
    }
    ////////////////////////////////////////////////////////////////////////

    fun isDone(tooth:String) : Boolean {
        var num = 0
        try {
            num = tooth.toInt()
        } catch (e: Exception) {
            Log.d(TAG, "Failed to parse String $tooth to integer.")
        }

        return num >= NUMBER_OF_TEETH
    }

    fun isHalfWayDone(tooth: String) : Boolean {
        var num = 0
        try {
            num = tooth.toInt()
        } catch (e: Exception) {
            Log.d(TAG, "Failed to parse String $tooth to integer.")
        }

        return num >= (NUMBER_OF_TEETH / 2)
    }

    fun pressure() {
        bad_pressure++
    }

    fun analyze(date : LocalDateTime, time : Int) {
        today = date
        duration = time

        expected_sec_per_tooth = duration / numOfTeeth

        /* 인제 시간과 치아당 시간, bad_pressure을 기준으로
           점수 매기고 comment 저장 */

        // 총 양치 시간
        calculate_duration()

        // 각 이빨 계산
        calculate_sec_per_tooth()

        // 압력
        calculate_pressure()

        // comment 저장
        set_comment()


        if (score < 0)
            score = 0


        // record를 Record와 DataCenter에 저장
        var record = Record(today, duration, sec_per_tooth, section_time, bad_pressure, score, comment)
        DataCenter.records.add(0, record)
        DataCenter.printRecords()
        init()
    }

    // for sampleRecord
    fun analyzeSample(date : LocalDateTime, time : Int, spt : Array<Int>, bp : Int) {
        today = date
        duration = time
        sec_per_tooth = spt
        bad_pressure = bp

        expected_sec_per_tooth = duration / numOfTeeth

        calculate_duration()
        calculate_pressure()
        calculate_sec_per_tooth()
        set_comment()

        if (score < 0)
            score = 0

        // record를 Record와 DataCenter에 저장
        var record = Record(today, duration, sec_per_tooth, section_time, bad_pressure, score, comment)
        DataCenter.records.add(record)
        init()

    }

    private fun calculate_duration() {
        var minusScore : Int
        if (duration < 150) { // 2분 30초
            minusScore = (150 - duration)     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
        else if (duration > 210) {   // 3분 30초
            minusScore = (duration - 210)     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
    }

    private fun calculate_pressure() {
        score -= bad_pressure * 3
    }

    private fun calculate_sec_per_tooth() {
        for (i in 11..47) {
            // 위
            if (i in 11..13 || i in 21..23) {
                sumDiff[0] += sec_per_tooth[i] - expected_sec_per_tooth
            }
            else if (i in 14..17) {
                sumDiff[1] += sec_per_tooth[i] - expected_sec_per_tooth
            }
            else if (i in 24..27) {
                sumDiff[2] += sec_per_tooth[i] - expected_sec_per_tooth
            }

            // 아래
            else if (i in 31..33 || i in 41..43) {
                sumDiff[3] += sec_per_tooth[i] - expected_sec_per_tooth
            }
            else if (i in 34..37) {
                sumDiff[4] += sec_per_tooth[i] - expected_sec_per_tooth
            }
            else if (i in 44..47) {
                sumDiff[5] += sec_per_tooth[i] - expected_sec_per_tooth
            }
        }
        setCommentOfSection()

    }

    private fun setCommentOfSection() {
        var moreTeeth : String = ""
        var lessTeeth : String = ""

        var more : Boolean = false
        var less : Boolean = false

        for (i in 0..5) {
            if (sumDiff[i] > 6) {
                if (more)
                    moreTeeth += "와 "
                moreTeeth += sectionName[i]
                section_time[i] = MORE
                more = true
                score -= 5
            }
            else if (sumDiff[i] < -6) {
                if(less)
                    lessTeeth += "와 "
                lessTeeth += sectionName[i]
                section_time[i] = LESS
                less = true
                score -= 5
            }
            else
                section_time[i] = OKAY
        }

        if (more)
            moreTeeth += "를 오래 양치했습니다. "
        if (less)
            lessTeeth += "를 상대적으로 짧게 양치했습니다. "

        teethTimeComment = moreTeeth + lessTeeth
    }

    private fun set_comment() {
        if(duration in 150..210) {
            comment += "양치를 적절한 시간 동안 했습니다. "
        }
        else if (duration < 150) {
            comment += "양치 시간이 부족하네요! 다음부터는 조금 더 구석구석 닦아 보도록 해봐요. "
        }
        else
            comment += "양치를 너무 오래하셨네요. 너무 오래 양치하면 오히려 잇몸과 치아가 상할 수 있습니다. "

        comment += teethTimeComment

        if (bad_pressure >= 5)
            comment += "양치를 세게 하는 경향이 있습니다. 살살 양치하세요. "
    }

    private fun init() {
        today = LocalDateTime.now()
        duration = -1
        sec_per_tooth = Array<Int>(50,{0})
        section_time = Array<Int>(6,{0})
        bad_pressure = 0
        score = 100
        comment = ""
        teethTimeComment = ""
    }


}